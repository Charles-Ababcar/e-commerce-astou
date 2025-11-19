package com.example.demo.dto.request;

public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;

    public ProductResponseDTO() {
    }

    private String imageUrl;
    private int priceCents;
    private int stock;
    private Long categoryId;
    private String categoryName;

    private Long shopId;

    private String shopName;

    public ProductResponseDTO(Long id, String name, String description, String imageUrl, int priceCents, int stock, Long categoryId, String categoryName, Long shopId, String shopName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.priceCents = priceCents;
        this.stock = stock;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.shopId = shopId;
        this.shopName = shopName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPriceCents(int priceCents) {
        this.priceCents = priceCents;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public ProductResponseDTO(Long id, String name, String description, String imageUrl, int priceCents,
                              int stock, Long categoryId, String categoryName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.priceCents = priceCents;
        this.stock = stock;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public int getPriceCents() { return priceCents; }
    public int getStock() { return stock; }
    public Long getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
}
