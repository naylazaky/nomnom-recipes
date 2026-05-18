package com.nomnom.recipes.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nomnom.recipes.R;
import com.nomnom.recipes.data.api.model.Meal;

import java.util.ArrayList;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<Meal> meals = new ArrayList<>();
    private OnMealClickListener listener;

    public interface OnMealClickListener {
        void onMealClick(Meal meal);
    }

    public void setOnMealClickListener(OnMealClickListener listener) {
        this.listener = listener;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        holder.bind(meals.get(position));
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    class MealViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgMeal;
        private final TextView tvMealName;
        private final TextView tvMealArea;

        MealViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMeal = itemView.findViewById(R.id.imgMeal);
            tvMealName = itemView.findViewById(R.id.tvMealName);
            tvMealArea = itemView.findViewById(R.id.tvMealArea);
        }

        void bind(Meal meal) {
            tvMealName.setText(meal.getStrMeal());
            tvMealArea.setText(meal.getStrArea() != null ? meal.getStrArea() : meal.getStrCategory());

            Glide.with(itemView.getContext())
                    .load(meal.getStrMealThumb())
                    .placeholder(R.color.shimmer_base)
                    .centerCrop()
                    .into(imgMeal);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onMealClick(meal);
            });
        }
    }
}