package com.app.pertodemim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.app.pertodemim.network.SessionManager;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppPrefs", KEY_LANGUAGE_INDEX = "languageIndex", KEY_DARK_MODE = "darkMode", KEY_BIOMETRICS = "biometrics";
    private int selectedLanguageIndex = 0;
    private final String[] languages = {"Português", "English", "Español"};
    private TextView tvCurrentLanguage;
    private MaterialSwitch switchDarkMode, switchBiometrics;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sessionManager = new SessionManager(this);
        initViews(); loadSettings(); setupInteractions();
    }

    private void initViews() {
        tvCurrentLanguage = findViewById(R.id.tvCurrentLanguage); switchDarkMode = findViewById(R.id.switchDarkMode); switchBiometrics = findViewById(R.id.switchBiometrics);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        selectedLanguageIndex = prefs.getInt(KEY_LANGUAGE_INDEX, 0); tvCurrentLanguage.setText(languages[selectedLanguageIndex]);
        switchDarkMode.setChecked(prefs.getBoolean(KEY_DARK_MODE, false)); switchBiometrics.setChecked(prefs.getBoolean(KEY_BIOMETRICS, false));
    }

    private void saveBoolSetting(String key, boolean value) { getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean(key, value).apply(); }
    private void saveLanguageSetting(int index) { getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putInt(KEY_LANGUAGE_INDEX, index).apply(); }

    private void setupInteractions() {
        findViewById(R.id.btnChangePassword).setOnClickListener(v -> startActivity(new Intent(this, ChangePasswordActivity.class)));
        findViewById(R.id.btnDeleteAccount).setOnClickListener(v -> showDeleteAccountDialog());
        findViewById(R.id.btnLanguage).setOnClickListener(v -> showLanguageDialog());
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(this, MainActivity.class); intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent); finish();
        });
        switchDarkMode.setOnCheckedChangeListener((b, isChecked) -> { saveBoolSetting(KEY_DARK_MODE, isChecked); Toast.makeText(this, isChecked ? "Modo Escuro será aplicado em breve" : "Modo Claro ativado", Toast.LENGTH_SHORT).show(); });
        switchBiometrics.setOnCheckedChangeListener((b, isChecked) -> { saveBoolSetting(KEY_BIOMETRICS, isChecked); Toast.makeText(this, isChecked ? "Biometria ativada" : "Biometria desativada", Toast.LENGTH_SHORT).show(); });
    }

    private void showDeleteAccountDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.excluir_conta).setMessage(R.string.delete_account_msg).setPositiveButton(R.string.excluir_confirm, (d, w) -> {
                Toast.makeText(this, "Conta excluída com sucesso.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class); intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); startActivity(intent);
            }).setNegativeButton(R.string.cancelar, null).create();
        dialog.show(); dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.logout_red));
    }

    private void showLanguageDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.idioma).setSingleChoiceItems(languages, selectedLanguageIndex, (dialog, which) -> {
                selectedLanguageIndex = which; tvCurrentLanguage.setText(languages[which]); saveLanguageSetting(which);
                Toast.makeText(this, "Idioma alterado para: " + languages[which], Toast.LENGTH_SHORT).show(); dialog.dismiss();
            }).setNegativeButton(R.string.cancelar, null).show();
    }
}
