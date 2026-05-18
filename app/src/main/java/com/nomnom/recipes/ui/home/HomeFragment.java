package com.nomnom.recipes.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nomnom.recipes.adapter.CategoryAdapter;
import com.nomnom.recipes.adapter.MealAdapter;
import com.nomnom.recipes.data.api.model.Category;
import com.nomnom.recipes.data.prefs.PrefsManager;
import com.nomnom.recipes.databinding.FragmentHomeBinding;
import com.nomnom.recipes.ui.detail.DetailActivity;
import com.nomnom.recipes.utils.Constants;

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private MealAdapter mealAdapter;
    private CategoryAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupRecyclerViews();
        setupObservers();
        setupClickListeners();

        viewModel.loadCategories();
        String lastCategory = PrefsManager.getInstance(requireContext()).getLastCategory();
        viewModel.loadMealsByCategory(lastCategory);
    }

    private void setupRecyclerViews() {
        categoryAdapter = new CategoryAdapter();
        binding.rvCategories.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvCategories.setAdapter(categoryAdapter);

        categoryAdapter.setOnCategoryClickListener(category ->
                viewModel.loadMealsByCategory(category));

        mealAdapter = new MealAdapter();
        binding.rvMeals.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvMeals.setAdapter(mealAdapter);

        mealAdapter.setOnMealClickListener(meal -> {
            Intent intent = new Intent(requireContext(), DetailActivity.class);
            intent.putExtra(Constants.EXTRA_MEAL_ID, meal.getIdMeal());
            intent.putExtra(Constants.EXTRA_MEAL_NAME, meal.getStrMeal());
            intent.putExtra(Constants.EXTRA_MEAL_THUMB, meal.getStrMealThumb());
            startActivity(intent);
        });
    }

    private void setupObservers() {
        viewModel.getCategories().observe(getViewLifecycleOwner(), this::updateCategories);

        viewModel.getMeals().observe(getViewLifecycleOwner(), meals -> {
            mealAdapter.setMeals(meals);
            binding.rvMeals.setVisibility(View.VISIBLE);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                showShimmer();
            } else {
                hideShimmer();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), err -> {
            if (err != null) {
                showError();
            } else {
                hideError();
            }
        });

        viewModel.getIsOffline().observe(getViewLifecycleOwner(), isOffline -> {
            binding.tvOfflineBanner.setVisibility(isOffline ? View.VISIBLE : View.GONE);
        });
    }

    private void setupClickListeners() {
        binding.btnRandom.setOnClickListener(v -> viewModel.loadRandomMeal());

        binding.btnRetry.setOnClickListener(v -> {
            String lastCategory = PrefsManager.getInstance(requireContext()).getLastCategory();
            viewModel.loadMealsByCategory(lastCategory);
        });
    }

    private void updateCategories(List<Category> categories) {
        categoryAdapter.setCategories(categories);
    }

    private void showShimmer() {
        binding.shimmerContainer.getRoot().setVisibility(View.VISIBLE);
        binding.shimmerContainer.shimmerLayout.startShimmer();
        binding.rvMeals.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.GONE);
    }

    private void hideShimmer() {
        binding.shimmerContainer.shimmerLayout.stopShimmer();
        binding.shimmerContainer.getRoot().setVisibility(View.GONE);
    }

    private void showError() {
        binding.layoutError.setVisibility(View.VISIBLE);
        binding.rvMeals.setVisibility(View.GONE);
        binding.shimmerContainer.getRoot().setVisibility(View.GONE);
    }

    private void hideError() {
        binding.layoutError.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}