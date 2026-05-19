package com.example.Alisam_Codes.controllers;

import com.example.Alisam_Codes.services.OrderService;
import com.example.Alisam_Codes.services.ProductService;
import com.example.Alisam_Codes.services.UserService;
import com.example.Alisam_Codes.repositories.CustomizationRepository;
import com.example.Alisam_Codes.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CustomizationRepository customizationRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/admin")
    public String dashboard(Model model) {
        // Fetch all orders using mapping or service method
        // For demonstration, fetch all or specific if user is admin
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/dashboard";
    }

    @org.springframework.web.bind.annotation.PostMapping("/admin/orders/update")
    public String updateOrderDetails(@org.springframework.web.bind.annotation.RequestParam("id") Long id,
                                     @org.springframework.web.bind.annotation.RequestParam(value = "status", required = false) String status,
                                     @org.springframework.web.bind.annotation.RequestParam(value = "trackingId", required = false) String trackingId,
                                     @org.springframework.web.bind.annotation.RequestParam(value = "shiprocketOrderId", required = false) String shiprocketOrderId) {
        orderService.updateOrderDetails(id, status, trackingId, shiprocketOrderId);
        return "redirect:/admin";
    }

    // --- Product Management ---

    @GetMapping("/admin/products")
    public String manageProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }

    @GetMapping("/admin/managers")
    public String manageManagers(Model model) {
        model.addAttribute("managers", userService.findByRole("MANAGER"));
        return "admin/managers";
    }

    @GetMapping("/admin/managers/new")
    public String showAddManagerForm(Model model) {
        model.addAttribute("user", new com.example.Alisam_Codes.models.User());
        return "admin/manager-form";
    }

    @org.springframework.web.bind.annotation.PostMapping("/admin/managers/add")
    public String addManager(@org.springframework.web.bind.annotation.ModelAttribute com.example.Alisam_Codes.models.User user, Model model) {
        // Check if email already exists
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already exists!");
            return "admin/manager-form";
        }
        user.setRole("MANAGER");
        userService.registerUser(user);
        return "redirect:/admin/managers";
    }

    @GetMapping("/admin/products/new")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new com.example.Alisam_Codes.models.Product());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/product-form";
    }

    @org.springframework.web.bind.annotation.PostMapping("/admin/products/add")
    public String addProduct(@org.springframework.web.bind.annotation.ModelAttribute com.example.Alisam_Codes.models.Product product,
                             @org.springframework.web.bind.annotation.RequestParam(value = "modelFile", required = false) org.springframework.web.multipart.MultipartFile modelFile) {
        String newPath = saveModelFile(modelFile);
        if (newPath != null) {
            product.setModelGlbPath(newPath);
        }
        productService.saveProduct(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/admin/products/delete/{id}")
    public String deleteProduct(@org.springframework.web.bind.annotation.PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/admin/products/edit/{id}")
    public String showEditProductForm(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        com.example.Alisam_Codes.models.Product product = productService.getProductById(id).orElse(null);
        if (product == null) {
            return "redirect:/admin/products";
        }
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/product-form";
    }

    @org.springframework.web.bind.annotation.PostMapping("/admin/products/edit/{id}")
    public String updateProduct(@org.springframework.web.bind.annotation.PathVariable Long id, 
                                @org.springframework.web.bind.annotation.ModelAttribute com.example.Alisam_Codes.models.Product product,
                                @org.springframework.web.bind.annotation.RequestParam(value = "modelFile", required = false) org.springframework.web.multipart.MultipartFile modelFile) {
        com.example.Alisam_Codes.models.Product existingProduct = productService.getProductById(id).orElse(null);
        if (existingProduct != null) {
            String newPath = saveModelFile(modelFile);
            if (newPath != null) {
                product.setModelGlbPath(newPath);
            } else {
                product.setModelGlbPath(existingProduct.getModelGlbPath());
            }
        }
        
        product.setId(id); // Ensure the correct ID is retained for update
        productService.saveProduct(product);
        return "redirect:/admin/products";
    }

    private String saveModelFile(org.springframework.web.multipart.MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                String fileName = java.util.UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replace(" ", "_");
                
                // Save to static/models directory in src tree so it persists across rebuilds
                java.nio.file.Path path = java.nio.file.Paths.get("src/main/resources/static/models/" + fileName);
                java.nio.file.Files.createDirectories(path.getParent());
                java.nio.file.Files.copy(file.getInputStream(), path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                // Also save to target so it's instantly available without a server rebuild
                java.nio.file.Path targetPath = java.nio.file.Paths.get("target/classes/static/models/" + fileName);
                java.nio.file.Files.createDirectories(targetPath.getParent());
                java.nio.file.Files.copy(file.getInputStream(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                return "/models/" + fileName;
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
