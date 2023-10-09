package com.example.ecommerceprediction.holders;

import java.io.Serializable;

public class Products implements Serializable {
    private String ProductID;
    private String Brand;
    private String Color;
    private String Gender;
    private String Material;
    private Object Model;
    private int Size;
    private double Price;
    private String Type;
    private String p_image_url;
    private int averageRating;
    private int ratingCount;

    public Products() {
    }

    public Products(String productID, String brand, String color, String gender, String material, Object model, double price, String type, String p_image_url, int averageRating, int ratingCount) {
        this.ProductID = productID;
        this.Brand = brand;
        this.Color = color;
        this.Gender = gender;
        this.Material = material;
        this.Model = model;
        this.Price = price;
        this.Type = type;
        this.p_image_url = p_image_url;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
    }

    public int getSize() {
        return Size;
    }

    public void setSize(int size) {
        Size = size;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        this.ProductID = productID;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        this.Brand = brand;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        this.Color = color;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        this.Gender = gender;
    }

    public String getMaterial() {
        return Material;
    }

    public void setMaterial(String material) {
        this.Material = material;
    }

    public Object getModel() {
        return Model;
    }

    public void setModel(Object model) {
        this.Model = model;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        this.Price = price;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public String getP_image_url() {
        return p_image_url;
    }

    public void setP_image_url(String p_image_url) {
        this.p_image_url = p_image_url;
    }

    public int getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(int averageRating) {
        this.averageRating = averageRating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
}
