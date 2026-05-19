package com.example.Alisam_Codes.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "is_customizable")
    private boolean isCustomizable;

    @Column(name = "canva_template_id")
    private String canvaTemplateId;

    @Column(name = "model_glb_path")
    private String modelGlbPath;

    @Column(name = "added_by_manager_id")
    private Long addedByManagerId;

    public Product() {}

    public Product(String name, String description, BigDecimal price, String imageUrl, Category category, boolean isCustomizable, String canvaTemplateId, String modelGlbPath) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.isCustomizable = isCustomizable;
        this.canvaTemplateId = canvaTemplateId;
        this.modelGlbPath = modelGlbPath;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public boolean isCustomizable() { return isCustomizable; }
    public void setCustomizable(boolean customizable) { isCustomizable = customizable; }

    public String getCanvaTemplateId() { return canvaTemplateId; }
    public void setCanvaTemplateId(String canvaTemplateId) { this.canvaTemplateId = canvaTemplateId; }

    public String getModelGlbPath() { return modelGlbPath; }
    public void setModelGlbPath(String modelGlbPath) { this.modelGlbPath = modelGlbPath; }

    public Long getAddedByManagerId() { return addedByManagerId; }
    public void setAddedByManagerId(Long addedByManagerId) { this.addedByManagerId = addedByManagerId; }
}
