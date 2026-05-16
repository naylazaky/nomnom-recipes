package com.nomnom.recipes.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nomnom.recipes.data.api.model.Meal;
import com.nomnom.recipes.utils.Constants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PrefsManager {

    private static PrefsManager instance;
    private final SharedPreferences prefs;
    private final Gson gson;

    private PrefsManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static PrefsManager getInstance(Context context) {
        if (instance == null) {
            instance = new PrefsManager(context);
        }
        return instance;
    }

    public void saveThemeMode(boolean isDark) {
        prefs.edit().putBoolean(Constants.KEY_THEME_MODE, isDark).apply();
    }

    public boolean isDarkMode() {
        return prefs.getBoolean(Constants.KEY_THEME_MODE, false);
    }

    public void saveLastCategory(String category) {
        prefs.edit().putString(Constants.KEY_LAST_CATEGORY, category).apply();
    }

    public String getLastCategory() {
        return prefs.getString(Constants.KEY_LAST_CATEGORY, Constants.DEFAULT_CATEGORY);
    }

    public void addFavorite(Meal meal) {
        List<Meal> favorites = getAllFavorites();
        for (Meal m : favorites) {
            if (m.getIdMeal().equals(meal.getIdMeal())) return;
        }
        favorites.add(meal);
        saveFavoriteList(favorites);
    }

    public void removeFavorite(String mealId) {
        List<Meal> favorites = getAllFavorites();
        favorites.removeIf(m -> m.getIdMeal().equals(mealId));
        saveFavoriteList(favorites);
    }

    public boolean isFavorite(String mealId) {
        List<Meal> favorites = getAllFavorites();
        for (Meal m : favorites) {
            if (m.getIdMeal().equals(mealId)) return true;
        }
        return false;
    }

    public List<Meal> getAllFavorites() {
        String json = prefs.getString(Constants.KEY_FAVORITE_MEALS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Meal>>() {}.getType();
        List<Meal> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public void clearAllFavorites() {
        prefs.edit().remove(Constants.KEY_FAVORITE_MEALS).apply();
    }

    private void saveFavoriteList(List<Meal> list) {
        prefs.edit().putString(Constants.KEY_FAVORITE_MEALS, gson.toJson(list)).apply();
    }

    public void saveCachedMeals(String category, List<Meal> meals) {
        String key = Constants.KEY_CACHE_PREFIX + category;
        prefs.edit().putString(key, gson.toJson(meals)).apply();
    }

    public List<Meal> getCachedMeals(String category) {
        String key = Constants.KEY_CACHE_PREFIX + category;
        String json = prefs.getString(key, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Meal>>() {}.getType();
        List<Meal> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }
}