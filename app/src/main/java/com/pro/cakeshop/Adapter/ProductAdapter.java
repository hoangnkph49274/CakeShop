package com.pro.cakeshop.Adapter;

import android.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pro.cakeshop.Fragment.Admin.AdminProductFragment;
import com.pro.cakeshop.Model.Banh;
import com.pro.cakeshop.Model.Loai;
import com.pro.cakeshop.R;
import com.pro.cakeshop.Database.FirebaseHelper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Banh> productList;
    private AdminProductFragment adminProductFragment;
    private FirebaseHelper firebaseHelper;
    private Map<String, String> loaiMap = new HashMap<>();

    public ProductAdapter(List<Banh> productList, Map<String, String> loaiMap, AdminProductFragment adminProductFragment) {
        this.productList = productList;
        this.loaiMap = loaiMap;
        this.adminProductFragment = adminProductFragment;
        this.firebaseHelper = new FirebaseHelper();
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Banh banh = productList.get(position);
        holder.tvName.setText(banh.getTenBanh());
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        holder.tvPriceSale.setText(decimalFormat.format(banh.getGia()) + " đ");
        // Lấy tên loại từ AdminProductFragment
        String tenLoai = loaiMap.get(banh.getMaLoai());


        if (tenLoai != null) {
            holder.tv_category.setText(tenLoai);
        } else {
            holder.tv_category.setText("Không xác định");
        }
        Glide.with(holder.itemView.getContext()).load(banh.getHinhAnh()).into(holder.imgProduct);

        holder.imgEdit.setOnClickListener(v -> showEditDialog(banh));
        holder.imgDelete.setOnClickListener(v -> adminProductFragment.deleteProduct(banh));
    }

    private String formatCurrency(long amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); // Set separator as dot
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(amount);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateList(List<Banh> newList) {
        this.productList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgEdit, imgDelete;
        TextView tvName, tvPriceSale, tv_category;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPriceSale = itemView.findViewById(R.id.tv_price);
            tv_category = itemView.findViewById(R.id.tv_category);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }

    private void showEditDialog(Banh banh) {
        AlertDialog.Builder builder = new AlertDialog.Builder(adminProductFragment.getContext());
        builder.setTitle("Chỉnh sửa sản phẩm");

        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(adminProductFragment.getContext())
                .inflate(R.layout.dialog_edit_product, null);
        builder.setView(dialogView);

        // Find views
        EditText edtName = dialogView.findViewById(R.id.edt_product_name);
        EditText edtPrice = dialogView.findViewById(R.id.edt_product_price);
        EditText edtImageUrl = dialogView.findViewById(R.id.edt_product_image_url);
        EditText edtMoTa = dialogView.findViewById(R.id.edt_product_description);
        Spinner spinnerLoai = dialogView.findViewById(R.id.spinner_loai);

        // Populate current product data
        edtName.setText(banh.getTenBanh());
        edtPrice.setText(String.valueOf(banh.getGia()));
        edtImageUrl.setText(banh.getHinhAnh());
        edtMoTa.setText(banh.getMoTa());

        // Setup Loai Spinner
        firebaseHelper.getListLoai(new FirebaseHelper.FirebaseCallback<List<Loai>>() {
            @Override
            public void onSuccess(List<Loai> result) {
                List<String> loaiNames = new ArrayList<>();
                loaiMap.clear(); // Clear existing map

                for (Loai loai : result) {
                    loaiNames.add(loai.getTenLoai());
                    loaiMap.put(loai.getMaLoai(), loai.getTenLoai());
                }

                ArrayAdapter<String> loaiAdapter = new ArrayAdapter<>(
                        adminProductFragment.getContext(),
                        android.R.layout.simple_spinner_item,
                        loaiNames
                );
                loaiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLoai.setAdapter(loaiAdapter);

                // Set current category selection
                if (banh.getMaLoai() != null) {
                    String currentLoaiName = loaiMap.get(banh.getMaLoai());
                    if (currentLoaiName != null) {
                        int spinnerPosition = loaiAdapter.getPosition(currentLoaiName);
                        spinnerLoai.setSelection(spinnerPosition);
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(adminProductFragment.getContext(), "Lỗi tải danh sách loại: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            // Validate and update product
            String name = edtName.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String imageUrl = edtImageUrl.getText().toString().trim();
            String moTa = edtMoTa.getText().toString().trim();
            String tenLoai = spinnerLoai.getSelectedItem().toString();

            if (!name.isEmpty() && !priceStr.isEmpty() && !imageUrl.isEmpty()) {
                try {
                    double price = Double.parseDouble(priceStr);

                    // Find maLoai for selected tenLoai
                    String maLoai = getLoaiMaByTen(tenLoai);

                    // Update Banh object
                    banh.setTenBanh(name);
                    banh.setGia(price);
                    banh.setHinhAnh(imageUrl);
                    banh.setMoTa(moTa);
                    banh.setMaLoai(maLoai);

                    // Call update method in AdminProductFragment
                    adminProductFragment.updateProduct(banh);
                } catch (NumberFormatException e) {
                    Toast.makeText(adminProductFragment.getContext(), "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(adminProductFragment.getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Helper method to get maLoai from tenLoai
    private String getLoaiMaByTen(String tenLoai) {
        for (Map.Entry<String, String> entry : loaiMap.entrySet()) {
            if (entry.getValue().equals(tenLoai)) {
                return entry.getKey();
            }
        }
        return null;
    }
}