package com.example.Alisam_Codes.models;

import jakarta.persistence.*;

@Entity
@Table(name = "customizations")
public class Customization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "customer_size")
    private String size;

    private String paper;

    private Integer qty;

    @Column(name = "custom_text")
    private String text;

    private String color;

    public Customization() {}

    public Customization(Product product, String size, String paper, Integer qty, String text, String color) {
        this.product = product;
        this.size = size;
        this.paper = paper;
        this.qty = qty;
        this.text = text;
        this.color = color;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getPaper() { return paper; }
    public void setPaper(String paper) { this.paper = paper; }

    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
