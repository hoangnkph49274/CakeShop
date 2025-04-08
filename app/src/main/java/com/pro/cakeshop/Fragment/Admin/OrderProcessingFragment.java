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

import com.pro.cakeshop.Adapter.admin.ProcessingOrderAdapter;
import com.pro.cakeshop.Database.FirebaseHelper;
import com.pro.cakeshop.Model.DonHang;
import com.pro.cakeshop.R;

import java.util.ArrayList;
import java.util.List;

public class OrderProcessingFragment extends Fragment implements ProcessingOrderAdapter.OrderActionListener {

    private static final String TAG = "OrderProcessingFragment";
    private RecyclerView recyclerView;
    private ProcessingOrderAdapter orderAdapter;
    private List<DonHang> orderList;
    private FirebaseHelper firebaseHelper;

    public OrderProcessingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_processing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase helper
        firebaseHelper = new FirebaseHelper();

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.order_processing_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize order list
        orderList = new ArrayList<>();

        // Initialize adapter with action buttons
        orderAdapter = new ProcessingOrderAdapter(getContext(), orderList, this);
        recyclerView.setAdapter(orderAdapter);

        // Load processing orders
        loadProcessingOrders();
    }

    private void loadProcessingOrders() {
        // Show loading indicator if needed

        firebaseHelper.getAllDonHang(new FirebaseHelper.FirebaseCallback<List<DonHang>>() {
            @Override
            public void onSuccess(List<DonHang> result) {
                // Filter orders with "Processing" status
                List<DonHang> processingOrders = new ArrayList<>();
                for (DonHang order : result) {
                    if ("Chưa nhận hàng".equals(order.getTrangThai())) {
                        processingOrders.add(order);
                    }
                }

                // Update UI on main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Hide loading indicator if needed

                        // Update the adapter with filtered orders
                        orderList.clear();
                        orderList.addAll(processingOrders);
                        orderAdapter.notifyDataSetChanged();

                        // Show empty state if needed
                        if (processingOrders.isEmpty()) {
                            // You could show an empty state view here
                            Log.d(TAG, "No processing orders found");
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

    @Override
    public void onMarkAsCompleted(String maKH, String maDonHang) {
        firebaseHelper.updateOrderStatus(maKH, maDonHang, "Đã giao", new FirebaseHelper.FirebaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Reload orders to refresh the list
                        loadProcessingOrders();
                    });
                }
            }

            @Override
            public void onFailure(String message) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(),
                                "Failed to update order: " + message,
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    @Override
    public void onViewDetails(DonHang order) {
        // You can implement order detail viewing here
        // For example, opening a new fragment or dialog with order details

        Toast.makeText(getContext(), "View details for order: " + order.getMaDonHang(), Toast.LENGTH_SHORT).show();

        // Example: Navigate to order details fragment
        // OrderDetailsFragment detailsFragment = OrderDetailsFragment.newInstance(order.getMaKH(), order.getMaDonHang());
        // getParentFragmentManager().beginTransaction()
        //     .replace(R.id.fragment_container, detailsFragment)
        //     .addToBackStack(null)
        //     .commit();
    }
}