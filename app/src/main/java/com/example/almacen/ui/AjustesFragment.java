package com.example.almacen.ui;

import android.os.Bundle;
import com.example.almacen.R;
import androidx.preference.PreferenceFragmentCompat;

public class AjustesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}