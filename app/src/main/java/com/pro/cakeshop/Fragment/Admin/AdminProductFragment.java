package com.pro.cakeshop.Fragment.Admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pro.cakeshop.Adapter.ProductAdapter;
import com.pro.cakeshop.Database.FirebaseHelper;
import com.pro.cakeshop.Model.Banh;
import com.pro.cakeshop.Model.Loai;
import com.pro.cakeshop.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminProductFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Banh> productList = new ArrayList<>();
    private List<Loai> loaiList = new ArrayList<>();
    private Map<String, String> loaiMap = new HashMap<>();
    private EditText edtSearch;
    private ImageView imgSearch;
    private FloatingActionButton btnAdd;
    private FirebaseHelper firebaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_product, container, false);

        firebaseHelper = new FirebaseHelper();

        initViews(view);
        loadLoaiList();
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
        productAdapter = new ProductAdapter(productList,loaiMap, this);
        recyclerView.setAdapter(productAdapter);
    }

    private void loadProducts() {
        firebaseHelper.getListBanh(new FirebaseHelper.FirebaseCallback<List<Banh>>() {
            @Override
            public void onSuccess(List<Banh> result) {
                if (result == null || result.isEmpty()) {
                    Log.e("AdminProductFragment", "Danh sách bánh rỗng!");
                } else {
                    Log.d("AdminProductFragment", "Số lượng bánh: " + result.size());
                }

                productList.clear();
                productList.addAll(result);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                Log.e("AdminProductFragment", "Lỗi khi lấy dữ liệu: " + error);
                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
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
        productAdapter.updateList(filteredList);
    }

    private void setupAddButton() {
        btnAdd.setOnClickListener(v -> {
            loadLoaiList(); // Đảm bảo loại đã được tải trước
            new android.os.Handler().postDelayed(this::showAddProductDialog, 1000); // Đợi 1 giây trước khi mở hộp thoại
        });
    }


    private void loadLoaiList() {
        firebaseHelper.getListLoai(new FirebaseHelper.FirebaseCallback<List<Loai>>() {
            @Override
            public void onSuccess(List<Loai> result) {
                if (loaiMap == null) {
                    loaiMap = new HashMap<>();
                }
                loaiMap.clear();  // Đảm bảo không bị null

                loaiList.clear();
                loaiList.addAll(result);

                for (Loai loai : result) {
                    loaiMap.put(loai.getMaLoai(), loai.getTenLoai());
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), "Lỗi tải danh sách loại: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thêm sản phẩm mới");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);

        EditText edtName = dialogView.findViewById(R.id.edt_product_name);
        EditText edtPrice = dialogView.findViewById(R.id.edt_product_price);
        EditText edtImageUrl = dialogView.findViewById(R.id.edt_product_image_url);
        EditText edtMoTa = dialogView.findViewById(R.id.edt_product_description);
        Spinner spinnerLoai = dialogView.findViewById(R.id.spinner_loai);

        // Đợi danh sách loại được load
        if (loaiMap.isEmpty()) {
            Toast.makeText(getContext(), "Danh sách loại chưa được tải, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật Spinner nếu có dữ liệu
        List<String> loaiNames = new ArrayList<>(loaiMap.values());
        ArrayAdapter<String> loaiAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                loaiNames
        );
        loaiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoai.setAdapter(loaiAdapter);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = edtName.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String imageUrl = edtImageUrl.getText().toString().trim();
            String moTa = edtMoTa.getText().toString().trim();
            String tenLoai = spinnerLoai.getSelectedItem().toString();

            if (!name.isEmpty() && !priceStr.isEmpty() && !imageUrl.isEmpty()) {
                try {
                    double price = Double.parseDouble(priceStr);
                    String maLoai = getLoaiMaByTen(tenLoai);
                    addProduct(name, price, imageUrl, moTa, maLoai);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    // Phương thức trợ giúp để lấy mã loại từ tên loại
    private String getLoaiMaByTen(String tenLoai) {
        for (Map.Entry<String, String> entry : loaiMap.entrySet()) {
            if (entry.getValue().equals(tenLoai)) {
                return entry.getKey();
            }
        }
        return null; // Trả về null nếu không tìm thấy
    }

    private void addProduct(String name, double price, String imageUrl, String moTa, String maLoai) {
        Banh newBanh = new Banh();
        newBanh.setTenBanh(name);
        newBanh.setGia(price);
        newBanh.setHinhAnh(imageUrl);
        newBanh.setMoTa(moTa);
        newBanh.setMaLoai(maLoai);

        firebaseHelper.addBanh(newBanh, new FirebaseHelper.FirebaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getContext(), "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                loadProducts(); // Refresh the list
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cập nhật phương thức updateProduct để hỗ trợ cập nhật đầy đủ thông tin
    public void updateProduct(Banh banh) {
        firebaseHelper.updateBanhFull(banh, new FirebaseHelper.FirebaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                loadProducts(); // Refresh the list
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public String getLoaiTenByMa(String maLoai) {
        return loaiMap.get(maLoai); // Lấy tên loại từ HashMap
    }

    public void deleteProduct(Banh banh) {
        // Confirm delete dialog
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    firebaseHelper.deleteBanh(banh.getMaBanh(), new FirebaseHelper.FirebaseCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(getContext(), "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                            loadProducts(); // Refresh the list
                        }

                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}