package com.nomnom.recipes.ui.search;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nomnom.recipes.data.api.model.Meal;
import com.nomnom.recipes.data.api.model.MealResponse;
import com.nomnom.recipes.data.repository.MealRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends AndroidViewModel {

    private final MealRepository repository;

    private final MutableLiveData<List<Meal>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isEmpty = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isError = new MutableLiveData<>(false);

    public SearchViewModel(@NonNull Application application) {
        super(application);
        repository = new MealRepository(application);
    }

    public LiveData<List<Meal>> getSearchResults() { return searchResults; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsEmpty() { return isEmpty; }
    public LiveData<Boolean> getIsError() { return isError; }

    public void searchMeals(String query) {
        if (!repository.isConnected()) {
            isError.setValue(true);
            return;
        }

        isLoading.setValue(true);
        isEmpty.setValue(false);
        isError.setValue(false);

        repository.searchMeals(query, new Callback<MealResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealResponse> call,
                                   @NonNull Response<MealResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null
                        && response.body().getMeals() != null
                        && !response.body().getMeals().isEmpty()) {
                    searchResults.setValue(response.body().getMeals());
                    isEmpty.setValue(false);
                } else {
                    searchResults.setValue(null);
                    isEmpty.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealResponse> call,
                                  @NonNull Throwable t) {
                isLoading.setValue(false);
                isError.setValue(true);
            }
        });
    }
}