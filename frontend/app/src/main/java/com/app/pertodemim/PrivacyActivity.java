package com.app.pertodemim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.materialswitch.MaterialSwitch;

public class PrivacyActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_LOCATION = "privacy_location", KEY_ANALYTICS = "privacy_analytics", KEY_PUBLIC_PROFILE = "privacy_public_profile", KEY_SHOW_HISTORY = "privacy_show_history";
    private MaterialSwitch switchLocation, switchAnalytics, switchPublicProfile, switchShowHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        initViews(); loadSettings(); setupInteractions();
    }

    private void initViews() {
        switchLocation = findViewById(R.id.switchLocation); switchAnalytics = findViewById(R.id.switchAnalytics);
        switchPublicProfile = findViewById(R.id.switchPublicProfile); switchShowHistory = findViewById(R.id.switchShowHistory);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnViewPrivacyPolicy).setOnClickListener(v -> startActivity(new Intent(this, PrivacyPolicyActivity.class)));
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        switchLocation.setChecked(prefs.getBoolean(KEY_LOCATION, true)); switchAnalytics.setChecked(prefs.getBoolean(KEY_ANALYTICS, true));
        switchPublicProfile.setChecked(prefs.getBoolean(KEY_PUBLIC_PROFILE, false)); switchShowHistory.setChecked(prefs.getBoolean(KEY_SHOW_HISTORY, true));
    }

    private void saveBoolSetting(String key, boolean value) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean(key, value).apply();
    }

    private void setupInteractions() {
        switchLocation.setOnCheckedChangeListener((b, isChecked) -> { saveBoolSetting(KEY_LOCATION, isChecked); showFeedback("Localização", isChecked); });
        switchAnalytics.setOnCheckedChangeListener((b, isChecked) -> { saveBoolSetting(KEY_ANALYTICS, isChecked); showFeedback("Dados anônimos", isChecked); });
        switchPublicProfile.setOnCheckedChangeListener((b, isChecked) -> { saveBoolSetting(KEY_PUBLIC_PROFILE, isChecked); showFeedback("Perfil público", isChecked); });
        switchShowHistory.setOnCheckedChangeListener((b, isChecked) -> { saveBoolSetting(KEY_SHOW_HISTORY, isChecked); showFeedback("Histórico", isChecked); });
    }

    private void showFeedback(String title, boolean isEnabled) {
        Toast.makeText(this, title + (isEnabled ? " ativado" : " desativado"), Toast.LENGTH_SHORT).show();
    }
}
