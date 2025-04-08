package com.pro.cakeshop.Adapter.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.cakeshop.Constant;
import com.pro.cakeshop.Model.DonHang;
import com.pro.cakeshop.Model.DonHangChiTiet;
import com.pro.cakeshop.R;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {
    private final Context context;
    private final List<DonHang> donHangList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public OrderHistoryAdapter(Context context, List<DonHang> donHangList) {
        this.context = context;
        this.donHangList = donHangList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_revenue, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        DonHang donHang = donHangList.get(position);

        holder.tvId.setText(donHang.getMaDonHang());
        String formattedDate = "Không xác định";
        try {
            String ngayDatRaw = donHang.getNgayDat();

            if (ngayDatRaw.matches("\\d+")) {
                // Là chuỗi số (timestamp)
                long timestamp = Long.parseLong(ngayDatRaw);
                Date date = new Date(timestamp);
                formattedDate = dateFormat.format(date);

            } else {
                // Là chuỗi ngày ISO 8601 (ví dụ: "2025-04-04T13:35:26Z")
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                Date date = isoFormat.parse(ngayDatRaw);
                formattedDate = dateFormat.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Ghi log nếu muốn
        }
        holder.tvDate.setText(formattedDate);


        String totalAmount = formatCurrency((long) calculateTotalAmount(donHang)) + " " + Constant.CURRENCY;

        // Format total amount
        holder.tvTotalAmount.setText(totalAmount);

    }

    private String formatCurrency(long amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); // Set separator as dot
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(amount);
    }

    private double calculateTotalAmount(DonHang order) {
        double total = 0;
        if (order.getDonHangChiTiet() != null) {
            for (DonHangChiTiet item : order.getDonHangChiTiet()) {
                total += item.getThanhTien();
            }
        }
        return total;
    }

    @Override
    public int getItemCount() {
        return donHangList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvDate, tvTotalAmount;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_id);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
        }
    }
}