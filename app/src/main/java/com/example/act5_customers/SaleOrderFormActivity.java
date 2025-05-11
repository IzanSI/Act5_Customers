package com.example.act5_customers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.act5_customers.SaleOrder;
import com.google.gson.*;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class SaleOrderFormActivity extends AppCompatActivity {

    private EditText etName, etDate, etState;
    private Button btnSave;

    private String baseUrl, db, username, password;
    private int uid;

    private SaleOrder saleOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_order_form);

        baseUrl = getIntent().getStringExtra("baseUrl");
        db = getIntent().getStringExtra("db");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        uid = getIntent().getIntExtra("uid", -1);

        saleOrder = (SaleOrder) getIntent().getSerializableExtra("sale_order");

        etName = findViewById(R.id.etOrderName);
        etDate = findViewById(R.id.etOrderDate);
        etState = findViewById(R.id.etOrderState);
        btnSave = findViewById(R.id.btnSaveOrder);

        if (saleOrder != null) {
            etName.setText(saleOrder.name);
            etDate.setText(saleOrder.dateOrder);
            etState.setText(saleOrder.state);
        }

        btnSave.setOnClickListener(v -> updateOrder());

        Button btnLines = findViewById(R.id.btnViewLines);
        btnLines.setOnClickListener(v -> {
            Intent intent = new Intent(SaleOrderFormActivity.this, SaleOrderLineListActivity.class);
            intent.putExtra("order_id", saleOrder.id);
            intent.putExtra("uid", uid);
            intent.putExtra("username", username);
            intent.putExtra("password", password);
            intent.putExtra("db", db);
            intent.putExtra("baseUrl", baseUrl);
            startActivity(intent);
        });

    }

    private void updateOrder() {
        String name = etName.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String state = etState.getText().toString().trim();

        JsonObject values = new JsonObject();
        values.addProperty("name", name);
        values.addProperty("date_order", date);
        values.addProperty("state", state);

        JsonArray args = new JsonArray();
        args.add(db);
        args.add(uid);
        args.add(password);
        args.add("sale.order");
        args.add("write");

        JsonArray writeArgs = new JsonArray();
        writeArgs.add(saleOrder.id);
        writeArgs.add(values);
        args.add(writeArgs);

        JsonObject params = new JsonObject();
        params.addProperty("service", "object");
        params.addProperty("method", "execute_kw");
        params.add("args", args);

        JsonObject body = new JsonObject();
        body.addProperty("jsonrpc", "2.0");
        body.addProperty("method", "call");
        body.add("params", params);
        body.addProperty("id", 1);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OdooClient client = retrofit.create(OdooClient.class);

        client.authenticate(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                runOnUiThread(() -> {
                    Toast.makeText(SaleOrderFormActivity.this, "Ordre actualitzada", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                runOnUiThread(() -> Toast.makeText(SaleOrderFormActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}
