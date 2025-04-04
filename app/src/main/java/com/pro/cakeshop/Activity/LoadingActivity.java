package com.pro.cakeshop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.pro.cakeshop.Constant;
import com.pro.cakeshop.Database.FirebaseHelper;
import com.pro.cakeshop.Model.DonHang;
import com.pro.cakeshop.Model.DonHangChiTiet;
import com.pro.cakeshop.R;

import java.util.HashMap;
import java.util.Map;

public class LoadingActivity extends AppCompatActivity {

    private DonHang mOrderBooking;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loading);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase helper
        firebaseHelper = new FirebaseHelper();

        // Get data from intent
        getDataIntent();

        // Simulate loading with delay
        Handler handler = new Handler();
        handler.postDelayed(this::createOrderFirebase, 2000);
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Toast.makeText(this, "No data provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get DonHang object from bundle
        mOrderBooking = (DonHang) bundle.getSerializable("ORDER_OBJECT");

        if (mOrderBooking == null) {
            Toast.makeText(this, "Order data is missing", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void createOrderFirebase() {
        if (mOrderBooking == null) {
            Toast.makeText(this, "Order data is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Make sure customer ID is not null
        if (mOrderBooking.getMaKH() == null || mOrderBooking.getMaKH().isEmpty()) {
            Toast.makeText(this, "Customer ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Convert DonHang to Map to avoid type conversion issues
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("maDonHang", mOrderBooking.getMaDonHang());
        orderMap.put("maKH", mOrderBooking.getMaKH());
        orderMap.put("ngayDat", mOrderBooking.getNgayDat());
        orderMap.put("tongTien", mOrderBooking.getTongTien());
        orderMap.put("trangThai", mOrderBooking.getTrangThai());
        orderMap.put("diaChi", mOrderBooking.getDiaChi());
        orderMap.put("donHangChiTiet", mOrderBooking.getDonHangChiTiet());

        // Create order in Firebase using a Map instead of the DonHang object directly
        firebaseHelper.getDonHangReference()
                .child(mOrderBooking.getMaKH())
                .child(mOrderBooking.getMaDonHang())
                .setValue(orderMap)
                .addOnSuccessListener(aVoid -> {
                    // Clear cart after successful order
                    clearCart(mOrderBooking.getMaKH());

                    // Navigate to receipt screen
                    navigateToReceiptScreen(mOrderBooking.getMaDonHang(), mOrderBooking.getMaKH());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoadingActivity.this,
                            "Failed to create order: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void clearCart(String userId) {
        // Remove all items from the user's cart
        firebaseHelper.getDatabase()
                .getReference("gioHang")
                .child(userId)
                .removeValue();
    }

    private void navigateToReceiptScreen(String orderId, String customerId) {
        // It's critical that we pass BOTH the orderId AND customerId
        Intent intent = new Intent(this, BillActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.ORDER_ID, orderId);
        bundle.putString(Constant.CUSTOMER_ID, customerId);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}