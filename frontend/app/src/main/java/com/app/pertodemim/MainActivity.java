package com.app.pertodemim;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.app.pertodemim.model.LoginRequest;
import com.app.pertodemim.model.UserResponse;
import com.app.pertodemim.network.RetrofitClient;
import com.app.pertodemim.network.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText textUsuario, textSenha;
    private TextInputLayout layoutUsuario, layoutSenha;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        layoutUsuario = findViewById(R.id.layoutUsuario);
        layoutSenha = findViewById(R.id.layoutSenha);
        textUsuario = findViewById(R.id.textUsuario);
        textSenha = findViewById(R.id.textSenha);
        Button btEntrar = findViewById(R.id.btEntrar);
        TextView textRedefinirSenha = findViewById(R.id.textView2);
        Button btCriarCliente = findViewById(R.id.btCriarCliente);
        Button btCriarFornecedor = findViewById(R.id.btCriarFornecedor);

        setupClearErrorOnTouch();

        btCriarCliente.setOnClickListener(v -> startActivity(new Intent(this, RegisterClientActivity.class)));
        btCriarFornecedor.setOnClickListener(v -> startActivity(new Intent(this, RegisterProviderActivity.class)));
        textRedefinirSenha.setOnClickListener(v -> startActivity(new Intent(this, ResetPasswordActivity.class)));

        btEntrar.setOnClickListener(v -> {
            if (validateFields()) {
                String email = textUsuario.getText().toString().trim();
                String senha = textSenha.getText().toString();

                showLoading(true, btEntrar);

                LoginRequest loginRequest = new LoginRequest(email, senha);
                RetrofitClient.getApiService().login(loginRequest).enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        showLoading(false, btEntrar);
                        if (response.isSuccessful() && response.body() != null) {
                            UserResponse userResponse = response.body();
                            sessionManager.saveAuthToken(userResponse.getToken());
                            if (userResponse.getUsuario() != null) {
                                sessionManager.saveUserId(userResponse.getUsuario().getId());
                                sessionManager.saveUserType(userResponse.getUsuario().getTipo());
                            }
                            Toast.makeText(MainActivity.this, userResponse.getMensagem(), Toast.LENGTH_SHORT).show();
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
                                    if (errorBody != null && errorBody.getErro() != null) errorMsg = errorBody.getErro();
                                } catch (Exception e) { errorMsg = "Campos ausentes"; }
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

        View mainView = findViewById(R.id.main);
        int pL = mainView.getPaddingLeft(), pT = mainView.getPaddingTop(), pR = mainView.getPaddingRight(), pB = mainView.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left + pL, systemBars.top + pT, systemBars.right + pR, systemBars.bottom + pB);
            return insets;
        });
    }

    private void showLoading(boolean loading, Button btn) {
        ProgressBar pb = findViewById(R.id.loadingLogin);
        if (loading) {
            btn.setText("");
            btn.setEnabled(false);
            pb.setVisibility(View.VISIBLE);
        } else {
            btn.setText(R.string.entrar);
            btn.setEnabled(true);
            pb.setVisibility(View.GONE);
        }
    }

    private void setupClearErrorOnTouch() {
        textUsuario.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) layoutUsuario.setError(null); });
        textUsuario.setOnClickListener(v -> layoutUsuario.setError(null));
        textSenha.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) layoutSenha.setError(null); });
        textSenha.setOnClickListener(v -> layoutSenha.setError(null));
    }

    private boolean validateFields() {
        layoutUsuario.setError(null);
        layoutSenha.setError(null);
        String inputUsuario = textUsuario.getText() != null ? textUsuario.getText().toString().trim() : "";
        String inputSenha = textSenha.getText() != null ? textSenha.getText().toString() : "";

        if (TextUtils.isEmpty(inputUsuario)) {
            layoutUsuario.setError(getString(R.string.error_required));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputUsuario).matches()) {
            layoutUsuario.setError(getString(R.string.error_invalid_email));
            return false;
        }

        if (TextUtils.isEmpty(inputSenha)) {
            layoutSenha.setError(getString(R.string.error_required));
            return false;
        }
        return true;
    }
}
