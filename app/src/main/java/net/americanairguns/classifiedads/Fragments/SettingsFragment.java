package net.americanairguns.classifiedads.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;

import net.americanairguns.classifiedads.R;

// TODO: Add additional settings for: Font Size,
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Boolean currentTheme, currentFontSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().findViewById(R.id.action_sort).setEnabled(false);
        getActivity().findViewById(R.id.action_sort).setVisibility(View.INVISIBLE);

        getPreferenceManager().setSharedPreferencesName("appPreferences");
        addPreferencesFromResource(R.xml.fragment_settings);

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        currentTheme = getPreferenceManager().getSharedPreferences().getBoolean("themeSwitch", false);
        currentFontSize = getPreferenceManager().getSharedPreferences().getBoolean("fontSize", false);

        ((ActivityCallback)getActivity()).CloseDrawer();
    }

    @Override
    public void onStop() {
        if (currentTheme != getPreferenceManager().getSharedPreferences().getBoolean("themeSwitch", false) || currentFontSize != getPreferenceManager().getSharedPreferences().getBoolean("fontSize", false)) {
            getActivity().finish();
            startActivity(getActivity().getIntent());
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        getPreferenceManager().getSharedPreferences().
                unregisterOnSharedPreferenceChangeListener(this);
        getActivity().findViewById(R.id.action_sort).setEnabled(true);
        getActivity().findViewById(R.id.action_sort).setVisibility(View.VISIBLE);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {}

    public interface ActivityCallback {
        void CloseDrawer();
    }
}