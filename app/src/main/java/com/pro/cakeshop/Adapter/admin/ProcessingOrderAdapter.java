package com.pro.cakeshop.Adapter.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.cakeshop.Activity.BillActivity;
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

public class ProcessingOrderAdapter extends RecyclerView.Adapter<ProcessingOrderAdapter.OrderViewHolder> {

    private Context context;
    private List<DonHang> orderList;
    private SimpleDateFormat dateFormat;
    private OrderActionListener actionListener;

    public interface OrderActionListener {
        void onMarkAsCompleted(String maKH, String maDonHang);
        void onViewDetails(DonHang order);
    }

    public ProcessingOrderAdapter(Context context, List<DonHang> orderList, OrderActionListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_processing_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        DonHang order = orderList.get(position);

        // Set order ID
        holder.tvOrderId.setText("Order #" + order.getMaDonHang());

        // Set customer ID
        DatabaseReference khachHangRef = FirebaseDatabase.getInstance()
                .getReference("khachHang").child(order.getMaKH());

        khachHangRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String tenKH = snapshot.child("tenKH").getValue(String.class);
                    holder.tvCustomerId.setText("Khách hàng: " + (tenKH != null ? tenKH : "Không rõ tên"));
                } else {
                    holder.tvCustomerId.setText("Khách không tồn tại");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.tvCustomerId.setText("Lỗi tải tên KH");
            }
        });


        // Set order date
        String formattedDate = "Ngày đặt: Không xác định";
        try {
            String ngayDatRaw = order.getNgayDat();

            if (ngayDatRaw.matches("\\d+")) {
                // Là chuỗi số (timestamp)
                long timestamp = Long.parseLong(ngayDatRaw);
                Date date = new Date(timestamp);
                formattedDate = "Ngày đặt: " + dateFormat.format(date);

            } else {
                // Là chuỗi ngày ISO 8601 (ví dụ: "2025-04-04T13:35:26Z")
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                Date date = isoFormat.parse(ngayDatRaw);
                formattedDate = "Ngày đặt: " + dateFormat.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Ghi log nếu muốn
        }
        holder.tvOrderDate.setText(formattedDate);

        // Set order status
        holder.tvOrderStatus.setText("Trạng thái: " + order.getTrangThai());

        // Set order total amount
        String totalAmount = formatCurrency((long) calculateTotalAmount(order)) + " " + Constant.CURRENCY;
        holder.tvOrderTotal.setText("Tổng tiền: " + totalAmount);

        // Set number of items
        int itemCount = order.getDonHangChiTiet() != null ? order.getDonHangChiTiet().size() : 0;
        holder.tvItemCount.setText(itemCount + " items");

        // Set up button click listeners
        holder.btnComplete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onMarkAsCompleted(order.getMaKH(), order.getMaDonHang());
            }
        });

        holder.btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, BillActivity.class);

            // Pass necessary data as extras
            Bundle bundle = new Bundle();
            bundle.putString(Constant.ORDER_ID, order.getMaDonHang());
            bundle.putString(Constant.CUSTOMER_ID, order.getMaKH());

            intent.putExtras(bundle);
            context.startActivity(intent);
        });
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
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomerId, tvOrderDate, tvOrderStatus, tvOrderTotal, tvItemCount;
        Button btnComplete, btnViewDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_processing_order_id);
            tvCustomerId = itemView.findViewById(R.id.tv_processing_customer_id);
            tvOrderDate = itemView.findViewById(R.id.tv_processing_order_date);
            tvOrderStatus = itemView.findViewById(R.id.tv_processing_order_status);
            tvOrderTotal = itemView.findViewById(R.id.tv_processing_order_total);
            tvItemCount = itemView.findViewById(R.id.tv_processing_item_count);
            btnComplete = itemView.findViewById(R.id.btn_mark_completed);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
        }
    }
}