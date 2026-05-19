package com.app.pertodemim;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

// Tela para cadastrar uma nova senha após a verificação do código de recuperação
public class NewPasswordActivity extends AppCompatActivity {

    // Campos para digitação da nova senha e sua confirmação (Layouts e Inputs)
    private TextInputLayout layoutNovaSenha, layoutConfirmaNovaSenha;
    private TextInputEditText textNovaSenha, textConfirmaNovaSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ativa o modo de tela cheia
        EdgeToEdge.enable(this);
        // Define o layout da tela de criação de nova senha
        setContentView(R.layout.activity_new_password);

        // Inicializa os componentes da tela buscando-os pelo ID
        TextView tvVoltar = findViewById(R.id.tvVoltarNewPass);
        layoutNovaSenha = findViewById(R.id.layoutNovaSenha);
        layoutConfirmaNovaSenha = findViewById(R.id.layoutConfirmaNovaSenha);
        textNovaSenha = findViewById(R.id.textNovaSenha);
        textConfirmaNovaSenha = findViewById(R.id.textConfirmaNovaSenha);
        MaterialButton btAlterarSenha = findViewById(R.id.btAlterarSenha);

        // Configura para que a mensagem de erro suma assim que o usuário clicar no campo
        setupClearErrorOnTouch();
        
        // Botão voltar: retorna à tela de verificação de código
        tvVoltar.setOnClickListener(v -> finish());

        // Ação do botão "Alterar Senha": valida os campos e finaliza o processo
        btAlterarSenha.setOnClickListener(v -> {
            if (validateFields()) {
                // Exibe uma mensagem de sucesso flutuante (Toast)
                Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_LONG).show();
                
                // Retorna para a tela de Login limpando as telas anteriores da memória
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    // Função que limpa o texto vermelho de erro quando o usuário interage com o campo
    private void setupClearErrorOnTouch() {
        textNovaSenha.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) layoutNovaSenha.setError(null); });
        textNovaSenha.setOnClickListener(v -> layoutNovaSenha.setError(null));
        textConfirmaNovaSenha.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) layoutConfirmaNovaSenha.setError(null); });
        textConfirmaNovaSenha.setOnClickListener(v -> layoutConfirmaNovaSenha.setError(null));
    }

    // Validação: verifica se as senhas foram preenchidas e se ambas são iguais
    private boolean validateFields() {
        boolean valid = true;
        layoutNovaSenha.setError(null); // Reseta erros anteriores
        layoutConfirmaNovaSenha.setError(null);

        // Pega os textos digitados
        String senha = textNovaSenha.getText() != null ? textNovaSenha.getText().toString() : "";
        String confirma = textConfirmaNovaSenha.getText() != null ? textConfirmaNovaSenha.getText().toString() : "";

        // Verifica se os campos estão em branco
        if (TextUtils.isEmpty(senha)) {
            layoutNovaSenha.setError(getString(R.string.error_required));
            valid = false;
        }
        if (TextUtils.isEmpty(confirma)) {
            layoutConfirmaNovaSenha.setError(getString(R.string.error_required));
            valid = false;
        } else if (!Objects.equals(senha, confirma)) {
            // Se as senhas não coincidirem, exibe erro nos dois campos
            layoutNovaSenha.setError(getString(R.string.error_password_mismatch));
            layoutConfirmaNovaSenha.setError(getString(R.string.error_password_mismatch));
            valid = false;
        }

        return valid;
    }
}