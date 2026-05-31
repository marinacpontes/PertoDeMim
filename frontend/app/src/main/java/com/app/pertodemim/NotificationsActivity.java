package com.app.pertodemim;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.materialswitch.MaterialSwitch;

public class NotificationsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_PUSH = "push_notifications", KEY_CHAT = "chat_alerts", KEY_EMAIL = "email_promo", KEY_SMS = "sms_alerts";
    private MaterialSwitch switchPush, switchChat, switchEmail, switchSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        initViews(); loadSettings(); setupInteractions();
    }

    private void initViews() {
        switchPush = findViewById(R.id.switchPushNotifications); switchChat = findViewById(R.id.switchChatAlerts);
        switchEmail = findViewById(R.id.switchEmailPromo); switchSms = findViewById(R.id.switchSmsAlerts);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        switchPush.setChecked(prefs.getBoolean(KEY_PUSH, true)); switchChat.setChecked(prefs.getBoolean(KEY_CHAT, true));
        switchEmail.setChecked(prefs.getBoolean(KEY_EMAIL, false)); switchSms.setChecked(prefs.getBoolean(KEY_SMS, false));
    }

    private void saveBoolSetting(String key, boolean value) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean(key, value).apply();
    }

    private void setupInteractions() {
        switchPush.setOnCheckedChangeListener((b, isChecked) -> { saveBoolSetting(KEY_PUSH, isChecked); showFeedback("Push", isChecked); });
        switchChat.setOnCheckedChangeListener((b, isChecked) -> { saveBoolSetting(KEY_CHAT, isChecked); showFeedback("Chat", isChecked); });
        switchEmail.setOnCheckedChangeListener((b, isChecked) -> { saveBoolSetting(KEY_EMAIL, isChecked); showFeedback("E-mail", isChecked); });
        switchSms.setOnCheckedChangeListener((b, isChecked) -> { saveBoolSetting(KEY_SMS, isChecked); showFeedback("SMS", isChecked); });
    }

    private void showFeedback(String title, boolean isEnabled) {
        Toast.makeText(this, title + (isEnabled ? " ativado" : " desativado"), Toast.LENGTH_SHORT).show();
    }
}
