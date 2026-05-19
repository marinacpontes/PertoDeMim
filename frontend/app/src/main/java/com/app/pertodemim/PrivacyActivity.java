package com.app.pertodemim;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.materialswitch.MaterialSwitch;

// Tela para gerenciar configurações de privacidade com persistência
public class PrivacyActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_LOCATION = "privacy_location";
    private static final String KEY_ANALYTICS = "privacy_analytics";
    private static final String KEY_PUBLIC_PROFILE = "privacy_public_profile";
    private static final String KEY_SHOW_HISTORY = "privacy_show_history";

    private MaterialSwitch switchLocation, switchAnalytics, switchPublicProfile, switchShowHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        initViews();
        loadSettings(); // Carrega as preferências salvas
        setupInteractions();
    }

    private void initViews() {
        switchLocation = findViewById(R.id.switchLocation);
        switchAnalytics = findViewById(R.id.switchAnalytics);
        switchPublicProfile = findViewById(R.id.switchPublicProfile);
        switchShowHistory = findViewById(R.id.switchShowHistory);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Botão para ler a política completa (simulação)
        findViewById(R.id.btnViewPrivacyPolicy).setOnClickListener(v -> 
            Toast.makeText(this, "Abrindo Política de Privacidade...", Toast.LENGTH_SHORT).show());
    }

    // Carrega os estados dos switches salvos no SharedPreferences
    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        switchLocation.setChecked(prefs.getBoolean(KEY_LOCATION, true));
        switchAnalytics.setChecked(prefs.getBoolean(KEY_ANALYTICS, true));
        switchPublicProfile.setChecked(prefs.getBoolean(KEY_PUBLIC_PROFILE, false));
        switchShowHistory.setChecked(prefs.getBoolean(KEY_SHOW_HISTORY, true));
    }

    // Salva uma configuração booleana
    private void saveBoolSetting(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void setupInteractions() {
        switchLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveBoolSetting(KEY_LOCATION, isChecked);
            showFeedback("Localização em tempo real", isChecked);
        });

        switchAnalytics.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveBoolSetting(KEY_ANALYTICS, isChecked);
            showFeedback("Dados anônimos", isChecked);
        });

        switchPublicProfile.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveBoolSetting(KEY_PUBLIC_PROFILE, isChecked);
            showFeedback("Perfil público", isChecked);
        });

        switchShowHistory.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveBoolSetting(KEY_SHOW_HISTORY, isChecked);
            showFeedback("Histórico no perfil", isChecked);
        });
    }

    private void showFeedback(String title, boolean isEnabled) {
        String status = isEnabled ? "ativado" : "desativado";
        Toast.makeText(this, title + " " + status, Toast.LENGTH_SHORT).show();
    }
}