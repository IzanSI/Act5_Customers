package com.example.act5_customers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button btnList = findViewById(R.id.btnListCustomers);
        Button btnAdd = findViewById(R.id.btnAddCustomer);
        Button btnEdit = findViewById(R.id.btnEditCustomer);
        Button btnDelete = findViewById(R.id.btnDeleteCustomer);
        Button btnSalesOrders = findViewById(R.id.btnSalesOrders);

        btnList.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, CustomerListActivity.class);
            intent.putExtra("uid", getIntent().getIntExtra("uid", -1));
            intent.putExtra("username", getIntent().getStringExtra("username"));
            intent.putExtra("password", getIntent().getStringExtra("password"));
            intent.putExtra("db", getIntent().getStringExtra("db"));
            intent.putExtra("baseUrl", "http://192.168.231.253");
            startActivity(intent);
        });

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, CustomerFormActivity.class);
            intent.putExtra("uid", getIntent().getIntExtra("uid", -1));
            intent.putExtra("username", getIntent().getStringExtra("username"));
            intent.putExtra("password", getIntent().getStringExtra("password"));
            intent.putExtra("db", getIntent().getStringExtra("db"));
            intent.putExtra("baseUrl", "http://192.168.231.253");
            startActivity(intent);
        });

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, CustomerListActivity.class);
            intent.putExtra("uid", getIntent().getIntExtra("uid", -1));
            intent.putExtra("username", getIntent().getStringExtra("username"));
            intent.putExtra("password", getIntent().getStringExtra("password"));
            intent.putExtra("db", getIntent().getStringExtra("db"));
            intent.putExtra("baseUrl", "http://192.168.231.253");
            intent.putExtra("mode", "edit");
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, CustomerListActivity.class);
            intent.putExtra("uid", getIntent().getIntExtra("uid", -1));
            intent.putExtra("username", getIntent().getStringExtra("username"));
            intent.putExtra("password", getIntent().getStringExtra("password"));
            intent.putExtra("db", getIntent().getStringExtra("db"));
            intent.putExtra("baseUrl", "http://192.168.231.253");
            intent.putExtra("mode", "delete");
            startActivity(intent);
        });

        btnSalesOrders.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, SaleOrderListActivity.class);
            intent.putExtra("uid", getIntent().getIntExtra("uid", -1));
            intent.putExtra("username", getIntent().getStringExtra("username"));
            intent.putExtra("password", getIntent().getStringExtra("password"));
            intent.putExtra("db", getIntent().getStringExtra("db"));
            intent.putExtra("baseUrl", "http://192.168.231.253");
            startActivity(intent);
        });

    }

}

