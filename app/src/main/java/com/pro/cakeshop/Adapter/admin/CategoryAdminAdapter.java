package com.pro.cakeshop.adapter.admin;

import android.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pro.cakeshop.Fragment.Admin.AdminCategoryFragment;
import com.pro.cakeshop.Model.Loai;
import com.pro.cakeshop.R;
import java.util.ArrayList;
import java.util.List;

public class CategoryAdminAdapter extends RecyclerView.Adapter<CategoryAdminAdapter.CategoryViewHolder> {
    private List<Loai> loaiList;
    private AdminCategoryFragment adminCategoryFragment;

    public CategoryAdminAdapter(List<Loai> loaiList, AdminCategoryFragment adminCategoryFragment) {
        this.loaiList = loaiList;
        this.adminCategoryFragment = adminCategoryFragment;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Loai loai = loaiList.get(position);
        holder.tvName.setText(loai.getTenLoai());

        holder.imgEdit.setOnClickListener(v -> showEditDialog(loai));
        holder.imgDelete.setOnClickListener(v -> adminCategoryFragment.deleteLoai(loai));
    }

    @Override
    public int getItemCount() {
        return loaiList.size();
    }

    public void updateList(List<Loai> newList) {
        this.loaiList = new ArrayList<>(newList); // Gán danh sách mới hoàn toàn
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView imgEdit, imgDelete;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }

    private void showEditDialog(Loai loai) {
        AlertDialog.Builder builder = new AlertDialog.Builder(adminCategoryFragment.getContext());
        builder.setTitle("Chỉnh sửa loại bánh");

        final EditText input = new EditText(adminCategoryFragment.getContext());
        input.setText(loai.getTenLoai());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                adminCategoryFragment.updateLoai(loai.getMaLoai(), newName);
            } else {
                Toast.makeText(adminCategoryFragment.getContext(), "Tên không được để trống", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
