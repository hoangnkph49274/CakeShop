package com.pro.cakeshop.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.pro.cakeshop.Adapter.CategoryAdapter;
import com.pro.cakeshop.Adapter.ProductUserAdapter;
import com.pro.cakeshop.Database.FirebaseHelper;
import com.pro.cakeshop.Model.Banh;
import com.pro.cakeshop.Model.Loai;
import com.pro.cakeshop.R;
import com.pro.cakeshop.Activity.ProductDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener, ProductUserAdapter.OnProductClickListener {

    private RecyclerView categoryRecyclerView;
    private RecyclerView productsRecyclerView;
    private CategoryAdapter categoryAdapter;
    private ProductUserAdapter productAdapter;
    private List<Loai> loaiList;
    private List<Banh> banhList;
    private EditText searchEditText;
    private FirebaseHelper firebaseHelper;
    private View loadingView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Firebase Helper
        firebaseHelper = new FirebaseHelper();

        // Initialize views
        searchEditText = view.findViewById(R.id.searchEditText);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        productsRecyclerView = view.findViewById(R.id.productsRecyclerView);
        loadingView = view.findViewById(R.id.loadingView);

        // Show loading
        showLoading(true);

        // Setup search functionality
        setupSearch();

        // Initialize data from Firebase
        loadLoaiData();

        return view;
    }

    private void showLoading(boolean isLoading) {
        if (loadingView != null) {
            loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterProducts(s.toString());
            }
        });
    }

    private void filterProducts(String query) {
        if (banhList == null) return;

        List<Banh> filteredList = new ArrayList<>();
        for (Banh banh : banhList) {
            if (banh.getTenBanh().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(banh);
            }
        }
        productAdapter.updateProductList(filteredList);
    }

    private void loadLoaiData() {
        firebaseHelper.getListLoai(new FirebaseHelper.FirebaseCallback<List<Loai>>() {
            @Override
            public void onSuccess(List<Loai> result) {
                loaiList = result;
                setupCategoryRecyclerView();

                // After loading categories, load products
                loadBanhData();
            }

            @Override
            public void onFailure(String error) {
                showLoading(false);
                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBanhData() {
        firebaseHelper.getListBanh(new FirebaseHelper.FirebaseCallback<List<Banh>>() {
            @Override
            public void onSuccess(List<Banh> result) {
                banhList = result;
                showLoading(false);
                setupProductRecyclerView();
            }

            @Override
            public void onFailure(String error) {
                showLoading(false);
                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCategoryRecyclerView() {
        Loai allCategory = new Loai();
        allCategory.setMaLoai("all");
        allCategory.setTenLoai("Tất cả");

        // Cập nhật loaiList để bao gồm cả mục "Tất cả"
        List<Loai> updatedList = new ArrayList<>();
        updatedList.add(allCategory);
        updatedList.addAll(loaiList);
        loaiList = updatedList; // Gán lại loaiList

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(layoutManager);
        categoryAdapter = new CategoryAdapter(getContext(), loaiList, this);
        categoryRecyclerView.setAdapter(categoryAdapter);
        ViewPager2 viewPager = requireActivity().findViewById(R.id.viewpager_2);

        categoryRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                viewPager.setUserInputEnabled(false);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_UP) {
                    viewPager.setUserInputEnabled(true);
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                // Không cần xử lý
            }
        });
    }

    private void setupProductRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),1);
        productsRecyclerView.setLayoutManager(layoutManager);

        // Hiển thị tất cả sản phẩm khi khởi động
        productAdapter = new ProductUserAdapter(getContext(), banhList, this);
        productsRecyclerView.setAdapter(productAdapter);

        // Đồng thời có thể chọn mục "Tất cả" trong danh sách
        if (categoryAdapter != null) {
            categoryAdapter.setSelectedPosition(0); // Vị trí 0 là "Tất cả"
        }
    }

    private List<Banh> filterBanhByLoai(String maLoai) {
        List<Banh> filteredList = new ArrayList<>();
        if (banhList != null) {
            for (Banh banh : banhList) {
                if (banh.getMaLoai().equals(maLoai)) {
                    filteredList.add(banh);
                }
            }
        }
        return filteredList;
    }

    @Override
    public void onCategoryClick(int position) {
        // Get selected loai ID
        String maLoai = loaiList.get(position).getMaLoai();
        String tenLoai = loaiList.get(position).getTenLoai();

        List<Banh> filteredBanh;

        // Kiểm tra xem đây có phải là danh mục "Tất cả" không
        if ("all".equals(maLoai) || "Tất cả".equals(tenLoai)) {
            // Hiển thị tất cả sản phẩm
            filteredBanh = banhList;
        } else {
            // Filter products by category
            filteredBanh = filterBanhByLoai(maLoai);
        }

        // Update product adapter
        productAdapter.updateProductList(filteredBanh);
    }
    @Override
    public void onProductClick(Banh banh) {
        // Chuyển sang ProductDetailActivity và truyền mã bánh
        Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
        intent.putExtra("product_id", banh.getMaBanh());
        startActivity(intent);
    }
}