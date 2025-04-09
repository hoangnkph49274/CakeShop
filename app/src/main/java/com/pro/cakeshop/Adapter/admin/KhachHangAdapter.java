package com.pro.cakeshop.Adapter.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.cakeshop.Model.KhachHang;
import com.pro.cakeshop.R;

import java.util.List;

public class KhachHangAdapter extends RecyclerView.Adapter<KhachHangAdapter.KhachHangViewHolder> {

    private List<KhachHang> khachHangList;
    private OnCustomerDeleteListener deleteListener;

    public interface OnCustomerDeleteListener {
        void onDelete(KhachHang khachHang);
    }

    public KhachHangAdapter(List<KhachHang> khachHangList, OnCustomerDeleteListener deleteListener) {
        this.khachHangList = khachHangList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public KhachHangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_khach_hang, parent, false);
        return new KhachHangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KhachHangViewHolder holder, int position) {
        KhachHang khachHang = khachHangList.get(position);

        holder.tvTenKhachHang.setText(khachHang.getTenKH());
        holder.tvSoDienThoai.setText("SĐT: " + khachHang.getSdt());
        holder.tvEmail.setText("Email: " + khachHang.getEmail());
        holder.tvDiaChi.setText("Địa chỉ: " + khachHang.getDiaChi());

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(khachHang);
            }
        });
    }

    @Override
    public int getItemCount() {
        return khachHangList.size();
    }

    static class KhachHangViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenKhachHang, tvSoDienThoai, tvEmail, tvDiaChi;
        ImageButton btnDelete;

        public KhachHangViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenKhachHang = itemView.findViewById(R.id.tvTenKhachHang);
            tvSoDienThoai = itemView.findViewById(R.id.tvSoDienThoai);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvDiaChi = itemView.findViewById(R.id.tvDiaChi);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}