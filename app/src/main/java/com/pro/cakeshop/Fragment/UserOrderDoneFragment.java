package com.pro.cakeshop.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pro.cakeshop.Adapter.OrderHistoryAdapter;
import com.pro.cakeshop.Adapter.admin.OrderAdapter;
import com.pro.cakeshop.Database.FirebaseHelper;
import com.pro.cakeshop.Model.DonHang;
import com.pro.cakeshop.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UserOrderDoneFragment extends Fragment {

    private RecyclerView recyclerViewOrders;
    private OrderHistoryAdapter adapter;
    private List<DonHang> orderList;
    private TextView tvNoOrders;
    private FirebaseHelper firebaseHelper;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_order_done, container, false);

        // Initialize views
        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);
        tvNoOrders = view.findViewById(R.id.tvNoOrders);

        // Initialize Firebase components
        firebaseHelper = new FirebaseHelper();
        mAuth = FirebaseAuth.getInstance();

        // Initialize order list and adapter
        orderList = new ArrayList<>();
        adapter = new OrderHistoryAdapter(getContext(),orderList);
        recyclerViewOrders.setAdapter(adapter);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Load completed orders
        loadCompletedOrders();

        return view;
    }

    private void loadCompletedOrders() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Show loading state
            tvNoOrders.setText("Loading orders...");
            tvNoOrders.setVisibility(View.VISIBLE);

            firebaseHelper.getLichSuDonHangByMaKH(userId, new FirebaseHelper.FirebaseCallback<List<DonHang>>() {
                @Override
                public void onSuccess(List<DonHang> result) {
                    // Filter only completed orders
                    List<DonHang> completedOrders = new ArrayList<>();
                    for (DonHang order : result) {
                        // Check if order status is completed/done
                        if (order.getTrangThai() != null &&
                                (order.getTrangThai().equals("Đã giao"))) {
                            completedOrders.add(order);
                        }
                    }

                    // Sort orders by date (newest first)
                    Collections.sort(completedOrders, new Comparator<DonHang>() {
                        @Override
                        public int compare(DonHang o1, DonHang o2) {
                            return o2.getNgayDat().compareTo(o1.getNgayDat());
                        }
                    });

                    // Update UI
                    if (completedOrders.isEmpty()) {
                        tvNoOrders.setText("No completed orders found");
                        tvNoOrders.setVisibility(View.VISIBLE);
                        recyclerViewOrders.setVisibility(View.GONE);
                    } else {
                        tvNoOrders.setVisibility(View.GONE);
                        recyclerViewOrders.setVisibility(View.VISIBLE);
                        orderList.clear();
                        orderList.addAll(completedOrders);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(String message) {
                    Log.e("UserOrderDone", "Error loading orders: " + message);
                    tvNoOrders.setText("Failed to load orders: " + message);
                    tvNoOrders.setVisibility(View.VISIBLE);
                    recyclerViewOrders.setVisibility(View.GONE);
                }
            });
        } else {
            tvNoOrders.setText("Please log in to view your orders");
            tvNoOrders.setVisibility(View.VISIBLE);
            recyclerViewOrders.setVisibility(View.GONE);
        }
    }
}