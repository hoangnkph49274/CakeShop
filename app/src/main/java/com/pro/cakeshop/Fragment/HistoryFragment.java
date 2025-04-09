package com.pro.cakeshop.Fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pro.cakeshop.Activity.BillActivity;

import com.pro.cakeshop.Activity.UserInfoActivity;
import com.pro.cakeshop.Adapter.OrderHistoryAdapter;
import com.pro.cakeshop.Database.FirebaseHelper;
import com.pro.cakeshop.Fragment.Admin.OrderDoneFragment;
import com.pro.cakeshop.Fragment.Admin.OrderProcessingFragment;
import com.pro.cakeshop.Model.DonHang;
import com.pro.cakeshop.R;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HistoryFragment extends Fragment  {

    private FirebaseAuth mAuth;
    private Button btnProcessing, btnDone;
    private FrameLayout container;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_history, container, false);

        btnProcessing = mView.findViewById(R.id.btnProcessing);
        btnDone = mView.findViewById(R.id.btnDone);
        this.container = mView.findViewById(R.id.history_fragment_container);

        btnProcessing.setOnClickListener(v -> {
            showFragment(new UserOrderProcessingFragment());
            setActiveButton(btnProcessing);
            setInactiveButton(btnDone);
        });

        btnDone.setOnClickListener(v -> {
            showFragment(new UserOrderDoneFragment());
            setActiveButton(btnDone);
            setInactiveButton(btnProcessing);
        });

        // Mặc định hiển thị tab đầu tiên và thiết lập màu
        showFragment(new UserOrderProcessingFragment());
        setActiveButton(btnProcessing);
        setInactiveButton(btnDone);

        return mView;
    }

    private void showFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.history_fragment_container, fragment)
                .commit();
    }

    private void setActiveButton(Button button) {
        int selectedColor = ContextCompat.getColor(requireContext(), R.color.teal_200);
        button.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
        button.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
    }

    private void setInactiveButton(Button button) {
        int defaultColor = ContextCompat.getColor(requireContext(), R.color.gray);
        button.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
        button.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
    }
}