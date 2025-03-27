package com.pro.cakeshop.Fragment.Admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.cakeshop.R;
import com.pro.cakeshop.adapter.admin.CategoryAdminAdapter;
import com.pro.cakeshop.Model.Loai;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminCategoryFragment extends Fragment {

    private RecyclerView rcvData;
    private FloatingActionButton btnAdd;
    private EditText edtSearch;
    private DatabaseReference databaseReference;
    private List<Loai> loaiList;
    private CategoryAdminAdapter categoryAdminAdapter;

    public AdminCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_category, container, false);

        rcvData = view.findViewById(R.id.rcv_data);
        btnAdd = view.findViewById(R.id.btn_add);
        edtSearch = view.findViewById(R.id.edt_search_name);

        databaseReference = FirebaseDatabase.getInstance().getReference("loai");
        loaiList = new ArrayList<>();
        categoryAdminAdapter = new CategoryAdminAdapter(loaiList, this);

        rcvData.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvData.setAdapter(categoryAdminAdapter);

        btnAdd.setOnClickListener(v -> showAddDialog());
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        loadLoaiBanh();

        return view;
    }

    private void loadLoaiBanh() {
        Log.d("AdminCategoryFragment", "Đang tải dữ liệu từ Firebase...");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    loaiList.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Loai loai = data.getValue(Loai.class);
                        if (loai != null) {
                            loaiList.add(loai);
                            Log.d("AdminCategoryFragment", "Đã tải: " + loai.getTenLoai());
                        }
                    }
                    categoryAdminAdapter.updateList(loaiList);
                } else {
                    Log.d("AdminCategoryFragment", "Không có dữ liệu trên Firebase.");
                    Toast.makeText(getContext(), "Không có dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AdminCategoryFragment", "Lỗi tải dữ liệu: " + error.getMessage());
                Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm loại bánh");

        final EditText input = new EditText(getContext());
        input.setHint("Nhập tên loại bánh...");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String tenLoai = input.getText().toString().trim();
            if (!tenLoai.isEmpty()) {
                String id = databaseReference.push().getKey();
                Loai loai = new Loai(id, tenLoai);
                // Gọi addOnSuccessListener để reload danh sách sau khi thêm thành công
                databaseReference.child(id).setValue(loai)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Thêm thành công!", Toast.LENGTH_SHORT).show();
                            loadLoaiBanh();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), "Lỗi khi thêm: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(getContext(), "Tên không được để trống!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void updateLoai(String maLoai, String newName) {
        if (maLoai == null || maLoai.isEmpty()) {
            Toast.makeText(getContext(), "Lỗi: Mã loại không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.child(maLoai).child("tenLoai").setValue(newName)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    loadLoaiBanh();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void deleteLoai(Loai loai) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa loại bánh này không?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            databaseReference.child(loai.getMaLoai()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Xóa thành công!", Toast.LENGTH_SHORT).show();
                        loadLoaiBanh();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi xóa!", Toast.LENGTH_SHORT).show());
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void filterList(String keyword) {
        List<Loai> filteredList = new ArrayList<>();
        for (Loai loai : loaiList) {
            if (loai.getTenLoai().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(loai);
            }
        }
        categoryAdminAdapter.updateList(filteredList);
    }
}