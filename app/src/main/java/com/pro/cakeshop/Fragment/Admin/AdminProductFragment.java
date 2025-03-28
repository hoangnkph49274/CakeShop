package com.pro.cakeshop.Fragment.Admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pro.cakeshop.Adapter.ProductAdapter;
import com.pro.cakeshop.Database.FirebaseHelper;
import com.pro.cakeshop.Model.Banh;
import com.pro.cakeshop.R;

import java.util.ArrayList;
import java.util.List;

public class AdminProductFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Banh> productList = new ArrayList<>();
    private EditText edtSearch;
    private ImageView imgSearch;
    private FloatingActionButton btnAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_product, container, false);
        initViews(view);
        setupRecyclerView();
        loadProducts();
        setupSearchFunctionality();
        setupAddButton();
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rcv_data);
        edtSearch = view.findViewById(R.id.edt_search_name);
        imgSearch = view.findViewById(R.id.img_search);
        btnAdd = view.findViewById(R.id.btn_add);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new ProductAdapter(productList, new ProductAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Banh banh) {
                // Xử lý chỉnh sửa sản phẩm
            }

            @Override
            public void onDeleteClick(Banh banh) {
                // Xử lý xóa sản phẩm
            }
        });
        recyclerView.setAdapter(productAdapter);
    }

    private void loadProducts() {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.getListBanh(new FirebaseHelper.FirebaseCallback<List<Banh>>() {
            @Override
            public void onSuccess(List<Banh> result) {
                productList.clear();
                productList.addAll(result);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                // Xử lý lỗi nếu có
            }
        });
    }

    private void setupSearchFunctionality() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterProducts(String query) {
        List<Banh> filteredList = new ArrayList<>();
        for (Banh banh : productList) {
            if (banh.getTenBanh().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(banh);
            }
        }
        productAdapter.setProductList(filteredList);
    }

    private void setupAddButton() {
        btnAdd.setOnClickListener(v -> {
            // Xử lý thêm sản phẩm mới
        });
    }
}