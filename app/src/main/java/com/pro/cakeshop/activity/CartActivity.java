//package com.pro.cakeshop.activity;
//
//import android.os.Bundle;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.pro.cakeshop.Adapter.CartAdapter;
//import com.pro.cakeshop.Model.Banh;
//import com.pro.cakeshop.Model.GioHang;
//import com.pro.cakeshop.R;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class CartActivity extends AppCompatActivity {
//    private DatabaseReference databaseReference;
//    private RecyclerView recyclerView;
//    private CartAdapter adapter;
//    private List<GioHang> cartList;
//    private Map<String, Banh> banhMap;
//    private TextView totalAmountTextView;
//    private int totalAmount = 0;
//    private String userId = "B002";  // Giả định ID khách hàng
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_cart);
//
//        recyclerView = findViewById(R.id.rcv_cart);
//        totalAmountTextView = findViewById(R.id.tv_total);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        cartList = new ArrayList<>();
//        banhMap = new HashMap<>();
//
//        databaseReference = FirebaseDatabase.getInstance().getReference();
//
//        fetchCartItems();
//    }
//
//    private void fetchCartItems() {
//        databaseReference.child("gioHang").orderByChild("maKH").equalTo(userId)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        cartList.clear();
//                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                            GioHang item = dataSnapshot.getValue(GioHang.class);
//                            if (item != null) {
//                                cartList.add(item);
//                            }
//                        }
//                        fetchCakeDetails();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(CartActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private void fetchCakeDetails() {
//        databaseReference.child("banh").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                banhMap.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Banh banh = dataSnapshot.getValue(Banh.class);
//                    if (banh != null) {
//                        banhMap.put(banh.getMaBanh(), banh);
//                    }
//                }
//                updateCart();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(CartActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void updateCart() {
//        totalAmount = 0;
//        for (GioHang item : cartList) {
//            Banh banh = banhMap.get(item.getMaBanh());
//            if (banh != null) {
//                totalAmount += banh.getGia() * item.getSoLuong();
//            }
//        }
//        totalAmountTextView.setText(totalAmount + " VND");
//        adapter = new CartAdapter(cartList, new ArrayList<>(banhMap.values()), this);
//        recyclerView.setAdapter(adapter);
//    }
//}
