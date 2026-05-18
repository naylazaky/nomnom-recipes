package com.nomnom.recipes.ui.favorite;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nomnom.recipes.data.api.model.Meal;
import com.nomnom.recipes.data.prefs.PrefsManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriteViewModel extends AndroidViewModel {

    private final PrefsManager prefsManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final MutableLiveData<List<Meal>> favorites = new MutableLiveData<>();

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        prefsManager = PrefsManager.getInstance(application);
    }

    public LiveData<List<Meal>> getFavorites() { return favorites; }

    public void loadFavorites() {
        executor.execute(() -> {
            List<Meal> list = prefsManager.getAllFavorites();
            mainHandler.post(() -> favorites.setValue(list));
        });
    }

    public void removeFavorite(String mealId) {
        executor.execute(() -> {
            prefsManager.removeFavorite(mealId);
            List<Meal> list = prefsManager.getAllFavorites();
            mainHandler.post(() -> favorites.setValue(list));
        });
    }

    public void clearAllFavorites() {
        executor.execute(() -> {
            prefsManager.clearAllFavorites();
            mainHandler.post(() -> favorites.setValue(null));
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}