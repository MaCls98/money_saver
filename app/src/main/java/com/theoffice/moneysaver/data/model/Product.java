package com.theoffice.moneysaver.data.model;

import java.io.Serializable;

public class Product implements Serializable {

    private String productName;
    private int productValue;
    private String productPhoto;
    private String latitude;
    private String longitude;

    public Product(String productName, int productValue, String productPhoto, String latitude, String longitude) {
        this.productName = productName;
        this.productValue = productValue;
        this.productPhoto = productPhoto;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
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
