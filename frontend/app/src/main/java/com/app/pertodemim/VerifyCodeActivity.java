package com.app.pertodemim;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Locale;

public class VerifyCodeActivity extends AppCompatActivity {

    private TextView subtitleVerify, tvReenviar;
    private TextInputLayout layoutCodigo;
    private TextInputEditText textCodigo;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_code);

        subtitleVerify = findViewById(R.id.subtitleVerify);
        layoutCodigo = findViewById(R.id.layoutCodigo);
        textCodigo = findViewById(R.id.textCodigo);
        tvReenviar = findViewById(R.id.tvReenviar);

        textCodigo.setOnFocusChangeListener((v, h) -> { if (h) layoutCodigo.setError(null); });
        textCodigo.setOnClickListener(v -> layoutCodigo.setError(null));
        findViewById(R.id.tvVoltarVerify).setOnClickListener(v -> finish());
        findViewById(R.id.btVerificar).setOnClickListener(v -> { if (validateFields()) startActivity(new Intent(this, NewPasswordActivity.class)); });
        tvReenviar.setOnClickListener(v -> {
            subtitleVerify.setText(getString(R.string.email_reenviado));
            subtitleVerify.setTextColor(ContextCompat.getColor(this, R.color.verde_petroleo_profundo));
            startResendTimer();
        });
    }

    private void startResendTimer() {
        tvReenviar.setEnabled(false); tvReenviar.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override public void onTick(long m) { tvReenviar.setText(String.format(Locale.getDefault(), "Reenviar e-mail em %02ds", m / 1000)); }
            @Override public void onFinish() { tvReenviar.setEnabled(true); tvReenviar.setText(R.string.reenviar_email); tvReenviar.setTextColor(ContextCompat.getColor(VerifyCodeActivity.this, R.color.verde_petroleo_profundo)); }
        }.start();
    }

    @Override protected void onDestroy() { super.onDestroy(); if (countDownTimer != null) countDownTimer.cancel(); }

    private boolean validateFields() {
        layoutCodigo.setError(null);
        String code = textCodigo.getText() != null ? textCodigo.getText().toString().trim() : "";
        if (TextUtils.isEmpty(code)) { layoutCodigo.setError(getString(R.string.error_required)); return false; }
        return true;
    }
}
