package com.pro.cakeshop.Fragment.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.cakeshop.Adapter.admin.OrderAdapter;
import com.pro.cakeshop.Database.FirebaseHelper;
import com.pro.cakeshop.Model.DonHang;
import com.pro.cakeshop.R;

import java.util.ArrayList;
import java.util.List;

public class OrderDoneFragment extends Fragment {

    private static final String TAG = "OrderDoneFragment";
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<DonHang> orderList;
    private FirebaseHelper firebaseHelper;

    public OrderDoneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_done, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase helper
        firebaseHelper = new FirebaseHelper();

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.order_done_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize order list
        orderList = new ArrayList<>();

        // Initialize adapter
        orderAdapter = new OrderAdapter(getContext(), orderList);
        recyclerView.setAdapter(orderAdapter);

        // Load completed orders
        loadCompletedOrders();
    }


    private void loadCompletedOrders() {
        // Show loading indicator if needed

        firebaseHelper.getAllDonHang(new FirebaseHelper.FirebaseCallback<List<DonHang>>() {
            @Override
            public void onSuccess(List<DonHang> result) {
                // Filter orders with "Completed" status
                List<DonHang> completedOrders = new ArrayList<>();
                for (DonHang order : result) {
                    if ("Đã giao".equals(order.getTrangThai())) {
                        completedOrders.add(order);
                    }
                }

                // Update UI on main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Hide loading indicator if needed

                        // Update the adapter with filtered orders
                        orderList.clear();
                        orderList.addAll(completedOrders);
                        orderAdapter.notifyDataSetChanged();

                        // Show empty state if needed
                        if (completedOrders.isEmpty()) {
                            // You could show an empty state view here
                            Log.d(TAG, "No completed orders found");
                        }
                    });
                }
            }

            @Override
            public void onFailure(String message) {
                // Handle error on main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Hide loading indicator if needed

                        // Show error message
                        Toast.makeText(getContext(),
                                "Failed to load orders: " + message,
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error loading orders: " + message);
                    });
                }
            }
        });
    }
}