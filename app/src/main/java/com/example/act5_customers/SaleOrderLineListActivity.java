package com.example.act5_customers;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.act5_customers.SaleOrderLine;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class SaleOrderLineListActivity extends AppCompatActivity {

    private String baseUrl, db, username, password;
    private int uid, orderId;

    private RecyclerView recyclerView;
    private List<SaleOrderLine> lines = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_order_line_list);

        baseUrl = getIntent().getStringExtra("baseUrl");
        db = getIntent().getStringExtra("db");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        uid = getIntent().getIntExtra("uid", -1);
        orderId = getIntent().getIntExtra("order_id", -1);

        recyclerView = findViewById(R.id.recyclerLines);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchLines();
    }

    private void fetchLines() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OdooClient client = retrofit.create(OdooClient.class);

        JsonArray domain = new JsonArray();
        JsonArray filter = new JsonArray();
        filter.add("order_id");
        filter.add("=");
        filter.add(orderId);
        domain.add(filter);

        JsonArray fields = new JsonArray();
        fields.add("product_id");
        fields.add("product_uom_qty");
        fields.add("price_unit");

        JsonArray args = new JsonArray();
        args.add(db);
        args.add(uid);
        args.add(password);
        args.add("sale.order.line");
        args.add("search_read");

        JsonArray methodArgs = new JsonArray();
        methodArgs.add(domain);

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
                    JsonArray result = response.body().getAsJsonObject("result").getAsJsonArray();
                    for (JsonElement e : result) {
                        JsonObject obj = e.getAsJsonObject();
                        String productName = obj.getAsJsonArray("product_id").get(1).getAsString();
                        double price = obj.get("price_unit").getAsDouble();
                        int qty = obj.get("product_uom_qty").getAsInt();

                        lines.add(new SaleOrderLine(obj.get("id").getAsInt(), productName, price, qty));
                    }

                    runOnUiThread(() -> recyclerView.setAdapter(new SaleOrderLineAdapter(lines)));

                } catch (Exception e) {
                    Log.e("ORDER_LINES", "Error: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("ORDER_LINES", "Error connexió: " + t.getMessage());
            }
        });
    }
}
