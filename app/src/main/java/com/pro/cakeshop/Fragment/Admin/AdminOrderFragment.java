package com.pro.cakeshop.Fragment.Admin;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.pro.cakeshop.R;

public class AdminOrderFragment extends Fragment {

    private Button btnProcessing, btnDone;
    private FrameLayout container;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_admin_order, container, false);

        btnProcessing = mView.findViewById(R.id.btnProcessing);
        btnDone = mView.findViewById(R.id.btnDone);
        this.container = mView.findViewById(R.id.order_fragment_container);

        btnProcessing.setOnClickListener(v -> {
            showFragment(new OrderProcessingFragment());
            setActiveButton(btnProcessing);
            setInactiveButton(btnDone);
        });

        btnDone.setOnClickListener(v -> {
            showFragment(new OrderDoneFragment());
            setActiveButton(btnDone);
            setInactiveButton(btnProcessing);
        });

        // Mặc định hiển thị tab đầu tiên và thiết lập màu
        showFragment(new OrderProcessingFragment());
        setActiveButton(btnProcessing);
        setInactiveButton(btnDone);

        return mView;
    }

    private void showFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.order_fragment_container, fragment)
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
