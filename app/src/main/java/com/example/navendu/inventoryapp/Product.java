package com.example.navendu.inventoryapp;

/**
 * Defines product
 */
public class Product {

    private int dbId;
    private String productName;
    private byte[] productImage;
    private String productDesc;
    private double productPrice;
    private int qtyAvl; //product quantity available
    private int qtySold; // product quantity sold

    public Product(int dbId, String productName, byte[] productImage, String productDesc, double productPrice, int qtyAvl, int qtySold) {
        this.dbId = dbId;
        this.productName = productName;
        this.productImage = productImage;
        this.productDesc = productDesc;
        this.productPrice = productPrice;
        this.qtyAvl = qtyAvl;
        this.qtySold = qtySold;
    }

    public int getDbId() {
        return dbId;
    }

    public String getProductName() {
        return productName;
    }

    public byte[] getProductImage() {
        return productImage;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public int getQtyAvl() {
        return qtyAvl;
    }

    public int getQtySold() {
        return qtySold;
    }
}
