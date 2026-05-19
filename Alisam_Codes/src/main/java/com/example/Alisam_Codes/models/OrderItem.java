package com.example.Alisam_Codes.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "custom_image_url")
    private String customImageUrl;

    @Column(name = "custom_text", length = 500)
    private String customText;

    @Column(name = "canva_design_id")
    private String canvaDesignId;

    public OrderItem() {}

    public OrderItem(Order order, Product product, Integer quantity, BigDecimal price, String customImageUrl, String customText, String canvaDesignId) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.customImageUrl = customImageUrl;
        this.customText = customText;
        this.canvaDesignId = canvaDesignId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getCustomImageUrl() { return customImageUrl; }
    public void setCustomImageUrl(String customImageUrl) { this.customImageUrl = customImageUrl; }

    public String getCustomText() { return customText; }
    public void setCustomText(String customText) { this.customText = customText; }

    public String getCanvaDesignId() { return canvaDesignId; }
    public void setCanvaDesignId(String canvaDesignId) { this.canvaDesignId = canvaDesignId; }
}
