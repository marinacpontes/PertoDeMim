package com.app.pertodemim;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.materialswitch.MaterialSwitch;

// Tela para gerenciar preferências de notificações com persistência
public class NotificationsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_PUSH = "push_notifications";
    private static final String KEY_CHAT = "chat_alerts";
    private static final String KEY_EMAIL = "email_promo";
    private static final String KEY_SMS = "sms_alerts";

    private MaterialSwitch switchPush, switchChat, switchEmail, switchSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        initViews();
        loadSettings(); // Carrega as preferências salvas
        setupInteractions();
    }

    private void initViews() {
        switchPush = findViewById(R.id.switchPushNotifications);
        switchChat = findViewById(R.id.switchChatAlerts);
        switchEmail = findViewById(R.id.switchEmailPromo);
        switchSms = findViewById(R.id.switchSmsAlerts);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    // Carrega os estados dos switches salvos no SharedPreferences
    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        switchPush.setChecked(prefs.getBoolean(KEY_PUSH, true));
        switchChat.setChecked(prefs.getBoolean(KEY_CHAT, true));
        switchEmail.setChecked(prefs.getBoolean(KEY_EMAIL, false));
        switchSms.setChecked(prefs.getBoolean(KEY_SMS, false));
    }

    // Salva uma configuração booleana
    private void saveBoolSetting(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void setupInteractions() {
        switchPush.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveBoolSetting(KEY_PUSH, isChecked);
            showFeedback("Notificações Push", isChecked);
        });

        switchChat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveBoolSetting(KEY_CHAT, isChecked);
            showFeedback("Alertas de Chat", isChecked);
        });

        switchEmail.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveBoolSetting(KEY_EMAIL, isChecked);
            showFeedback("E-mails promocionais", isChecked);
        });

        switchSms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveBoolSetting(KEY_SMS, isChecked);
            showFeedback("Alertas por SMS", isChecked);
        });
    }

    private void showFeedback(String title, boolean isEnabled) {
        String status = isEnabled ? "ativado" : "desativado";
        Toast.makeText(this, title + " " + status, Toast.LENGTH_SHORT).show();
    }
}