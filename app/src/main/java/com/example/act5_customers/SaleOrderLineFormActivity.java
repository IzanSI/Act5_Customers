package com.example.act5_customers;

import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.*;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class SaleOrderLineFormActivity extends AppCompatActivity {

    private EditText etProductId, etQuantity, etPriceUnit;
    private Button btnSave;
    private int uid;
    private String baseUrl, db, username, password;
    private int orderId;

    private SaleOrderLine editingLine = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_order_line_form);

        etProductId = findViewById(R.id.etProductId);
        etQuantity = findViewById(R.id.etQuantity);
        etPriceUnit = findViewById(R.id.etPriceUnit);
        btnSave = findViewById(R.id.btnSaveLine);

        baseUrl = getIntent().getStringExtra("baseUrl");
        db = getIntent().getStringExtra("db");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        uid = getIntent().getIntExtra("uid", -1);
        orderId = getIntent().getIntExtra("order_id", -1);

        if (getIntent().hasExtra("sale_order_line")) {
            editingLine = (SaleOrderLine) getIntent().getSerializableExtra("sale_order_line");
            etProductId.setText(String.valueOf(editingLine.productId));
            etQuantity.setText(String.valueOf(editingLine.quantity));
            etPriceUnit.setText(String.valueOf(editingLine.priceUnit));
        }

        btnSave.setOnClickListener(v -> saveOrUpdateLine());
    }

    private void saveOrUpdateLine() {
        JsonObject values = new JsonObject();
        values.addProperty("order_id", orderId);
        values.addProperty("product_id", Integer.parseInt(etProductId.getText().toString()));
        values.addProperty("product_uom_qty", Float.parseFloat(etQuantity.getText().toString()));
        values.addProperty("price_unit", Float.parseFloat(etPriceUnit.getText().toString()));

        JsonArray args = new JsonArray();
        args.add(db);
        args.add(uid);
        args.add(password);
        args.add("sale.order.line");

        if (editingLine == null) {
            args.add("create");
            JsonArray data = new JsonArray();
            data.add(values);
            args.add(data);
        } else {
            args.add("write");
            JsonArray data = new JsonArray();
            data.add(editingLine.id);
            data.add(values);
            args.add(data);
        }

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
                    Toast.makeText(SaleOrderLineFormActivity.this,
                            editingLine == null ? "Línia creada" : "Línia actualitzada", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                runOnUiThread(() -> Toast.makeText(SaleOrderLineFormActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}
