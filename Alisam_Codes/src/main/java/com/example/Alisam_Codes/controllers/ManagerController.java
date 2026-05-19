package com.example.Alisam_Codes.controllers;

import com.example.Alisam_Codes.models.Product;
import com.example.Alisam_Codes.models.User;
import com.example.Alisam_Codes.services.ProductService;
import com.example.Alisam_Codes.repositories.CategoryRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PathVariable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.io.IOException;

@Controller
public class ManagerController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/manager/dashboard")
    public String managerDashboard(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || (!loggedInUser.getRole().equals("MANAGER") && !loggedInUser.getRole().equals("ADMIN"))) {
            return "redirect:/login"; // Restrict to managers (admin fallback for testing)
        }
        
        // Load manager's specific products
        model.addAttribute("products", productService.getProductsByManagerId(loggedInUser.getId()));
        return "manager/dashboard"; // Render manager view (My Products)
    }

    @GetMapping("/manager/all-products")
    public String managerAllProducts(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || (!loggedInUser.getRole().equals("MANAGER") && !loggedInUser.getRole().equals("ADMIN"))) {
            return "redirect:/login";
        }
        
        // Load ALL products so manager can update any
        model.addAttribute("products", productService.getAllProducts());
        return "manager/all-products"; 
    }

    @GetMapping("/manager/products/new")
    public String showAddProductForm(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || (!loggedInUser.getRole().equals("MANAGER") && !loggedInUser.getRole().equals("ADMIN"))) {
            return "redirect:/login";
        }
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        return "manager/product-form";
    }

    @PostMapping("/manager/products/add")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam(value = "modelFile", required = false) MultipartFile modelFile,
                             HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        
        String newPath = saveModelFile(modelFile);
        if (newPath != null) {
            product.setModelGlbPath(newPath);
        }
        
        // Link product to manager
        product.setAddedByManagerId(loggedInUser.getId());
        productService.saveProduct(product);
        
        return "redirect:/manager/dashboard";
    }

    @GetMapping("/manager/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            Product product = productService.getProductById(id).orElse(null);
            // Delete only if added by them or if they are admin
            if (product != null && (product.getAddedByManagerId().equals(loggedInUser.getId()) || loggedInUser.getRole().equals("ADMIN"))) {
                productService.deleteProduct(id);
            }
        }
        return "redirect:/manager/dashboard";
    }

    @GetMapping("/manager/products/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || (!loggedInUser.getRole().equals("MANAGER") && !loggedInUser.getRole().equals("ADMIN"))) {
            return "redirect:/login";
        }
        Product product = productService.getProductById(id).orElse(null);
        if (product == null) {
            return "redirect:/manager/dashboard";
        }
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        return "manager/product-form";
    }

    @PostMapping("/manager/products/edit/{id}")
    public String updateProduct(@PathVariable Long id, 
                                @ModelAttribute Product product,
                                @RequestParam(value = "modelFile", required = false) MultipartFile modelFile,
                                HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        
        Product existingProduct = productService.getProductById(id).orElse(null);
        if (existingProduct != null) {
            String newPath = saveModelFile(modelFile);
            if (newPath != null) {
                product.setModelGlbPath(newPath);
            } else {
                product.setModelGlbPath(existingProduct.getModelGlbPath());
            }
            // Retain original addedByManagerId
            product.setAddedByManagerId(existingProduct.getAddedByManagerId());
        }
        
        product.setId(id);
        productService.saveProduct(product);
        return "redirect:/manager/all-products";
    }

    private String saveModelFile(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replace(" ", "_");
                Path path = Paths.get("src/main/resources/static/models/" + fileName);
                Files.createDirectories(path.getParent());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                
                Path targetPath = Paths.get("target/classes/static/models/" + fileName);
                Files.createDirectories(targetPath.getParent());
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                return "/models/" + fileName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
