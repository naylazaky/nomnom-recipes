package com.nomnom.recipes.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nomnom.recipes.data.api.model.Category;
import com.nomnom.recipes.data.api.model.CategoryResponse;
import com.nomnom.recipes.data.api.model.Meal;
import com.nomnom.recipes.data.api.model.MealResponse;
import com.nomnom.recipes.data.repository.MealRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends AndroidViewModel {

    private final MealRepository repository;

    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<List<Meal>> meals = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isOffline = new MutableLiveData<>(false);

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new MealRepository(application);
    }

    public LiveData<List<Category>> getCategories() { return categories; }
    public LiveData<List<Meal>> getMeals() { return meals; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getIsOffline() { return isOffline; }

    public void loadCategories() {
        repository.getCategories(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(@NonNull Call<CategoryResponse> call,
                                   @NonNull Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories.setValue(response.body().getCategories());
                }
            }
            @Override
            public void onFailure(@NonNull Call<CategoryResponse> call,
                                  @NonNull Throwable t) {
            }
        });
    }

    public void loadMealsByCategory(String category) {
        isLoading.setValue(true);
        isOffline.setValue(false);
        error.setValue(null);

        if (!repository.isConnected()) {
            List<Meal> cached = repository.getPrefsManager().getCachedMeals(category);
            if (!cached.isEmpty()) {
                meals.setValue(cached);
                isOffline.setValue(true);
            } else {
                error.setValue("no_connection");
            }
            isLoading.setValue(false);
            return;
        }

        repository.getMealsByCategory(category, new Callback<MealResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealResponse> call,
                                   @NonNull Response<MealResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null
                        && response.body().getMeals() != null) {
                    List<Meal> result = response.body().getMeals();
                    meals.setValue(result);
                    repository.getPrefsManager().saveCachedMeals(category, result);
                    repository.getPrefsManager().saveLastCategory(category);
                } else {
                    error.setValue("empty");
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealResponse> call,
                                  @NonNull Throwable t) {
                isLoading.setValue(false);
                List<Meal> cached = repository.getPrefsManager().getCachedMeals(category);
                if (!cached.isEmpty()) {
                    meals.setValue(cached);
                    isOffline.setValue(true);
                } else {
                    error.setValue("failed");
                }
            }
        });
    }

    public void loadRandomMeal() {
        if (!repository.isConnected()) return;

        repository.getRandomMeal(new Callback<MealResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealResponse> call,
                                   @NonNull Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getMeals() != null) {
                    meals.setValue(response.body().getMeals());
                }
            }
            @Override
            public void onFailure(@NonNull Call<MealResponse> call,
                                  @NonNull Throwable t) {
            }
        });
    }
}