package com.pro.cakeshop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pro.cakeshop.Adapter.DonHangChiTietAdapter;
import com.pro.cakeshop.Constant;
import com.pro.cakeshop.Database.FirebaseHelper;
import com.pro.cakeshop.Model.Banh;
import com.pro.cakeshop.Model.DonHang;
import com.pro.cakeshop.Model.DonHangChiTiet;
import com.pro.cakeshop.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class BillActivity extends AppCompatActivity {

    private TextView tvIdTransaction, tvDateTime;
    private RecyclerView rcvProducts;
    private TextView tvPrice, tvTotal, tvPaymentMethod;
    private TextView tvName, tvPhone, tvAddress;
    private TextView tvTrackingOrder;

    private String maDonHang;
    private String maKH;
    private DonHang mDonHang;
    private ValueEventListener mValueEventListener;
    private FirebaseHelper mFirebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bill);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Helper
        mFirebaseHelper = new FirebaseHelper();

        getDataIntent();
        getKhachHangInfo();
        initToolbar();
        initUi();
//        initListener();
        getDonHangDetailFromFirebase();
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        maDonHang = bundle.getString(Constant.ORDER_ID);
        maKH = bundle.getString(Constant.CUSTOMER_ID);
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> finish());
        tvToolbarTitle.setText("Hóa đơn");
    }

    private void initUi() {
        tvIdTransaction = findViewById(R.id.tv_id_transaction);
        tvDateTime = findViewById(R.id.tv_date_time);
        rcvProducts = findViewById(R.id.rcv_products);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvProducts.setLayoutManager(linearLayoutManager);

        tvPrice = findViewById(R.id.tv_price);
        tvTotal = findViewById(R.id.tv_total);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
//        tvTrackingOrder = findViewById(R.id.tv_tracking_order);
        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_phone);
        tvAddress = findViewById(R.id.tv_address);
    }

//    private void initListener() {
//        tvTrackingOrder.setOnClickListener(view -> {
//            if (mDonHang == null) return;
//            Bundle bundle = new Bundle();
//            bundle.putString(Constant.ORDER_ID, mDonHang.getMaDonHang());
//            bundle.putString(Constant.CUSTOMER_ID, mDonHang.getMaKH());
//
//            // Navigate to tracking activity
//            Intent intent = new Intent(BillActivity.this, TrackingOrderActivity.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
//            finish();
//        });
//    }

    private void getDonHangDetailFromFirebase() {
        showProgressDialog(true);
        Log.d("Firebase", "maKH: " + maKH + ", maDonHang: " + maDonHang);

        // Use Firebase get() method to fetch data once
        mFirebaseHelper.getDonHangReference().child(maKH).child(maDonHang).get()
                .addOnCompleteListener(task -> {
                    showProgressDialog(false);

                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();

                        // Kiểm tra nếu snapshot không có dữ liệu
                        if (!snapshot.exists()) {
                            showToastMessage("Order details not found.");
                            Log.e("zzzzzzzzz", "null data");
                            return;
                        }

                        // Lấy dữ liệu từ snapshot
                        mDonHang = snapshot.getValue(DonHang.class);

                        // Kiểm tra nếu mDonHang là null
                        if (mDonHang == null) {
                            showToastMessage("Order details not found.");
                            Log.e("zzzzzzzzz", "null ma");
                            return;
                        }

                        // Kiểm tra xem maDonHang có bị thiếu và gán lại nếu cần
                        if (mDonHang.getMaDonHang() == null || mDonHang.getMaDonHang().isEmpty()) {
                            mDonHang.setMaDonHang(snapshot.getKey());
                            Log.e("zzzzzzzzz", "null ma null");
                        }

                        // Khởi tạo UI với dữ liệu
                        initData();
                    } else {
                        showToastMessage("Failed to get order details: " + task.getException().getMessage());
                    }
                });
    }

    private String formatCurrency(long amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); // Set separator as dot
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(amount);
    }

    private void initData() {
        tvIdTransaction.setText(mDonHang.getMaDonHang());
        tvDateTime.setText(DateTimeUtils.convertTimeStampToDate((mDonHang.getNgayDat())));

        // Calculate subtotal from order details
        long subtotal = 0;
        if (mDonHang.getDonHangChiTiet() != null) {
            for (DonHangChiTiet chiTiet : mDonHang.getDonHangChiTiet()) {
                subtotal += chiTiet.getThanhTien();
            }
        }

        // Format price and total
        String strPrice = formatCurrency(subtotal) + Constant.CURRENCY;
        tvPrice.setText(strPrice);

        // Calculate total (you might add shipping fee, etc.)
        long total = subtotal; // Add additional fees if needed
        String strTotal = formatCurrency(total) + Constant.CURRENCY;
        tvTotal.setText(strTotal);


        // Check if we have order details
        if (mDonHang.getDonHangChiTiet() == null || mDonHang.getDonHangChiTiet().isEmpty()) {
            Log.e("RecyclerView", "Danh sách đơn hàng chi tiết rỗng!");
            // Show some placeholder or message in the UI
            Toast.makeText(this, "Không có chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("RecyclerView", "Danh sách đơn hàng chi tiết: " + mDonHang.getDonHangChiTiet().size());

        // Get the list of order details
        List<DonHangChiTiet> list = new ArrayList<>(mDonHang.getDonHangChiTiet());

        // Create and set adapter
        DonHangChiTietAdapter adapter = new DonHangChiTietAdapter(list, mFirebaseHelper);
        rcvProducts.setAdapter(adapter);
    }

    private void showProgressDialog(boolean isShow) {
        // Implement your progress dialog here
        // You can use a ProgressDialog or a custom layout
    }
    private void getKhachHangInfo() {
        if (maKH == null || maKH.isEmpty()) return;

        mFirebaseHelper.getKhachHangReference().child(maKH)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) return;

                        // Giả sử KhachHang có các trường tenKhachHang, soDienThoai, diaChi
                        String tenKhachHang = snapshot.child("tenKH").getValue(String.class);
                        String soDienThoai = snapshot.child("sdt").getValue(String.class);
                        String diaChi = snapshot.child("diaChi").getValue(String.class);

                        // Cập nhật UI
                        if (tenKhachHang != null) tvName.setText(tenKhachHang);
                        if (soDienThoai != null) tvPhone.setText(soDienThoai);
                        if (diaChi != null) tvAddress.setText(diaChi);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showToastMessage("Lỗi lấy thông tin khách hàng: " + error.getMessage());
                    }
                });
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    // Trong BillActivity.java


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mValueEventListener != null) {
            mFirebaseHelper.getDonHangReference().child(maKH).child(maDonHang)
                    .removeEventListener(mValueEventListener);
        }
    }
}