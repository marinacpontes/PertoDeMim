package com.app.pertodemim;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

// Tela de verificação de código enviado por e-mail para recuperação de senha
public class VerifyCodeActivity extends AppCompatActivity {

    // Componentes da interface: texto de instrução e campo de entrada do código
    private TextView subtitleVerify, tvReenviar;
    private TextInputLayout layoutCodigo;
    private TextInputEditText textCodigo;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Configura a tela para ocupar toda a área (modo imersivo)
        EdgeToEdge.enable(this);
        // Define o layout da tela de verificação
        setContentView(R.layout.activity_verify_code);

        // Inicializa os componentes buscando-os pelos IDs no XML
        TextView tvVoltar = findViewById(R.id.tvVoltarVerify);
        subtitleVerify = findViewById(R.id.subtitleVerify);
        layoutCodigo = findViewById(R.id.layoutCodigo);
        textCodigo = findViewById(R.id.textCodigo);
        MaterialButton btVerificar = findViewById(R.id.btVerificar);
        tvReenviar = findViewById(R.id.tvReenviar);

        // Faz a mensagem de erro desaparecer quando o usuário começar a interagir com o campo
        textCodigo.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) layoutCodigo.setError(null); });
        textCodigo.setOnClickListener(v -> layoutCodigo.setError(null));

        // Botão voltar: retorna para a tela anterior de digitar e-mail
        tvVoltar.setOnClickListener(v -> finish());

        // Ação do botão "Verificar": valida o campo e abre a tela de cadastrar nova senha
        btVerificar.setOnClickListener(v -> {
            if (validateFields()) {
                startActivity(new Intent(this, NewPasswordActivity.class));
            }
        });

        // Ação do texto "Reenviar": simula o reenvio alterando o texto da tela
        tvReenviar.setOnClickListener(v -> {
            subtitleVerify.setText(getString(R.string.email_reenviado));
            subtitleVerify.setTextColor(ContextCompat.getColor(this, R.color.verde_petroleo_profundo));
            startResendTimer();
        });
    }

    private void startResendTimer() {
        tvReenviar.setEnabled(false);
        tvReenviar.setTextColor(ContextCompat.getColor(this, R.color.text_gray));

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                String text = String.format(Locale.getDefault(), "Reenviar e-mail em %02ds", seconds);
                tvReenviar.setText(text);
            }

            @Override
            public void onFinish() {
                tvReenviar.setEnabled(true);
                tvReenviar.setText(R.string.reenviar_email);
                tvReenviar.setTextColor(ContextCompat.getColor(VerifyCodeActivity.this, R.color.verde_petroleo_profundo));
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    // Função que verifica se o código obrigatório foi digitado
    private boolean validateFields() {
        layoutCodigo.setError(null); // Reseta erros visuais
        String codigo = textCodigo.getText() != null ? textCodigo.getText().toString().trim() : "";
        
        // Exibe erro se o campo estiver vazio
        if (TextUtils.isEmpty(codigo)) {
            layoutCodigo.setError(getString(R.string.error_required));
            return false;
        }
        return true;
    }
}