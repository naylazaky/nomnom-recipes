package com.nomnom.recipes.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.nomnom.recipes.R;
import com.nomnom.recipes.data.api.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories = new ArrayList<>();
    private OnCategoryClickListener listener;
    private int selectedPosition = 0;

    public interface OnCategoryClickListener {
        void onCategoryClick(String categoryName);
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        int old = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(old);
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(categories.get(position), position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final Chip chip;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = (Chip) itemView;
        }

        void bind(Category category, boolean isSelected) {
            chip.setText(category.getStrCategory());
            chip.setChecked(isSelected);

            chip.setOnClickListener(v -> {
                if (listener != null) {
                    setSelectedPosition(getAdapterPosition());
                    listener.onCategoryClick(category.getStrCategory());
                }
            });
        }
    }
}