package com.theoffice.moneysaver.data.model;

import java.io.Serializable;

public class Product implements Serializable {

    private String productName;
    private int productValue;
    private String productPhoto;

    public Product(String productName, int productValue, String productPhoto) {
        this.productName = productName;
        this.productValue = productValue;
        this.productPhoto = productPhoto;
    }

    public String getProductName() {
        return productName;
    }

    public int getProductValue() {
        return productValue;
    }

    public String getProductPhoto() {
        return productPhoto;
    }
}
