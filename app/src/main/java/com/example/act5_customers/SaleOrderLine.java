package com.example.act5_customers;

import java.io.Serializable;

public class SaleOrderLine implements Serializable {
    public int id;
    public String productName;
    public double priceUnit;
    public int quantity;
    public char[] productId;

    public SaleOrderLine(int id, String productName, double priceUnit, int quantity) {
        this.id = id;
        this.productName = productName;
        this.priceUnit = priceUnit;
        this.quantity = quantity;
    }
}