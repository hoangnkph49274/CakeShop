package com.pro.cakeshop.Activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.cakeshop.Adapter.admin.OrderHistoryAdapter;
import com.pro.cakeshop.Model.DonHang;
import com.pro.cakeshop.Model.DonHangChiTiet;
import com.pro.cakeshop.R;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RevenueActivity extends AppCompatActivity {
    private TextView tvDateFrom, tvDateTo, tvTotalRevenue;
    private RecyclerView rcvOrderHistory;
    private OrderHistoryAdapter orderHistoryAdapter;
    private List<DonHang> orderList;

    private Calendar calendarFrom = Calendar.getInstance();
    private Calendar calendarTo = Calendar.getInstance();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue);

        initializeViews();
        setupDatePickers();
        setupRecyclerView();
        fetchOrderData();
    }

    private void initializeViews() {
        // Initialize toolbar

        tvDateFrom = findViewById(R.id.tv_date_from);
        tvDateTo = findViewById(R.id.tv_date_to);
        tvTotalRevenue = findViewById(R.id.tv_total_value);
        rcvOrderHistory = findViewById(R.id.rcv_order_history);

        // Set default date values to current date
        tvDateFrom.setText(dateFormatter.format(calendarFrom.getTime()));
        tvDateTo.setText(dateFormatter.format(calendarTo.getTime()));
    }

    private void setupDatePickers() {
        tvDateFrom.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        calendarFrom.set(Calendar.YEAR, year);
                        calendarFrom.set(Calendar.MONTH, month);
                        calendarFrom.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        tvDateFrom.setText(dateFormatter.format(calendarFrom.getTime()));
                        fetchOrderData(); // Refresh data when date changes
                    },
                    calendarFrom.get(Calendar.YEAR),
                    calendarFrom.get(Calendar.MONTH),
                    calendarFrom.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        tvDateTo.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        calendarTo.set(Calendar.YEAR, year);
                        calendarTo.set(Calendar.MONTH, month);
                        calendarTo.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        tvDateTo.setText(dateFormatter.format(calendarTo.getTime()));
                        fetchOrderData(); // Refresh data when date changes
                    },
                    calendarTo.get(Calendar.YEAR),
                    calendarTo.get(Calendar.MONTH),
                    calendarTo.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
    }

    private void setupRecyclerView() {
        orderList = new ArrayList<>();
        orderHistoryAdapter = new OrderHistoryAdapter(this, orderList);
        rcvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        rcvOrderHistory.setAdapter(orderHistoryAdapter);
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
    private void fetchOrderData() {
        orderRef = FirebaseDatabase.getInstance().getReference("donHang");
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderList.clear();
                int totalRevenue = 0;

                for (DataSnapshot customerSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot orderSnapshot : customerSnapshot.getChildren()) {
                        try {
                            DonHang donHang = new DonHang();
                            donHang.setMaDonHang(orderSnapshot.child("maDonHang").getValue(String.class));
                            donHang.setMaKH(orderSnapshot.child("maKH").getValue(String.class));
                            donHang.setDiaChi(orderSnapshot.child("diaChi").getValue(String.class));
                            donHang.setTrangThai(orderSnapshot.child("trangThai").getValue(String.class));

                            // Kiểm tra trạng thái "đã giao"
                            if (!"Đã giao".equalsIgnoreCase(donHang.getTrangThai())) {
                                continue; // Bỏ qua nếu không phải "đã giao"
                            }

                            // Handle ngayDat - could be Long or String
                            Object dateObj = orderSnapshot.child("ngayDat").getValue();
                            long orderDate = 0;
                            if (dateObj instanceof Long) {
                                orderDate = (Long) dateObj;
                                donHang.setNgayDat(orderDate);
                            } else if (dateObj instanceof String) {
                                try {
                                    String dateStr = (String) dateObj;
                                    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                                    Date date = isoFormat.parse(dateStr);
                                    orderDate = date != null ? date.getTime() : 0;
                                    donHang.setNgayDat(orderDate);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            // Handle tongTien
                            Object totalObj = orderSnapshot.child("tongTien").getValue();
                            if (totalObj instanceof Long) {
                                donHang.setTongTien(((Long) totalObj).intValue());
                            } else if (totalObj instanceof Integer) {
                                donHang.setTongTien((Integer) totalObj);
                            } else if (totalObj instanceof Double) {
                                donHang.setTongTien(((Double) totalObj).intValue());
                            } else if (totalObj instanceof String) {
                                donHang.setTongTien(Integer.parseInt((String) totalObj));
                            }

                            // Get order details if needed
                            DataSnapshot detailsSnapshot = orderSnapshot.child("donHangChiTiet");
                            if (detailsSnapshot.exists()) {
                                List<DonHangChiTiet> chiTietList = new ArrayList<>();
                                for (DataSnapshot chiTietSnapshot : detailsSnapshot.getChildren()) {
                                    DonHangChiTiet chiTiet = chiTietSnapshot.getValue(DonHangChiTiet.class);
                                    if (chiTiet != null) {
                                        chiTietList.add(chiTiet);
                                    }
                                }
                                donHang.setDonHangChiTiet(chiTietList);
                            }

                            // Filter orders by date range
                            if (isOrderInDateRange(orderDate)) {
                                orderList.add(donHang);
                                totalRevenue += donHang.getTongTien();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Update UI with filtered orders and total revenue
                orderHistoryAdapter.notifyDataSetChanged();
                tvTotalRevenue.setText(formatCurrency(totalRevenue) + " VNĐ");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RevenueActivity.this, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isOrderInDateRange(long orderDate) {
        // Set start time to beginning of the day
        calendarFrom.set(Calendar.HOUR_OF_DAY, 0);
        calendarFrom.set(Calendar.MINUTE, 0);
        calendarFrom.set(Calendar.SECOND, 0);

        // Set end time to end of the day
        calendarTo.set(Calendar.HOUR_OF_DAY, 23);
        calendarTo.set(Calendar.MINUTE, 59);
        calendarTo.set(Calendar.SECOND, 59);

        return orderDate >= calendarFrom.getTimeInMillis() && orderDate <= calendarTo.getTimeInMillis();
    }
}