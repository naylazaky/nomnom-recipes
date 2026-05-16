package com.nomnom.recipes;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import com.nomnom.recipes.data.prefs.PrefsManager;

public class NomNomApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        boolean isDark = PrefsManager.getInstance(this).isDarkMode();
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}