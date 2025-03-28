package com.pro.cakeshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.cakeshop.Model.Banh;
import com.pro.cakeshop.Model.GioHang;
import com.pro.cakeshop.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<GioHang> cartList;
    private List<Banh> banhList;
    private Context context;

    public CartAdapter(List<GioHang> cartList, List<Banh> banhList, Context context) {
        this.cartList = cartList;
        this.banhList = banhList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GioHang item = cartList.get(position);
        for (Banh banh : banhList) {
            if (item.getMaBanh().equals(banh.getMaBanh())) {
                holder.txt.setText(banh.getTenBanh());
                holder.txtPrice.setText(String.format("%,.0f VNĐ", banh.getGia())); // Format price
                holder.txtQuantity.setText("x" + item.getSoLuong());
                // You might want to load the image here if you have an ImageView in item_cart
                // Glide.with(context).load(banh.getHinhAnh()).into(holder.imgProduct);
                break; // Exit the loop once the matching Banh is found
            }
        }
    }

    @Override
    public int getItemCount() {
        return cartList != null ? cartList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt;
        TextView txtPrice;
        TextView txtQuantity;
        // If you have an ImageView in item_cart:
        // ImageView imgProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.tv_name); // Replace with the actual ID in your item_cart.xml
            txtPrice = itemView.findViewById(R.id.tv_price); // Replace with the actual ID in your item_cart.xml
            txtQuantity = itemView.findViewById(R.id.tv_quantity); // Replace with the actual ID in your item_cart.xml
            // If you have an ImageView:
            // imgProduct = itemView.findViewById(R.id.img_item_product); // Replace with the actual ID in your item_cart.xml
        }
    }
}