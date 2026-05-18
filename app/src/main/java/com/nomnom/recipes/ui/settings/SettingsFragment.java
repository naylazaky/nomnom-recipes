package com.nomnom.recipes.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.nomnom.recipes.R;
import com.nomnom.recipes.data.prefs.PrefsManager;
import com.nomnom.recipes.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private PrefsManager prefsManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefsManager = PrefsManager.getInstance(requireContext());

        setupDarkModeSwitch();
        setupDeleteAllButton();
    }

    private void setupDarkModeSwitch() {
        binding.switchDarkMode.setChecked(prefsManager.isDarkMode());

        binding.switchDarkMode.setOnCheckedChangeListener((btn, isChecked) -> {
            prefsManager.saveThemeMode(isChecked);

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            requireActivity().recreate();
        });
    }

    private void setupDeleteAllButton() {
        binding.btnDeleteAllFavorites.setOnClickListener(v ->
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.dialog_delete_all_title)
                        .setMessage(R.string.dialog_delete_all_message)
                        .setPositiveButton(R.string.dialog_yes, (d, w) -> {
                            prefsManager.clearAllFavorites();
                        })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .show()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}