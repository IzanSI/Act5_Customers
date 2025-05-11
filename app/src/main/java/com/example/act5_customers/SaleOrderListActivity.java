package com.example.act5_customers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class SaleOrderListActivity extends AppCompatActivity {

    private String baseUrl, db, username, password;
    private int uid;

    private RecyclerView recyclerView;
    private List<SaleOrder> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_order_list);

        baseUrl = getIntent().getStringExtra("baseUrl");
        db = getIntent().getStringExtra("db");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        uid = getIntent().getIntExtra("uid", -1);

        recyclerView = findViewById(R.id.recyclerSaleOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 🔙 Botó tornar
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // ➕ Botó afegir
        Button btnAdd = findViewById(R.id.btnAddOrder);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(SaleOrderListActivity.this, SaleOrderFormActivity.class);
            intent.putExtra("uid", uid);
            intent.putExtra("username", username);
            intent.putExtra("password", password);
            intent.putExtra("db", db);
            intent.putExtra("baseUrl", baseUrl);
            startActivity(intent);
        });

        fetchSaleOrders();
    }

    private void fetchSaleOrders() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OdooClient client = retrofit.create(OdooClient.class);

        JsonArray fields = new JsonArray();
        fields.add("name");
        fields.add("partner_id");
        fields.add("date_order");
        fields.add("state");

        JsonArray args = new JsonArray();
        args.add(db);
        args.add(uid);
        args.add(password);
        args.add("sale.order");
        args.add("search_read");

        JsonArray methodArgs = new JsonArray();
        methodArgs.add(new JsonArray());

        JsonObject kwargs = new JsonObject();
        kwargs.add("fields", fields);
        kwargs.addProperty("limit", 50);

        args.add(methodArgs);
        args.add(kwargs);

        JsonObject params = new JsonObject();
        params.addProperty("service", "object");
        params.addProperty("method", "execute_kw");
        params.add("args", args);

        JsonObject body = new JsonObject();
        body.addProperty("jsonrpc", "2.0");
        body.addProperty("method", "call");
        body.add("params", params);
        body.addProperty("id", 1);

        client.authenticate(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JsonArray records = response.body().getAsJsonArray("result");

                    for (JsonElement element : records) {
                        JsonObject o = element.getAsJsonObject();

                        String partnerName = "";
                        if (o.has("partner_id") && o.get("partner_id").isJsonArray()) {
                            JsonArray partnerArr = o.getAsJsonArray("partner_id");
                            if (partnerArr.size() > 1) {
                                partnerName = partnerArr.get(1).getAsString();
                            }
                        }

                        orderList.add(new SaleOrder(
                                o.get("id").getAsInt(),
                                o.get("name").getAsString(),
                                partnerName,
                                o.get("date_order").getAsString(),
                                o.get("state").getAsString()
                        ));
                    }

                    runOnUiThread(() -> recyclerView.setAdapter(
                            new SaleOrderAdapter(orderList, baseUrl, db, username, password, uid)
                    ));

                } catch (Exception e) {
                    Log.e("SALE_ORDERS", "Error: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("SALE_ORDERS", "Connexió fallida: " + t.getMessage());
            }
        });
    }
}
