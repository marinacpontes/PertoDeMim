package com.app.pertodemim;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.widget.Toast;

import com.app.pertodemim.model.LoginRequest;
import com.app.pertodemim.model.UserResponse;
import com.app.pertodemim.network.RetrofitClient;
import com.app.pertodemim.network.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Tela de Login (entrada principal do aplicativo)
public class MainActivity extends AppCompatActivity {

    // Componentes para digitar e-mail e senha (Campos de texto e layouts de erro)
    private TextInputEditText textUsuario, textSenha;
    private TextInputLayout layoutUsuario, layoutSenha;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Permite que o app ocupe a tela inteira, inclusive sob as barras de status
        EdgeToEdge.enable(this);
        // Define o layout da tela de login
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        // Inicializa os campos e botões da tela buscando pelos IDs do XML
        layoutUsuario = findViewById(R.id.layoutUsuario);
        layoutSenha = findViewById(R.id.layoutSenha);
        textUsuario = findViewById(R.id.textUsuario);
        textSenha = findViewById(R.id.textSenha);
        Button btEntrar = findViewById(R.id.btEntrar);
        TextView textRedefinirSenha = findViewById(R.id.textView2);
        Button btCriarCliente = findViewById(R.id.btCriarCliente);
        Button btCriarFornecedor = findViewById(R.id.btCriarFornecedor);

        // Configura para que a mensagem de erro suma assim que o usuário clicar no campo
        setupClearErrorOnTouch();

        // Configura os botões para abrir as telas de Cadastro e Recuperação de Senha
        btCriarCliente.setOnClickListener(v -> startActivity(new Intent(this, RegisterClientActivity.class)));
        btCriarFornecedor.setOnClickListener(v -> startActivity(new Intent(this, RegisterProviderActivity.class)));
        textRedefinirSenha.setOnClickListener(v -> startActivity(new Intent(this, ResetPasswordActivity.class)));

        // Ação do botão "Entrar"
        btEntrar.setOnClickListener(v -> {
            // Se o e-mail e a senha estiverem preenchidos corretamente
            if (validateFields()) {
                String email = textUsuario.getText().toString().trim();
                String senha = textSenha.getText().toString();

                // Simula um carregamento antes de entrar
                showLoading(true, btEntrar);

                LoginRequest loginRequest = new LoginRequest(email, senha);
                RetrofitClient.getApiService().login(loginRequest).enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        showLoading(false, btEntrar);
                        if (response.isSuccessful() && response.body() != null) {
                            UserResponse userResponse = response.body();
                            
                            // Salva o token e o tipo de usuário
                            sessionManager.saveAuthToken(userResponse.getToken());
                            if (userResponse.getUsuario() != null) {
                                sessionManager.saveUserType(userResponse.getUsuario().getTipo());
                            }

                            Toast.makeText(MainActivity.this, userResponse.getMensagem(), Toast.LENGTH_SHORT).show();

                            // Vai para a Home (Mapa)
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            String errorMsg = "Erro ao realizar login";
                            if (response.code() == 401) {
                                errorMsg = "E-mail ou senha incorretos";
                            } else if (response.code() == 400) {
                                try {
                                    UserResponse errorBody = new com.google.gson.Gson().fromJson(response.errorBody().string(), UserResponse.class);
                                    if (errorBody != null && errorBody.getErro() != null) {
                                        errorMsg = errorBody.getErro();
                                    }
                                } catch (Exception e) {
                                    errorMsg = "Campos ausentes";
                                }
                            }
                            Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        showLoading(false, btEntrar);
                        Toast.makeText(MainActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // Ajustas as margens da tela para que o conteúdo não fique "escondido" atrás da barra de bateria/relógio
        View mainView = findViewById(R.id.main);
        int pL = mainView.getPaddingLeft();
        int pT = mainView.getPaddingTop();
        int pR = mainView.getPaddingRight();
        int pB = mainView.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left + pL, systemBars.top + pT, systemBars.right + pR, systemBars.bottom + pB);
            return insets;
        });
    }

    // Função que alterna entre o botão de entrar e o ícone de carregamento
    private void showLoading(boolean loading, Button btn) {
        ProgressBar pb = findViewById(R.id.loadingLogin);
        if (loading) {
            btn.setText(""); // Esconde o texto
            btn.setEnabled(false); // Desativa o clique
            pb.setVisibility(View.VISIBLE); // Mostra o círculo girando
        } else {
            btn.setText(R.string.entrar);
            btn.setEnabled(true);
            pb.setVisibility(View.GONE);
        }
    }

    // Função que remove o aviso de erro (texto vermelho) quando o usuário interage com o campo
    private void setupClearErrorOnTouch() {
        textUsuario.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) layoutUsuario.setError(null); });
        textUsuario.setOnClickListener(v -> layoutUsuario.setError(null));
        textSenha.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) layoutSenha.setError(null); });
        textSenha.setOnClickListener(v -> layoutSenha.setError(null));
    }

    // Valida se os campos foram preenchidos corretamente (e-mail válido e senha não vazia)
    private boolean validateFields() {
        boolean valid = true;
        layoutUsuario.setError(null); // Reseta erros anteriores
        layoutSenha.setError(null);

        // Pega o que foi digitado
        String inputUsuario = textUsuario.getText() != null ? textUsuario.getText().toString().trim() : "";
        String inputSenha = textSenha.getText() != null ? textSenha.getText().toString() : "";

        // Verifica se o e-mail está vazio ou se o formato é inválido (@dominio)
        if (TextUtils.isEmpty(inputUsuario)) {
            layoutUsuario.setError(getString(R.string.error_required));
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputUsuario).matches()) {
            layoutUsuario.setError(getString(R.string.error_invalid_email));
            valid = false;
        }

        // Verifica se a senha foi preenchida
        if (TextUtils.isEmpty(inputSenha)) {
            layoutSenha.setError(getString(R.string.error_required));
            valid = false;
        }

        return valid;
    }
}