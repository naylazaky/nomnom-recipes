package com.nomnom.recipes.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nomnom.recipes.adapter.SearchAdapter;
import com.nomnom.recipes.databinding.FragmentSearchBinding;
import com.nomnom.recipes.ui.detail.DetailActivity;
import com.nomnom.recipes.utils.Constants;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private SearchViewModel viewModel;
    private SearchAdapter searchAdapter;
    private final Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable debounceRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        setupRecyclerView();
        setupSearchView();
        setupObservers();
    }

    private void setupRecyclerView() {
        searchAdapter = new SearchAdapter();
        binding.rvSearch.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSearch.setAdapter(searchAdapter);

        searchAdapter.setOnMealClickListener(meal -> {
            Intent intent = new Intent(requireContext(), DetailActivity.class);
            intent.putExtra(Constants.EXTRA_MEAL_ID, meal.getIdMeal());
            intent.putExtra(Constants.EXTRA_MEAL_NAME, meal.getStrMeal());
            intent.putExtra(Constants.EXTRA_MEAL_THUMB, meal.getStrMealThumb());
            startActivity(intent);
        });
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (debounceRunnable != null) {
                    debounceHandler.removeCallbacks(debounceRunnable);
                }

                if (newText.length() >= Constants.SEARCH_MIN_CHARS) {
                    debounceRunnable = () -> performSearch(newText);
                    debounceHandler.postDelayed(debounceRunnable, Constants.SEARCH_DEBOUNCE_MS);
                } else {
                    searchAdapter.setMeals(null);
                    binding.layoutEmpty.setVisibility(View.GONE);
                    binding.layoutError.setVisibility(View.GONE);
                }
                return true;
            }
        });
    }

    private void performSearch(String query) {
        if (query.trim().length() >= Constants.SEARCH_MIN_CHARS) {
            viewModel.searchMeals(query.trim());
        }
    }

    private void setupObservers() {
        viewModel.getSearchResults().observe(getViewLifecycleOwner(), meals -> {
            searchAdapter.setMeals(meals);
            binding.rvSearch.setVisibility(View.VISIBLE);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getIsEmpty().observe(getViewLifecycleOwner(), isEmpty -> {
            binding.layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            if (isEmpty) binding.rvSearch.setVisibility(View.GONE);
        });

        viewModel.getIsError().observe(getViewLifecycleOwner(), isError -> {
            binding.layoutError.setVisibility(isError ? View.VISIBLE : View.GONE);
            if (isError) binding.rvSearch.setVisibility(View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (debounceRunnable != null) {
            debounceHandler.removeCallbacks(debounceRunnable);
        }
        binding = null;
    }
}