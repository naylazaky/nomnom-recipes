package com.nomnom.recipes.ui.detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.nomnom.recipes.R;
import com.nomnom.recipes.data.api.model.Meal;
import com.nomnom.recipes.databinding.ActivityDetailBinding;
import com.nomnom.recipes.utils.Constants;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    private DetailViewModel viewModel;
    private String mealId;
    private String mealName;
    private String mealThumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mealId    = getIntent().getStringExtra(Constants.EXTRA_MEAL_ID);
        mealName  = getIntent().getStringExtra(Constants.EXTRA_MEAL_NAME);
        mealThumb = getIntent().getStringExtra(Constants.EXTRA_MEAL_THUMB);

        setupToolbar();
        setupViewModel();
        setupFab();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        binding.tvToolbarTitle.setText(mealName);
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Glide.with(this)
                .load(mealThumb)
                .placeholder(R.color.shimmer_base)
                .centerCrop()
                .into(binding.imgHero);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        viewModel.getMealDetail().observe(this, detail -> {
            if (detail == null) return;

            binding.chipCategory.setText(detail.getStrCategory());
            binding.chipArea.setText(detail.getStrArea());

            binding.tvInstructions.setText(detail.getStrInstructions());

            populateIngredients(detail);

            if (detail.getStrYoutube() != null && !detail.getStrYoutube().isEmpty()) {
                binding.btnYoutube.setVisibility(View.VISIBLE);
                binding.btnYoutube.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(detail.getStrYoutube()));
                    startActivity(intent);
                });
            }
        });

        viewModel.getIsFavorite().observe(this, isFav -> {
            binding.fabFavorite.setImageResource(
                    isFav ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite
            );
        });

        // Load detail
        if (mealId != null) viewModel.loadDetail(mealId);
    }

    private void setupFab() {
        binding.fabFavorite.setOnClickListener(v -> {
            Meal meal = new Meal();
            meal.setIdMeal(mealId);
            meal.setStrMeal(mealName);
            meal.setStrMealThumb(mealThumb);

            viewModel.toggleFavorite(meal);

            boolean isFav = Boolean.TRUE.equals(viewModel.getIsFavorite().getValue());
            Snackbar.make(binding.getRoot(),
                    isFav ? getString(R.string.added_to_favorite)
                            : getString(R.string.removed_from_favorite),
                    Snackbar.LENGTH_SHORT).show();
        });
    }

    private void populateIngredients(com.nomnom.recipes.data.api.model.MealDetail detail) {
        binding.layoutIngredients.removeAllViews();

        String[] ingredients = {
                detail.getStrIngredient1(),  detail.getStrIngredient2(),
                detail.getStrIngredient3(),  detail.getStrIngredient4(),
                detail.getStrIngredient5(),  detail.getStrIngredient6(),
                detail.getStrIngredient7(),  detail.getStrIngredient8(),
                detail.getStrIngredient9(),  detail.getStrIngredient10(),
                detail.getStrIngredient11(), detail.getStrIngredient12(),
                detail.getStrIngredient13(), detail.getStrIngredient14(),
                detail.getStrIngredient15(), detail.getStrIngredient16(),
                detail.getStrIngredient17(), detail.getStrIngredient18(),
                detail.getStrIngredient19(), detail.getStrIngredient20()
        };

        String[] measures = {
                detail.getStrMeasure1(),  detail.getStrMeasure2(),
                detail.getStrMeasure3(),  detail.getStrMeasure4(),
                detail.getStrMeasure5(),  detail.getStrMeasure6(),
                detail.getStrMeasure7(),  detail.getStrMeasure8(),
                detail.getStrMeasure9(),  detail.getStrMeasure10(),
                detail.getStrMeasure11(), detail.getStrMeasure12(),
                detail.getStrMeasure13(), detail.getStrMeasure14(),
                detail.getStrMeasure15(), detail.getStrMeasure16(),
                detail.getStrMeasure17(), detail.getStrMeasure18(),
                detail.getStrMeasure19(), detail.getStrMeasure20()
        };

        for (int i = 0; i < ingredients.length; i++) {
            String ing = ingredients[i];
            String mea = measures[i];

            if (ing != null && !ing.trim().isEmpty()) {
                String text = "• " + (mea != null ? mea.trim() + " " : "") + ing.trim();
                TextView tv = new TextView(this);
                tv.setText(text);
                tv.setTextSize(14f);
                tv.setPadding(0, 4, 0, 4);
                tv.setTextColor(getResources().getColor(
                        android.R.color.tab_indicator_text, getTheme()));
                binding.layoutIngredients.addView(tv);
            }
        }
    }
}