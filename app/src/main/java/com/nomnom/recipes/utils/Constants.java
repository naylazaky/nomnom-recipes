package com.nomnom.recipes.utils;


public class Constants {

    public static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";

    public static final String EXTRA_MEAL_ID    = "extra_meal_id";
    public static final String EXTRA_MEAL_NAME  = "extra_meal_name";
    public static final String EXTRA_MEAL_THUMB = "extra_meal_thumb";

    public static final String PREF_NAME          = "nomnom_prefs";
    public static final String KEY_THEME_MODE     = "theme_mode";
    public static final String KEY_LAST_CATEGORY  = "last_category";
    public static final String KEY_FAVORITE_MEALS = "favorite_meals";
    public static final String KEY_CACHE_PREFIX   = "cache_meals_";

    public static final String DEFAULT_CATEGORY  = "Chicken";
    public static final long   SEARCH_DEBOUNCE_MS = 500;
    public static final int    SEARCH_MIN_CHARS   = 3;
}