package com.pro.cakeshop.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.cakeshop.Adapter.CartAdapter;
import com.pro.cakeshop.Model.Banh;
import com.pro.cakeshop.Model.GioHang;
import com.pro.cakeshop.R;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private List<GioHang> cartList;
    private List<Banh> banhList;
    private TextView totalAmountTextView;
    private int totalAmount = 0;
    private String userId = "B002";  // Giả định ID khách hàng

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        recyclerView = view.findViewById(R.id.rcv_cart);
        totalAmountTextView = view.findViewById(R.id.tv_total);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cartList = new ArrayList<>();
        banhList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        fetchCartItems();

        return view;
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
                        Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchCakeDetails() {
        databaseReference.child("banh").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                banhList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Banh banh = dataSnapshot.getValue(Banh.class);
                    if (banh != null) {
                        banhList.add(banh);
                    }
                }
                updateCart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCart() {
        totalAmount = 0; // Reset trước khi tính lại
        for (GioHang item : cartList) {
            for (Banh banh : banhList) {
                if (item.getMaBanh().equals(banh.getMaBanh())) {
                    totalAmount += banh.getGia() * item.getSoLuong();
                }
            }
        }
        totalAmountTextView.setText("Tổng Tiền:"+ totalAmount + " VND");
        adapter = new CartAdapter(cartList, banhList, getContext()); // Sử dụng getContext() thay vì this
        recyclerView.setAdapter(adapter);
    }
}
