package com.pro.cakeshop.Adapter;

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
import com.pro.cakeshop.Database.FirebaseHelper;
import com.pro.cakeshop.Model.Loai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Banh> productList;
    private OnItemClickListener onItemClickListener;
    private Map<String, String> loaiMap = new HashMap<>();


    public interface OnItemClickListener {
        void onEditClick(Banh banh);
        void onDeleteClick(Banh banh);
    }

    public ProductAdapter(List<Banh> productList, OnItemClickListener listener) {
        this.productList = productList;
        this.onItemClickListener = listener;
        fetchLoaiData();
    }

    private void fetchLoaiData() {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.getListLoai(new FirebaseHelper.FirebaseCallback<List<Loai>>() {
            @Override
            public void onSuccess(List<Loai> result) {
                for (Loai loai : result) {
                    loaiMap.put(loai.getMaLoai(), loai.getTenLoai());
                }
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
            }
        });
    }

    public void setProductList(List<Banh> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Banh banh = productList.get(position);
        holder.tvName.setText(banh.getTenBanh());
        holder.tvPriceSale.setText(banh.getGia() + " VND");
        holder.tvCategory.setText(loaiMap.getOrDefault(banh.getMaLoai(), "Không xác định"));
        Glide.with(holder.itemView.getContext()).load(banh.getHinhAnh()).into(holder.imgProduct);

        holder.imgEdit.setOnClickListener(v -> onItemClickListener.onEditClick(banh));
        holder.imgDelete.setOnClickListener(v -> onItemClickListener.onDeleteClick(banh));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgEdit, imgDelete;
        TextView tvName, tvPriceSale, tvCategory;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPriceSale = itemView.findViewById(R.id.tv_price_sale);
            tvCategory = itemView.findViewById(R.id.tv_category);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }
}
