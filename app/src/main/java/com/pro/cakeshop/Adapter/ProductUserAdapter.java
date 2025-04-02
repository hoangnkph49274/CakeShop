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
import com.pro.cakeshop.Model.Banh;
import com.pro.cakeshop.R;

import java.text.DecimalFormat;
import java.util.List;

public class ProductUserAdapter extends RecyclerView.Adapter<ProductUserAdapter.ProductViewHolder> {
    private List<Banh> banhList;
    private Context context;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Banh banh);
    }

    public ProductUserAdapter(Context context, List<Banh> banhList, OnProductClickListener listener) {
        this.context = context;
        this.banhList = banhList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Banh banh = banhList.get(position);
        holder.productNameTextView.setText(banh.getTenBanh());
        holder.productPriceTextView.setText(formatPrice(banh.getGia()) + " đ");
        holder.tv_description.setText(banh.getMoTa());

        // Load image using Glide
        if (banh.getHinhAnh() != null && !banh.getHinhAnh().isEmpty()) {
            Glide.with(context)
                    .load(banh.getHinhAnh())
                    .placeholder(R.drawable.logo) // Add a placeholder image in your resources
                    .error(R.drawable.logo) // Add an error image in your resources
                    .into(holder.productImageView);
        } else {
            holder.productImageView.setImageResource(R.drawable.logo); // Set a default image
        }
    }

    private String formatPrice(double price) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(price);
    }

    @Override
    public int getItemCount() {
        return banhList != null ? banhList.size() : 0;
    }

    public void updateProductList(List<Banh> newBanhList) {
        this.banhList = newBanhList;
        notifyDataSetChanged();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView productPriceTextView, tv_description;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.img_product);
            productNameTextView = itemView.findViewById(R.id.tv_name);
            productPriceTextView = itemView.findViewById(R.id.tv_price_sale);
            tv_description = itemView.findViewById(R.id.tv_description);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onProductClick(banhList.get(position));
                }
            });
        }
    }
}