package com.example.act5_customers;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.act5_customers.OdooClient;
import com.google.gson.*;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class CustomerFormActivity extends AppCompatActivity {
    private Customer editingCustomer = null;
    private EditText etName, etEmail;
    private Button btnSave;
    private String baseUrl;
    private String db;
    private String username;
    private String password;
    private int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_form);
        baseUrl = getIntent().getStringExtra("baseUrl");
        db = getIntent().getStringExtra("db");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        uid = getIntent().getIntExtra("uid", -1);

        if (getIntent().hasExtra("customer")) {
            editingCustomer = (Customer) getIntent().getSerializableExtra("customer");
            etName.setText(editingCustomer.name);
            etEmail.setText(editingCustomer.email);
            baseUrl = getIntent().getStringExtra("baseUrl");
            db = getIntent().getStringExtra("db");
            username = getIntent().getStringExtra("username");
            password = getIntent().getStringExtra("password"); //Bz:lev/N=@50
            uid = getIntent().getIntExtra("uid", -1);
            btnSave.setText("Actualizar");
        }

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        btnSave = findViewById(R.id.btnSaveCustomer);

        btnSave.setOnClickListener(v -> saveCustomer());
    }

    private void saveCustomer() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OdooClient client = retrofit.create(OdooClient.class);

        JsonObject values = new JsonObject();
        values.addProperty("name", name);
        values.addProperty("email", email);
        values.addProperty("customer_rank", 1);

        JsonArray args = new JsonArray();
        args.add(db);
        args.add(uid);
        args.add(password);
        args.add("res.partner");

        JsonObject body = new JsonObject();
        body.addProperty("jsonrpc", "2.0");
        body.addProperty("method", "call");
        body.addProperty("id", 1);

        JsonArray methodArgs = new JsonArray();

        if (editingCustomer == null) {
            // CREATE
            args.add("create");

            JsonArray dataList = new JsonArray();
            dataList.add(values);
            methodArgs.add(dataList);

        } else {
            // UPDATE
            args.add("write");

            JsonArray dataList = new JsonArray();
            dataList.add(editingCustomer.id);
            dataList.add(values);
            methodArgs.add(dataList);
        }

        args.add(methodArgs);

        JsonObject params = new JsonObject();
        params.addProperty("service", "object");
        params.addProperty("method", "execute_kw");
        params.add("args", args);

        body.add("params", params);

        Log.d("CUSTOMER_FORM", body.toString());

        client.authenticate(body).enqueue(responseHandler(editingCustomer == null ? "Cliente creado" : "Cliente actualizado"));
    }

    private Callback<JsonObject> responseHandler(String successMessage) {
        return new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                runOnUiThread(() -> {
                    Toast.makeText(CustomerFormActivity.this, successMessage, Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                runOnUiThread(() ->
                        Toast.makeText(CustomerFormActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        };
    }

}

