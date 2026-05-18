package com.nomnom.recipes.ui.detail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nomnom.recipes.data.api.model.Meal;
import com.nomnom.recipes.data.api.model.MealDetail;
import com.nomnom.recipes.data.api.model.MealResponse;
import com.nomnom.recipes.data.prefs.PrefsManager;
import com.nomnom.recipes.data.repository.MealRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailViewModel extends AndroidViewModel {

    private final MealRepository repository;
    private final PrefsManager prefsManager;

    private final MutableLiveData<MealDetail> mealDetail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);

    public DetailViewModel(@NonNull Application application) {
        super(application);
        repository = new MealRepository(application);
        prefsManager = PrefsManager.getInstance(application);
    }

    public LiveData<MealDetail> getMealDetail() { return mealDetail; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsFavorite() { return isFavorite; }

    public void loadDetail(String mealId) {
        isLoading.setValue(true);
        repository.getMealDetail(mealId, new Callback<MealResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealResponse> call,
                                   @NonNull Response<MealResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null
                        && response.body().getMeals() != null
                        && !response.body().getMeals().isEmpty()) {
                    Meal meal = response.body().getMeals().get(0);
                    com.google.gson.Gson gson = new com.google.gson.Gson();
                    String json = gson.toJson(meal);
                    MealDetail detail = gson.fromJson(json, MealDetail.class);
                    mealDetail.setValue(detail);
                    isFavorite.setValue(prefsManager.isFavorite(mealId));
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealResponse> call,
                                  @NonNull Throwable t) {
                isLoading.setValue(false);
            }
        });
    }

    public void toggleFavorite(Meal meal) {
        String id = meal.getIdMeal();
        if (prefsManager.isFavorite(id)) {
            prefsManager.removeFavorite(id);
            isFavorite.setValue(false);
        } else {
            prefsManager.addFavorite(meal);
            isFavorite.setValue(true);
        }
    }
}