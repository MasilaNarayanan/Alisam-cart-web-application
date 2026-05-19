package com.example.Alisam_Codes.controllers;

import com.example.Alisam_Codes.models.Customization;
import com.example.Alisam_Codes.models.Order;
import com.example.Alisam_Codes.models.OrderItem;
import com.example.Alisam_Codes.models.Product;
import com.example.Alisam_Codes.models.User;
import com.example.Alisam_Codes.repositories.CustomizationRepository;
import com.example.Alisam_Codes.services.OrderService;
import com.example.Alisam_Codes.services.ProductService;
import com.example.Alisam_Codes.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;

import org.json.JSONObject;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

@Controller
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomizationRepository customizationRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = getCart(session);
        model.addAttribute("cart", cart);
        double total = cart.stream().mapToDouble(item -> item.getProduct().getPrice().doubleValue() * item.getQuantity()).sum();
        model.addAttribute("total", total);
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId, 
                            @RequestParam Integer quantity,
                            @RequestParam(required = false) String size,
                            @RequestParam(required = false) String paper,
                            @RequestParam(required = false) String text,
                            @RequestParam(required = false) String color,
                            @RequestParam(required = false) String customImageUrl,
                            @RequestParam(required = false) String canvaDesignId,
                            HttpSession session) {
        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isPresent()) {
            List<CartItem> cart = getCart(session);
            cart.add(new CartItem(productOpt.get(), quantity, size, paper, text, color, customImageUrl, canvaDesignId));
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }
    
    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam int index, HttpSession session) {
        List<CartItem> cart = getCart(session);
        if (index >= 0 && index < cart.size()) {
            cart.remove(index);
        }
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    @ResponseBody
    public ResponseEntity<?> updateCartQuantity(@RequestParam("index") int index, @RequestParam("quantity") int quantity, HttpSession session) {
        List<CartItem> cart = getCart(session);
        if (index >= 0 && index < cart.size() && quantity > 0) {
            cart.get(index).setQuantity(quantity);
            session.setAttribute("cart", cart);
            
            // Recalculate totals
            double itemTotal = cart.get(index).getProduct().getPrice().doubleValue() * quantity;
            double subtotal = cart.stream().mapToDouble(item -> item.getProduct().getPrice().doubleValue() * item.getQuantity()).sum();
            double tax = subtotal * 0.18;
            double finalTotal = subtotal + tax;
            
            JSONObject response = new JSONObject();
            response.put("success", true);
            response.put("itemTotal", String.format("%.2f", itemTotal));
            response.put("subtotal", String.format("%.2f", subtotal));
            response.put("tax", String.format("%.2f", tax));
            response.put("finalTotal", String.format("%.2f", finalTotal));
            
            return ResponseEntity.ok(response.toString());
        }
        return ResponseEntity.badRequest().body("{\"success\":false}");
    }

    @GetMapping("/checkout")
    public String showCheckout(HttpSession session, Model model) {
        List<CartItem> cart = getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }
        
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Redirect to login if no user
        }

        double total = cart.stream().mapToDouble(item -> item.getProduct().getPrice().doubleValue() * item.getQuantity()).sum();
        double totalWithShipping = total + 29; // ₹29 Basic shipping

        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int)(totalWithShipping * 100)); // amount in the smallest currency unit
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

            com.razorpay.Order razorpayOrder = razorpay.orders.create(orderRequest);
            model.addAttribute("razorpayOrderId", razorpayOrder.get("id"));
            model.addAttribute("razorpayKey", razorpayKeyId);
            model.addAttribute("amount", totalWithShipping);
            model.addAttribute("user", loggedInUser);
        } catch (RazorpayException e) {
            e.printStackTrace();
            return "redirect:/cart?error=payment_gateway_down";
        }

        model.addAttribute("total", total);
        model.addAttribute("cart", cart);
        model.addAttribute("pickupPincode", "641014"); // Singanallur
        return "checkout";
    }

    @PostMapping("/cart/verify-payment")
    @ResponseBody
    public ResponseEntity<String> verifyPayment(@RequestParam("razorpay_payment_id") String paymentId,
                                                @RequestParam("razorpay_order_id") String razorpayOrderId,
                                                @RequestParam("razorpay_signature") String signature,
                                                HttpSession session) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_signature", signature);

            boolean status = Utils.verifyPaymentSignature(options, razorpayKeySecret);
            if (status) {
                // Payment Verified. Create local order
                List<CartItem> cart = getCart(session);
                User user = (User) session.getAttribute("loggedInUser");

                if (cart.isEmpty() || user == null) {
                    return ResponseEntity.status(400).body("Session expired");
                }

                double total = cart.stream().mapToDouble(item -> item.getProduct().getPrice().doubleValue() * item.getQuantity()).sum();
                double totalWithShipping = total + 29;

                com.example.Alisam_Codes.models.Order order = new com.example.Alisam_Codes.models.Order();
                order.setUser(user);
                order.setStatus("PROCESSING");
                order.setRazorpayOrderId(razorpayOrderId);
                order.setTotalAmount(BigDecimal.valueOf(totalWithShipping));
                order.setCustomerName(user.getName());
                
                List<OrderItem> orderItems = new ArrayList<>();
                for (CartItem item : cart) {
                    OrderItem orderItem = new OrderItem(
                        order, 
                        item.getProduct(), 
                        item.getQuantity(), 
                        item.getProduct().getPrice(), 
                        item.getCustomImageUrl(), 
                        item.getText(), 
                        item.getCanvaDesignId() 
                    );
                    orderItems.add(orderItem);

                    Customization customization = new Customization(
                        item.getProduct(),
                        item.getSize(),
                        item.getPaper(),
                        item.getQuantity(),
                        item.getText(),
                        item.getColor()
                    );
                    customizationRepository.save(customization);
                }
                
                order.setItems(orderItems);
                orderService.createOrder(order);
                
                // Clear cart after checkout
                session.removeAttribute("cart");
                return ResponseEntity.ok("Success");
            } else {
                return ResponseEntity.status(400).body("Payment Verification Failed");
            }
        } catch (RazorpayException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing payment");
        }
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }
        return cart;
    }

    // Helper inner class for session cart
    public static class CartItem implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private Product product;
        private Integer quantity;
        private String size;
        private String paper;
        private String text;
        private String color;
        private String customImageUrl;
        private String canvaDesignId;

        public CartItem(Product product, Integer quantity, String size, String paper, String text, String color, String customImageUrl, String canvaDesignId) {
            this.product = product;
            this.quantity = quantity;
            this.size = size;
            this.paper = paper;
            this.text = text;
            this.color = color;
            this.customImageUrl = customImageUrl;
            this.canvaDesignId = canvaDesignId;
        }

        public Product getProduct() { return product; }
        public void setProduct(Product product) { this.product = product; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }
        public String getPaper() { return paper; }
        public void setPaper(String paper) { this.paper = paper; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public String getCustomImageUrl() { return customImageUrl; }
        public void setCustomImageUrl(String customImageUrl) { this.customImageUrl = customImageUrl; }
        public String getCanvaDesignId() { return canvaDesignId; }
        public void setCanvaDesignId(String canvaDesignId) { this.canvaDesignId = canvaDesignId; }
    }
}
