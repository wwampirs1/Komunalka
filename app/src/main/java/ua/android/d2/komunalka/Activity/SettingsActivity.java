package ua.android.d2.komunalka.Activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import ua.android.d2.komunalka.R;

/**
 * Created by Julia on 12.06.2015.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
