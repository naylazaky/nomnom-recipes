package com.nomnom.recipes.data.repository;

import android.content.Context;

import com.nomnom.recipes.data.api.ApiClient;
import com.nomnom.recipes.data.api.ApiService;
import com.nomnom.recipes.data.api.model.CategoryResponse;
import com.nomnom.recipes.data.api.model.MealResponse;
import com.nomnom.recipes.data.prefs.PrefsManager;
import com.nomnom.recipes.utils.NetworkUtils;

import retrofit2.Callback;

public class MealRepository {

    private final ApiService apiService;
    private final PrefsManager prefsManager;
    private final Context context;

    public MealRepository(Context context) {
        this.context = context.getApplicationContext();
        this.apiService = ApiClient.getInstance().getApiService();
        this.prefsManager = PrefsManager.getInstance(context);
    }

    public void getCategories(Callback<CategoryResponse> callback) {
        apiService.getCategories().enqueue(callback);
    }

    public void getMealsByCategory(String category, Callback<MealResponse> callback) {
        apiService.getMealsByCategory(category).enqueue(callback);
    }

    public void searchMeals(String query, Callback<MealResponse> callback) {
        apiService.searchMeals(query).enqueue(callback);
    }

    public void getMealDetail(String id, Callback<MealResponse> callback) {
        apiService.getMealDetail(id).enqueue(callback);
    }

    public void getRandomMeal(Callback<MealResponse> callback) {
        apiService.getRandomMeal().enqueue(callback);
    }

    public boolean isConnected() {
        return NetworkUtils.isConnected(context);
    }

    public PrefsManager getPrefsManager() {
        return prefsManager;
    }
}