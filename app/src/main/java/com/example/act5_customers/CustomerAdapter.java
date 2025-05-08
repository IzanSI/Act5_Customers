package com.example.act5_customers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.*;

import java.util.List;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    private final List<Customer> customers;
    private final String mode;
    private final String baseUrl, db, username, password;
    private final int uid;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, email;
        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tvCustomerName);
            email = view.findViewById(R.id.tvCustomerEmail);
        }
    }

    public CustomerAdapter(List<Customer> customers, String mode, String baseUrl, String db, String username, String password, int uid) {
        this.customers = customers;
        this.mode = mode;
        this.baseUrl = baseUrl;
        this.db = db;
        this.username = username;
        this.password = password;
        this.uid = uid;
    }

    @Override
    public CustomerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Customer c = customers.get(position);
        Context context = holder.itemView.getContext();

        holder.name.setText(c.name);
        holder.email.setText(c.email != null ? c.email : "");

        holder.itemView.setOnClickListener(v -> {
            if ("edit".equals(mode)) {
                Intent intent = new Intent(context, CustomerFormActivity.class);
                intent.putExtra("customer", c);
                intent.putExtra("uid", uid);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                intent.putExtra("db", db);
                intent.putExtra("baseUrl", baseUrl);
                context.startActivity(intent);
            } else if ("delete".equals(mode)) {
                new AlertDialog.Builder(context)
                        .setTitle("¿Eliminar cliente?")
                        .setMessage("¿Seguro que deseas eliminar a " + c.name + "?")
                        .setPositiveButton("Sí", (dialog, which) -> deleteCustomer(c, context))
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void deleteCustomer(Customer customer, Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OdooClient client = retrofit.create(OdooClient.class);

        JsonArray args = new JsonArray();
        args.add(db);
        args.add(uid);
        args.add(password);
        args.add("res.partner");
        args.add("unlink");

        JsonArray methodArgs = new JsonArray();
        JsonArray ids = new JsonArray();
        ids.add(customer.id);
        methodArgs.add(ids);
        args.add(methodArgs);

        JsonObject params = new JsonObject();
        params.addProperty("service", "object");
        params.addProperty("method", "execute_kw");
        params.add("args", args);

        JsonObject body = new JsonObject();
        body.addProperty("jsonrpc", "2.0");
        body.addProperty("method", "call");
        body.add("params", params);
        body.addProperty("id", 1);

        Log.d("DELETE_REQ", body.toString());

        client.authenticate(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Toast.makeText(context, "Cliente eliminado", Toast.LENGTH_SHORT).show();
                ((CustomerListActivity) context).recreate(); // recargar lista
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context, "Error al eliminar: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }
}
