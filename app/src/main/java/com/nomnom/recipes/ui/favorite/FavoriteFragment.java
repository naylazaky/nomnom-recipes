package com.nomnom.recipes.ui.favorite;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.nomnom.recipes.R;
import com.nomnom.recipes.adapter.FavoriteAdapter;
import com.nomnom.recipes.databinding.FragmentFavoriteBinding;
import com.nomnom.recipes.ui.detail.DetailActivity;
import com.nomnom.recipes.utils.Constants;

public class FavoriteFragment extends Fragment {

    private FragmentFavoriteBinding binding;
    private FavoriteViewModel viewModel;
    private FavoriteAdapter favoriteAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);

        setupRecyclerView();
        setupObservers();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadFavorites();
    }

    private void setupRecyclerView() {
        favoriteAdapter = new FavoriteAdapter();
        binding.rvFavorites.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvFavorites.setAdapter(favoriteAdapter);

        favoriteAdapter.setOnMealClickListener(meal -> {
            Intent intent = new Intent(requireContext(), DetailActivity.class);
            intent.putExtra(Constants.EXTRA_MEAL_ID, meal.getIdMeal());
            intent.putExtra(Constants.EXTRA_MEAL_NAME, meal.getStrMeal());
            intent.putExtra(Constants.EXTRA_MEAL_THUMB, meal.getStrMealThumb());
            startActivity(intent);
        });

        favoriteAdapter.setOnMealLongClickListener(meal ->
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.dialog_delete_title)
                        .setMessage(R.string.dialog_delete_message)
                        .setPositiveButton(R.string.dialog_yes, (d, w) ->
                                viewModel.removeFavorite(meal.getIdMeal()))
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .show()
        );
    }

    private void setupObservers() {
        viewModel.getFavorites().observe(getViewLifecycleOwner(), meals -> {
            if (meals == null || meals.isEmpty()) {
                binding.layoutEmpty.setVisibility(View.VISIBLE);
                binding.rvFavorites.setVisibility(View.GONE);
                favoriteAdapter.setMeals(null);
            } else {
                binding.layoutEmpty.setVisibility(View.GONE);
                binding.rvFavorites.setVisibility(View.VISIBLE);
                favoriteAdapter.setMeals(meals);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}