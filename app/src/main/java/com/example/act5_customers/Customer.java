package com.example.act5_customers;
public class Customer implements java.io.Serializable {
    public int id;
    public String name;
    public String email;

    public Customer(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}


