package com.pro.cakeshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pro.cakeshop.Model.GioHangItem;
import com.pro.cakeshop.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<GioHangItem> cartItemList;
    private Context context;
    private CartItemListener listener;

    public interface CartItemListener {
        void onQuantityChanged(int position, int newQuantity);
        void onItemRemoved(int position);
    }

    public CartAdapter(List<GioHangItem> cartItemList, Context context, CartItemListener listener) {
        this.cartItemList = cartItemList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        GioHangItem item = cartItemList.get(position);

        holder.tvName.setText(item.getTenBanh());
        holder.tvPrice.setText(String.format("%,d VND", item.getGia()));
        holder.tvCount.setText(String.valueOf(item.getSoLuong()));

        // Calculate and set total price for this item
        int totalPrice = item.getGia() * item.getSoLuong();
        holder.tvTotalPrice.setText(String.format("%,d VND", totalPrice));

        // Load image using Glide if available
        if (item.getHinhAnh() != null && !item.getHinhAnh().isEmpty()) {
            Glide.with(context)
                    .load(item.getHinhAnh())
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.ic_star_yellow)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.logo);
        }

        // Set click listeners for quantity buttons
        holder.tvAdd.setOnClickListener(v -> {
            int newQuantity = item.getSoLuong() + 1;
            if (listener != null) {
                listener.onQuantityChanged(holder.getAdapterPosition(), newQuantity);
            }
        });

        holder.tvSub.setOnClickListener(v -> {
            int newQuantity = item.getSoLuong() - 1;
            if (newQuantity <= 0) {
                if (listener != null) {
                    listener.onItemRemoved(holder.getAdapterPosition());
                }
            } else if (listener != null) {
                listener.onQuantityChanged(holder.getAdapterPosition(), newQuantity);
            }
        });

        holder.imgDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemRemoved(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList != null ? cartItemList.size() : 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvPrice;
        TextView tvCount;
        TextView tvTotalPrice;
        ImageView imgProduct;
        TextView tvAdd;
        TextView tvSub;
        ImageView imgDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvCount = itemView.findViewById(R.id.tv_count);
            tvTotalPrice = itemView.findViewById(R.id.tv_description);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvAdd = itemView.findViewById(R.id.tv_add);
            tvSub = itemView.findViewById(R.id.tv_sub);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }
}