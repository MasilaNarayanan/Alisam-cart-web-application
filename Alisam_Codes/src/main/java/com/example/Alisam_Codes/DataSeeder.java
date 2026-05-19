package com.example.Alisam_Codes;

import com.example.Alisam_Codes.models.Category;
import com.example.Alisam_Codes.models.Product;
import com.example.Alisam_Codes.models.User;
import com.example.Alisam_Codes.repositories.CategoryRepository;
import com.example.Alisam_Codes.repositories.ProductRepository;
import com.example.Alisam_Codes.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
        seedCategories();
        seedProducts();
    }

    private void seedUsers() {
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User admin = new User("Admin User", "admin@example.com", "admin123", "ADMIN");
            userRepository.save(admin);
            System.out.println("Default Admin seeded: admin@example.com");
        }
        
        if (userRepository.findByEmail("user@example.com").isEmpty()) {
            User customer = new User("Regular User", "user@example.com", "user123", "USER");
            userRepository.save(customer);
            System.out.println("Default User seeded: user@example.com");
        }
    }

    private void seedCategories() {
        if (categoryRepository.count() == 0) {
            String[] categories = {
                "Mugs & Drinkware", 
                "T-Shirts & Apparel", 
                "Mobile Covers", 
                "Canvas & Photo Gifts",
                "Home & Decor",
                "Stationery & Corporate Gifts"
            };

            for (String categoryName : categories) {
                Category category = new Category();
                category.setName(categoryName);
                categoryRepository.save(category);
            }
            System.out.println("Default categories seeded");
        }
    }

    private void seedProducts() {
        productRepository.deleteAll(); // Force refresh models
        
        if (productRepository.count() == 0) {
            java.util.List<Category> allCategories = categoryRepository.findAll();
            
            for (Category category : allCategories) {
                String catName = category.getName();
                
                if (catName.contains("Mugs")) {
                    addProduct(category, "Personalized White Ceramic Mug", "High-quality 11oz white ceramic mug personalized with your photos or text. Perfect for gifting.", "299.00", "https://images.unsplash.com/photo-1514228742587-6b1558fcca3d?q=80&w=600", "https://modelviewer.dev/shared-assets/models/Chair.glb", "DAGP6R4-K-4");
                    addProduct(category, "Color Changing Magic Mug", "Pour hot liquid and watch your hidden photo magically appear!", "399.00", "https://img.freepik.com/premium-psd/black-magic-mug-mockup_1332-13203.jpg?w=826", "https://modelviewer.dev/shared-assets/models/NeilArmstrong.glb", "DAGKxTtRf9o");
                    addProduct(category, "Custom Sipper Bottle", "750ml Aluminium Sipper Bottle with full wrap printing.", "449.00", "https://images.unsplash.com/photo-1602143407151-7111542de6e8?q=80&w=600", "https://modelviewer.dev/shared-assets/models/Astronaut.glb", "DAF5wH1R_2I");
                } 
                else if (catName.contains("Apparel")) {
                    addProduct(category, "Premium Cotton Custom T-Shirt", "100% Cotton, bio-washed, soft and comfortable. Print your own design.", "499.00", "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?q=80&w=600", "https://modelviewer.dev/shared-assets/models/RobotExpressive.glb", "DAF45A_wOa0");
                    addProduct(category, "Winter Warm Custom Hoodie", "Stay warm and stylish with custom printed hoodies. Rich cotton fleece.", "899.00", "https://images.unsplash.com/photo-1556821840-3a63f95609a7?q=80&w=600", "https://modelviewer.dev/shared-assets/models/shishkebab.glb", null);
                    addProduct(category, "Polo Collar Corporate T-Shirt", "Premium Matty Polo T-Shirt with left chest logo print.", "599.00", "https://images.unsplash.com/photo-1581655353564-df123a1eb820?q=80&w=600", "https://modelviewer.dev/shared-assets/models/RobotExpressive.glb", null);
                }
                else if (catName.contains("Mobile")) {
                    addProduct(category, "Custom Hard Phone Cover", "Polycarbonate hard case with edge-to-edge printing. Available for 500+ models.", "249.00", "https://images.unsplash.com/photo-1572635196237-14b3f281503f?q=80&w=600", "https://modelviewer.dev/shared-assets/models/Astronaut.glb", "DAGD6iRz03g");
                    addProduct(category, "Premium Glass Phone Case", "Shockproof TPU edges with a tempered glass back featuring vibrant prints.", "399.00", "https://images.unsplash.com/photo-1601593346740-925612772716?q=80&w=600", "https://modelviewer.dev/shared-assets/models/Chair.glb", "DAGD6iRz03g");
                }
                else if (catName.contains("Photo Gifts") || catName.contains("Canvas")) {
                    addProduct(category, "Custom Canvas Wall Print", "Gallery-wrapped canvas print of your favorite memories. Multiple sizes available.", "699.00", "https://images.unsplash.com/photo-1513519245088-0e12902e5a38?q=80&w=600", "https://modelviewer.dev/shared-assets/models/shishkebab.glb", "DAGP2zHk9Uo");
                    addProduct(category, "Acrylic Photo Block", "Free-standing 3D acrylic photo block. Perfect for desks and shelves.", "499.00", "https://images.unsplash.com/photo-1583847268964-b28ec8fce09f?q=80&w=600", "https://modelviewer.dev/shared-assets/models/NeilArmstrong.glb", "DAFx9H-_vU0");
                }
                else if (catName.contains("Decor")) {
                    addProduct(category, "Personalized Magic Cushion", "Sequin magic cushion where your hidden photo reveals upon swiping.", "449.00", "https://images.unsplash.com/photo-1584100936553-558223d6a2f8?q=80&w=600", "https://modelviewer.dev/shared-assets/models/Chair.glb", "DAGD5R4_Jk0");
                    addProduct(category, "LED Name Lamp", "Warm-white LED lamp customized with your name and photo contour.", "599.00", "https://images.unsplash.com/photo-1563821731633-8ec2e9eec0eb?q=80&w=600", "https://modelviewer.dev/shared-assets/models/Astronaut.glb", null);
                }
                else if (catName.contains("Stationery") || catName.contains("Corporate")) {
                    addProduct(category, "Personalized Mouse Pad", "Anti-slip custom printed mousepad for your office or gaming setup.", "199.00", "https://images.unsplash.com/photo-1610465299996-30f240ac2b1c?q=80&w=600", "https://modelviewer.dev/shared-assets/models/RobotExpressive.glb", "DAGD6iRz03g");
                    addProduct(category, "Engraved Metal Pen", "Premium metal rollerball pen with permanent laser-engraved name.", "249.00", "https://images.unsplash.com/photo-1585336261022-680e284a37fc?q=80&w=600", "https://modelviewer.dev/shared-assets/models/NeilArmstrong.glb", null);
                }
            }

            System.out.println("Realistic ALL categories products seeded for YourPrint clone");
        }
    }

    private void addProduct(Category category, String name, String desc, String price, String imgUrl, String glbPath, String canvaTemplateId) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(desc);
        p.setPrice(new BigDecimal(price));
        p.setImageUrl(imgUrl);
        p.setCustomizable(true);
        p.setCategory(category);
        p.setModelGlbPath(glbPath);
        p.setCanvaTemplateId(canvaTemplateId);
        productRepository.save(p);
    }
}
