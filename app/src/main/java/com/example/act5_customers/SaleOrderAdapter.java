package com.example.act5_customers;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.act5_customers.SaleOrder;

import java.util.List;

public class SaleOrderAdapter extends RecyclerView.Adapter<SaleOrderAdapter.ViewHolder> {
    private final List<SaleOrder> orders;
    private final String baseUrl, db, username, password;
    private final int uid;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, partner, date, state;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tvOrderName);
            partner = view.findViewById(R.id.tvOrderPartner);
            date = view.findViewById(R.id.tvOrderDate);
            state = view.findViewById(R.id.tvOrderState);
        }
    }

    public SaleOrderAdapter(List<SaleOrder> orders, String baseUrl, String db, String username, String password, int uid) {
        this.orders = orders;
        this.baseUrl = baseUrl;
        this.db = db;
        this.username = username;
        this.password = password;
        this.uid = uid;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale_order, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SaleOrder o = orders.get(position);
        Context context = holder.itemView.getContext();

        holder.name.setText(o.name);
        holder.partner.setText("Client: " + o.partnerName);
        holder.date.setText("Data: " + o.dateOrder);
        holder.state.setText("Estat: " + o.state);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SaleOrderFormActivity.class);
            intent.putExtra("sale_order", o);
            intent.putExtra("uid", uid); // Si no tens aquests valors dins l'adapter, els pots passar pel constructor
            intent.putExtra("username", username);
            intent.putExtra("password", password);
            intent.putExtra("db", db);
            intent.putExtra("baseUrl", baseUrl);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}