package com.pro.cakeshop.Fragment.Admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.pro.cakeshop.Activity.RevenueActivity;
import com.pro.cakeshop.LoginActivity;
import com.pro.cakeshop.R;

public class AdminSettingFragment extends Fragment {

    private TextView tvEmail;
    private TextView tvManageRevenue;
    private TextView tvManageCustomer;
    private TextView tvSignOut;

    public AdminSettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_setting, container, false);

        // Initialize views
        initViews(view);

        // Display admin email
        displayAdminInfo();

        // Set up click listeners
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        tvEmail = view.findViewById(R.id.tv_email);
        tvManageRevenue = view.findViewById(R.id.tv_manage_revenue);
        tvManageCustomer = view.findViewById(R.id.tv_manage_customer);
        tvSignOut = view.findViewById(R.id.tv_sign_out);
    }

    private void displayAdminInfo() {
        // You can get admin email from SharedPreferences if you stored it there
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "admin@admin.com");
        tvEmail.setText(email);
    }

    private void setupClickListeners() {
        // Navigate to Revenue Management screen
        tvManageRevenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRevenueManagement();
            }
        });

        // Navigate to Customer Management screen
        tvManageCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                navigateToCustomerManagement();
            }
        });

        // Handle sign out
        tvSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void navigateToRevenueManagement() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), RevenueActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Lỗi: Không thể mở RevenueActivity", Toast.LENGTH_SHORT).show();
        }
    }


//    private void navigateToCustomerManagement() {
//        // Replace current fragment with CustomerManagementFragment
//        try {
//            AdminCustomerFragment customerFragment = new AdminCustomerFragment();
//            getParentFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, customerFragment)
//                    .addToBackStack(null)
//                    .commit();
//        } catch (Exception e) {
//            Toast.makeText(getContext(), "Không thể mở trang quản lý khách hàng", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//    }

    private void signOut() {
//        // Clear user session data
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
//        editor.apply();

        // Navigate to Login screen
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();

        Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
    }
}