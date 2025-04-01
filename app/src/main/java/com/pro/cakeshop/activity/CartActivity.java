package com.pro.cakeshop.activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.cakeshop.Adapter.CartAdapter;
import com.pro.cakeshop.Model.Banh;
import com.pro.cakeshop.Model.GioHang;
import com.pro.cakeshop.Model.GioHangItem;
import com.pro.cakeshop.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private List<GioHang> cartList;
    private Map<String, Banh> banhMap;
    private TextView totalAmountTextView;
    private int totalAmount = 0;
    private String userId = "B002";  // Giả định ID khách hàng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.rcv_cart);
        totalAmountTextView = findViewById(R.id.tv_total);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartList = new ArrayList<>();
        banhMap = new HashMap<>();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        fetchCartItems();
    }

    private void fetchCartItems() {
        databaseReference.child("gioHang").orderByChild("maKH").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cartList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            GioHang item = dataSnapshot.getValue(GioHang.class);
                            if (item != null) {
                                cartList.add(item);
                            }
                        }
                        fetchCakeDetails();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CartActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchCakeDetails() {
        List<String> maBanhList = new ArrayList<>();
        for (GioHang item : cartList) {
            maBanhList.add(item.getMaBanh());
        }

        for (String maBanh : maBanhList) {
            databaseReference.child("banh").child(maBanh).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Banh banh = snapshot.getValue(Banh.class);
                    if (banh != null) {
                        banhMap.put(maBanh, banh);
                    }
                    if (banhMap.size() == maBanhList.size()) {
                        updateCart();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CartActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void updateCart() {
        totalAmount = 0;
        List<GioHangItem> gioHangItems = new ArrayList<>();

        for (GioHang item : cartList) {
            Banh banh = banhMap.get(item.getMaBanh());
            if (banh != null) {
                // Tạo GioHangItem từ GioHang và Banh
                GioHangItem gioHangItem = new GioHangItem();
                gioHangItem.setId(item.getMaBanh()); // hoặc một ID duy nhất khác
                gioHangItem.setSoLuong(item.getSoLuong());
//                gioHangItem.setGia(banh.getGia());
                gioHangItem.setTenBanh(banh.getTenBanh());
                gioHangItem.setHinhAnh(banh.getHinhAnh());

                gioHangItems.add(gioHangItem);
                totalAmount += banh.getGia() * item.getSoLuong();
            } else {
                Toast.makeText(this, "Sản phẩm không tồn tại!", Toast.LENGTH_SHORT).show();
            }
        }

        totalAmountTextView.setText(String.format("%,d VND", totalAmount));

        // Tạo CartItemListener
        CartAdapter.CartItemListener listener = new CartAdapter.CartItemListener() {
            @Override
            public void onQuantityChanged(int position, int newQuantity) {
                // Xử lý khi số lượng thay đổi
            }

            @Override
            public void onItemRemoved(int position) {
                // Xử lý khi item bị xóa
            }
        };

        adapter = new CartAdapter(gioHangItems, this, listener);
        recyclerView.setAdapter(adapter);
    }
}
