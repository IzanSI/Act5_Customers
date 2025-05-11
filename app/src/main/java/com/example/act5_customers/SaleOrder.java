package com.example.act5_customers;

import java.io.Serializable;

public class SaleOrder implements Serializable {
    public int id;
    public String name;
    public String partnerName;
    public String dateOrder;
    public String state;

    public SaleOrder(int id, String name, String partnerName, String dateOrder, String state) {
        this.id = id;
        this.name = name;
        this.partnerName = partnerName;
        this.dateOrder = dateOrder;
        this.state = state;
    }
}
