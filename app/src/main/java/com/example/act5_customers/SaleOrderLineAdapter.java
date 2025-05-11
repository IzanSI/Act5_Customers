package com.example.act5_customers;

import android.view.*;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.act5_customers.SaleOrderLine;

import java.util.List;

public class SaleOrderLineAdapter extends RecyclerView.Adapter<SaleOrderLineAdapter.ViewHolder> {
    private final List<SaleOrderLine> lines;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, qty, price;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tvProductName);
            qty = view.findViewById(R.id.tvQty);
            price = view.findViewById(R.id.tvPrice);
        }
    }

    public SaleOrderLineAdapter(List<SaleOrderLine> lines) {
        this.lines = lines;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale_order_line, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SaleOrderLine line = lines.get(position);
        holder.name.setText(line.productName);
        holder.qty.setText("Quantitat: " + line.quantity);
        holder.price.setText("Preu unitari: " + line.priceUnit + " €");
    }

    @Override
    public int getItemCount() {
        return lines.size();
    }
}
