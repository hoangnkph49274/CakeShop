package com.pro.cakeshop.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pro.cakeshop.Database.FirebaseHelper;
import com.pro.cakeshop.Model.Banh;
import com.pro.cakeshop.Model.DonHangChiTiet;
import com.pro.cakeshop.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class DonHangChiTietAdapter extends RecyclerView.Adapter<DonHangChiTietAdapter.OrderItemViewHolder> {

    private List<DonHangChiTiet> listItems;
    private FirebaseHelper firebaseHelper;
    private static final String TAG = "DonHangChiTietAdapter";

    public DonHangChiTietAdapter(List<DonHangChiTiet> listItems, FirebaseHelper firebaseHelper) {
        this.listItems = listItems;
        this.firebaseHelper = firebaseHelper;
        Log.d(TAG, "Constructor: Items count = " + (listItems != null ? listItems.size() : 0));
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_order, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        DonHangChiTiet item = listItems.get(position);

        // Set default values first
        holder.tvProductName.setText("Đang tải...");
        holder.tvPrice.setText("...");
        holder.tvQuantity.setText(String.valueOf(item.getSoLuong()));
        holder.imgProduct.setImageResource(R.drawable.image_no_available);

        if (item == null) {
            Log.e(TAG, "Item at position " + position + " is null");
            return;
        }

        if (item.getMaBanh() == null || item.getMaBanh().isEmpty()) {
            Log.e(TAG, "MaBanh is null or empty at position " + position);
            holder.tvProductName.setText("Không có mã bánh");
            return;
        }

        Log.d(TAG, "Loading data for maBanh: " + item.getMaBanh() + " at position " + position);

        // Gọi Firebase để lấy dữ liệu bánh
        firebaseHelper.getBanhByMa(item.getMaBanh(), new FirebaseHelper.FirebaseCallback<Banh>() {
            @Override
            public void onSuccess(Banh banh) {
                if (banh != null) {
                    Log.d(TAG, "Successfully loaded data for " + item.getMaBanh() + ": " + banh.getTenBanh());

                    // Hiển thị thông tin từ Firebase
                    holder.tvProductName.setText(banh.getTenBanh());

                    // Định dạng giá tiền
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                    symbols.setGroupingSeparator('.');
                    DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
                    holder.tvPrice.setText(decimalFormat.format(item.getThanhTien()) + "đ");
                    holder.tvDescription.setText(decimalFormat.format(banh.getGia())+ "đ" +"\t x");
                    // Load ảnh bằng Glide
                    if (banh.getHinhAnh() != null && !banh.getHinhAnh().isEmpty()) {
                        Glide.with(holder.imgProduct.getContext())
                                .load(banh.getHinhAnh())
                                .placeholder(R.drawable.image_no_available)
                                .error(R.drawable.image_no_available)
                                .into(holder.imgProduct);
                    } else {
                        holder.imgProduct.setImageResource(R.drawable.image_no_available);
                    }
                } else {
                    Log.e(TAG, "Banh object is null for maBanh: " + item.getMaBanh());
                    holder.tvProductName.setText("Không tìm thấy thông tin bánh");
                }
            }

            @Override
            public void onFailure(String message) {
                Log.e(TAG, "Failed to load data for maBanh: " + item.getMaBanh() + " - " + message);
                // Xử lý khi không lấy được dữ liệu từ Firebase
                holder.tvProductName.setText("Lỗi tải dữ liệu");
                holder.tvPrice.setText("0đ");
                holder.imgProduct.setImageResource(R.drawable.image_no_available);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listItems == null) {
            Log.e(TAG, "listItems is null");
            return 0;
        }
        Log.d(TAG, "getItemCount: " + listItems.size());
        return listItems.size();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgProduct;
        private TextView tvProductName;
        private TextView tvQuantity;
        private TextView tvPrice;
        private TextView tvDescription;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvProductName = itemView.findViewById(R.id.tv_name);
            tvQuantity = itemView.findViewById(R.id.tv_count);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }
    }
}