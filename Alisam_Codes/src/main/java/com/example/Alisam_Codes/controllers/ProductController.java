package com.example.Alisam_Codes.controllers;

import com.example.Alisam_Codes.models.Product;
import com.example.Alisam_Codes.services.CategoryService;
import com.example.Alisam_Codes.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;

import com.example.Alisam_Codes.models.Review;
import com.example.Alisam_Codes.models.User;
import com.example.Alisam_Codes.services.ReviewService;
import com.example.Alisam_Codes.services.UserService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.io.IOException;
import java.util.Optional;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/products")
    public String listProducts(@RequestParam(required = false) Long categoryId, Model model) {
        if (categoryId != null) {
            model.addAttribute("products", productService.getProductsByCategory(categoryId));
        } else {
            model.addAttribute("products", productService.getAllProducts());
        }
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products";
    }

    @GetMapping("/products/{id}")
    public String getProductDetails(@PathVariable Long id, Model model) {
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isPresent()) {
            model.addAttribute("product", productOpt.get());
            model.addAttribute("reviews", reviewService.getReviewsByProductId(id));
            return "product-details";
        }
        return "redirect:/products";
    }

    @PostMapping("/products/{id}/review")
    public String submitReview(@PathVariable Long id, 
                               @RequestParam("rating") int rating, 
                               @RequestParam(value = "reviewText", required = false) String reviewText,
                               @RequestParam(value = "photo", required = false) MultipartFile photo,
                               HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Must be logged in to review
        }
        
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            Review review = new Review();
            review.setProduct(product);
            review.setUser(loggedInUser);
            review.setRating(rating);
            review.setReviewText(reviewText);

            // Handle photo upload
            if (photo != null && !photo.isEmpty()) {
                try {
                    String fileName = UUID.randomUUID().toString() + "_" + photo.getOriginalFilename().replace(" ", "_");
                    Path path = Paths.get("src/main/resources/static/uploads/reviews/" + fileName);
                    Files.createDirectories(path.getParent());
                    Files.copy(photo.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    
                    Path targetPath = Paths.get("target/classes/static/uploads/reviews/" + fileName);
                    Files.createDirectories(targetPath.getParent());
                    Files.copy(photo.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    
                    review.setPhotoUrl("/uploads/reviews/" + fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            reviewService.saveReview(review); // Coins logic is inside here
        }
        
        return "redirect:/products/" + id;
    }

    @GetMapping("/api/products/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProductApi(@PathVariable Long id) {
        Optional<Product> productOpt = productService.getProductById(id);
        return productOpt.map(ResponseEntity::ok)
                         .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
