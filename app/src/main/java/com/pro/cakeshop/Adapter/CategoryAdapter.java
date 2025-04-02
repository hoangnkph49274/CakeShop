package com.pro.cakeshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.cakeshop.Model.Loai;
import com.pro.cakeshop.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Loai> categoryList;
    private Context context;
    private int selectedPosition = 0;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(int position);
    }

    public CategoryAdapter(Context context, List<Loai> categoryList, OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Loai category = categoryList.get(position);
        holder.categoryNameTextView.setText(category.getTenLoai());

        // Set selected state
        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.category_selected);
            holder.categoryNameTextView.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        } else {
            holder.itemView.setBackgroundResource(R.drawable.category_normal);
            holder.categoryNameTextView.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(selectedPosition);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    setSelectedPosition(position);
                    listener.onCategoryClick(position);
                }
            });
        }
    }
}