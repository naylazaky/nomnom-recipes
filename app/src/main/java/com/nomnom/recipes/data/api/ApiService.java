package com.nomnom.recipes.data.api;

import com.nomnom.recipes.data.api.model.CategoryResponse;
import com.nomnom.recipes.data.api.model.MealResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("categories.php")
    Call<CategoryResponse> getCategories();

    @GET("filter.php")
    Call<MealResponse> getMealsByCategory(@Query("c") String category);

    @GET("search.php")
    Call<MealResponse> searchMeals(@Query("s") String query);

    @GET("lookup.php")
    Call<MealResponse> getMealDetail(@Query("i") String id);

    @GET("random.php")
    Call<MealResponse> getRandomMeal();
}