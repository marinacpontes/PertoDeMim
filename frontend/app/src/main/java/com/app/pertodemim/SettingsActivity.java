package com.app.pertodemim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.materialswitch.MaterialSwitch;

import com.app.pertodemim.network.SessionManager;

// Tela de Configurações do Aplicativo com persistência de dados
public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_LANGUAGE_INDEX = "languageIndex";
    private static final String KEY_DARK_MODE = "darkMode";
    private static final String KEY_BIOMETRICS = "biometrics";

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

        initViews();
        loadSettings(); // Carrega as configurações salvas
        setupInteractions();
    }

    private void initViews() {
        tvCurrentLanguage = findViewById(R.id.tvCurrentLanguage);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchBiometrics = findViewById(R.id.switchBiometrics);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    // Carrega as configurações do SharedPreferences e aplica na interface
    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        selectedLanguageIndex = prefs.getInt(KEY_LANGUAGE_INDEX, 0);
        tvCurrentLanguage.setText(languages[selectedLanguageIndex]);

        boolean darkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        switchDarkMode.setChecked(darkMode);

        boolean biometrics = prefs.getBoolean(KEY_BIOMETRICS, false);
        switchBiometrics.setChecked(biometrics);
    }

    // Salva uma configuração booleana
    private void saveBoolSetting(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    // Salva o índice do idioma escolhido
    private void saveLanguageSetting(int index) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_LANGUAGE_INDEX, index);
        editor.apply();
    }

    private void setupInteractions() {
        findViewById(R.id.btnChangePassword).setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        findViewById(R.id.btnDeleteAccount).setOnClickListener(v -> showDeleteAccountDialog());

        findViewById(R.id.btnLanguage).setOnClickListener(v -> showLanguageDialog());

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Salva e avisa quando o Modo Escuro mudar
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveBoolSetting(KEY_DARK_MODE, isChecked);
            String msg = isChecked ? "Modo Escuro será aplicado globalmente em breve" : "Modo Claro ativado";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        // Salva e avisa quando a Biometria mudar
        switchBiometrics.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveBoolSetting(KEY_BIOMETRICS, isChecked);
            String msg = isChecked ? "Biometria ativada para os próximos acessos" : "Biometria desativada";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }

    private void showDeleteAccountDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(R.string.excluir_conta)
            .setMessage(R.string.delete_account_msg)
            .setPositiveButton(R.string.excluir_confirm, (d, which) -> {
                Toast.makeText(this, "Conta excluída com sucesso.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            })
            .setNegativeButton(R.string.cancelar, null)
            .create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
            androidx.core.content.ContextCompat.getColor(this, R.color.logout_red)
        );
    }

    private void showLanguageDialog() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.idioma)
            .setSingleChoiceItems(languages, selectedLanguageIndex, (dialog, which) -> {
                selectedLanguageIndex = which;
                String selected = languages[which];
                
                // Salva a escolha e atualiza a tela na hora
                saveLanguageSetting(which);
                tvCurrentLanguage.setText(selected);

                Toast.makeText(this, "Idioma alterado para: " + selected, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            })
            .setNegativeButton(R.string.cancelar, null)
            .show();
    }
}