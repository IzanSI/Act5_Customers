package com.example.act5_customers;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class CustomerListActivity extends AppCompatActivity {
    private String mode = null;
    private String baseUrl;
    private int uid;
    private String db;
    private String username;
    private String password;

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private List<Customer> customerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        mode = getIntent().getStringExtra("mode");

        // Obtener datos del intent
        baseUrl = getIntent().getStringExtra("baseUrl");
        uid = getIntent().getIntExtra("uid", -1);
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        db = getIntent().getStringExtra("db");

        // Referencias UI
        recyclerView = findViewById(R.id.recyclerCustomers);
        tvEmpty = findViewById(R.id.tvEmpty);

        Button btnBack = findViewById(R.id.btnBackToMenu);
        btnBack.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchCustomers();
    }

    private void fetchCustomers() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OdooClient client = retrofit.create(OdooClient.class);

        JsonArray fields = new JsonArray();
        fields.add("name");
        fields.add("email");

        JsonArray args = new JsonArray();
        args.add(db);
        args.add(uid);
        args.add(password);
        args.add("res.partner");
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

        Log.d("REQ", body.toString());

        Call<JsonObject> call = client.authenticate(body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JsonObject res = response.body();
                    Log.d("CUSTOMERS_RESPONSE", res.toString());

                    if (res.has("result") && res.get("result").isJsonArray()) {
                        JsonArray records = res.getAsJsonArray("result");
                        customerList.clear();

                        for (JsonElement element : records) {
                            JsonObject c = element.getAsJsonObject();
                            customerList.add(new Customer(
                                    c.get("id").getAsInt(),
                                    c.get("name").getAsString(),
                                    c.has("email") ? c.get("email").getAsString() : null
                            ));
                        }

                        Log.d("ADAPTER", "Cargando adapter con " + customerList.size() + " clientes");

                        runOnUiThread(() -> {
                            if (customerList.isEmpty()) {
                                recyclerView.setVisibility(View.GONE);
                                tvEmpty.setVisibility(View.VISIBLE);
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                                tvEmpty.setVisibility(View.GONE);
                                recyclerView.setAdapter(new CustomerAdapter(customerList, mode, baseUrl, db, username, password, uid));
                            }
                        });
                    } else {
                        Log.e("CUSTOMERS", "La respuesta no contiene lista de clientes");
                    }
                } catch (Exception e) {
                    Log.e("CUSTOMERS", "Error: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("CUSTOMERS", "Falló conexión: " + t.getMessage());
            }
        });
    }
}
