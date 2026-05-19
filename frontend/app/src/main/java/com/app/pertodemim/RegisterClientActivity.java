package com.app.pertodemim;

import android.app.DatePickerDialog;
import android.content.Intent;
import java.util.Calendar;
import java.util.Locale;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.app.pertodemim.model.User;
import com.app.pertodemim.model.UserResponse;
import com.app.pertodemim.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Tela para cadastro de novos Clientes integrada com a API
public class RegisterClientActivity extends AppCompatActivity {

    private TextInputLayout tilNome, tilEmail, tilTelefone, tilCPF, tilNascimento,
            tilCEP, tilLogradouro, tilNumero, tilBairro, tilCidade, tilEstado,
            tilSenha, tilConfirmarSenha;
    private TextInputEditText editNome, editEmail, editTelefone, editCPF, editNascimento,
            editCEP, editLogradouro, editNumero, editComplemento, editBairro, editCidade, editEstado,
            editSenha, editConfirmarSenha;
    private MaterialButton btnRegister;
    private ProgressBar pbRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_client);

        initViews();
        setupClearErrorOnTouch();
        setupDatePicker();

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {
            if (validateFields()) {
                registerUser();
            }
        });
    }

    private void initViews() {
        tilNome = findViewById(R.id.tilNome);
        tilEmail = findViewById(R.id.tilEmail);
        tilTelefone = findViewById(R.id.tilTelefone);
        tilCPF = findViewById(R.id.tilCPF);
        tilNascimento = findViewById(R.id.tilNascimento);
        tilCEP = findViewById(R.id.tilCEP);
        tilLogradouro = findViewById(R.id.tilLogradouro);
        tilNumero = findViewById(R.id.tilNumero);
        tilBairro = findViewById(R.id.tilBairro);
        tilCidade = findViewById(R.id.tilCidade);
        tilEstado = findViewById(R.id.tilEstado);
        tilSenha = findViewById(R.id.tilSenha);
        tilConfirmarSenha = findViewById(R.id.tilConfirmarSenha);

        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editTelefone = findViewById(R.id.editTelefone);
        editCPF = findViewById(R.id.editCPF);
        editNascimento = findViewById(R.id.editNascimento);
        editCEP = findViewById(R.id.editCEP);
        editLogradouro = findViewById(R.id.editLogradouro);
        editNumero = findViewById(R.id.editNumero);
        editComplemento = findViewById(R.id.editComplemento);
        editBairro = findViewById(R.id.editBairro);
        editCidade = findViewById(R.id.editCidade);
        editEstado = findViewById(R.id.editEstado);
        editSenha = findViewById(R.id.editSenha);
        editConfirmarSenha = findViewById(R.id.editConfirmarSenha);
        
        btnRegister = findViewById(R.id.btnRegister);
        pbRegister = findViewById(R.id.pbRegister);
    }

    private void setupClearErrorOnTouch() {
        View[] fields = {editNome, editEmail, editTelefone, editCPF, editNascimento, editCEP, 
                editLogradouro, editNumero, editComplemento, editBairro, editCidade, editEstado, editSenha, editConfirmarSenha};
        TextInputLayout[] layouts = {tilNome, tilEmail, tilTelefone, tilCPF, tilNascimento, tilCEP, 
                tilLogradouro, tilNumero, null, tilBairro, tilCidade, tilEstado, tilSenha, tilConfirmarSenha};

        for (int i = 0; i < fields.length; i++) {
            final TextInputLayout layout = layouts[i];
            if (layout != null && fields[i] != null) {
                fields[i].setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) layout.setError(null); });
                fields[i].setOnClickListener(v -> layout.setError(null));
            }
        }
    }

    private void setupDatePicker() {
        editNascimento.setFocusable(false);
        editNascimento.setClickable(true);
        View.OnClickListener listener = v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR) - 18; // Sugere 18 anos atrás
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
                String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                editNascimento.setText(date);
                tilNascimento.setError(null);
            }, year, month, day);
            datePickerDialog.show();
        };
        editNascimento.setOnClickListener(listener);
        tilNascimento.setEndIconOnClickListener(listener);
    }

    private void registerUser() {
        showLoading(true);
        
        User user = new User();
        user.setNome(getValue(editNome));
        user.setEmail(getValue(editEmail));
        user.setSenha(getValue(editSenha));
        user.setTipo("cliente");
        user.setTelefone(removeMask(getValue(editTelefone)));
        user.setCpfCnpj(removeMask(getValue(editCPF)));
        user.setLogradouro(getValue(editLogradouro));
        user.setCep(removeMask(getValue(editCEP)));
        user.setNumero(getValue(editNumero));
        user.setBairro(getValue(editBairro));
        user.setCidade(getValue(editCidade));
        user.setEstado(getValue(editEstado));
        user.setComplemento(getValue(editComplemento));
        user.setDataNascimento(getValue(editNascimento));

        RetrofitClient.getApiService().createUser(user).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse res = response.body();
                    if (res.getUsuario() != null) {
                        Toast.makeText(RegisterClientActivity.this, res.getMensagem(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterClientActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else if (res.getErros() != null) {
                        StringBuilder erros = new StringBuilder();
                        for (String e : res.getErros()) {
                            erros.append(e).append("\n");
                        }
                        Toast.makeText(RegisterClientActivity.this, erros.toString(), Toast.LENGTH_LONG).show();
                    } else if (res.getErro() != null) {
                        Toast.makeText(RegisterClientActivity.this, res.getErro(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegisterClientActivity.this, "Erro HTTP: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(RegisterClientActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean loading) {
        btnRegister.setEnabled(!loading);
        btnRegister.setText(loading ? "" : getString(R.string.cadastrar));
        pbRegister.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private String removeMask(String text) {
        return text.replaceAll("[^0-9]", "");
    }

    private String getValue(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private boolean validateFields() {
        boolean valid = true;
        resetErrors();

        if (isEmpty(editNome)) { tilNome.setError(getString(R.string.error_required)); valid = false; }
        if (isEmpty(editTelefone)) { tilTelefone.setError(getString(R.string.error_required)); valid = false; }
        if (isEmpty(editLogradouro)) { tilLogradouro.setError(getString(R.string.error_required)); valid = false; }
        if (isEmpty(editNumero)) { tilNumero.setError(getString(R.string.error_required)); valid = false; }
        if (isEmpty(editBairro)) { tilBairro.setError(getString(R.string.error_required)); valid = false; }
        if (isEmpty(editCidade)) { tilCidade.setError(getString(R.string.error_required)); valid = false; }
        if (isEmpty(editEstado)) { tilEstado.setError(getString(R.string.error_required)); valid = false; }
        if (isEmpty(editNascimento)) { tilNascimento.setError(getString(R.string.error_required)); valid = false; }

        String email = getValue(editEmail);
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.error_required));
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_invalid_email));
            valid = false;
        }

        String cpf = removeMask(getValue(editCPF));
        if (TextUtils.isEmpty(cpf)) {
            tilCPF.setError(getString(R.string.error_required));
            valid = false;
        } else if (cpf.length() != 11) {
            tilCPF.setError(getString(R.string.error_invalid_cpf));
            valid = false;
        }

        String cep = removeMask(getValue(editCEP));
        if (TextUtils.isEmpty(cep)) {
            tilCEP.setError(getString(R.string.error_required));
            valid = false;
        } else if (cep.length() != 8) {
            tilCEP.setError(getString(R.string.error_invalid_cep));
            valid = false;
        }

        String senha = getValue(editSenha);
        String confirmacao = getValue(editConfirmarSenha);

        if (TextUtils.isEmpty(senha)) {
            tilSenha.setError(getString(R.string.error_required));
            valid = false;
        } else if (senha.length() < 6) {
            tilSenha.setError("Mínimo 6 caracteres");
            valid = false;
        }
        
        if (TextUtils.isEmpty(confirmacao)) {
            tilConfirmarSenha.setError(getString(R.string.error_required));
            valid = false;
        } else if (!senha.equals(confirmacao)) {
            tilSenha.setError(getString(R.string.error_password_mismatch));
            tilConfirmarSenha.setError(getString(R.string.error_password_mismatch));
            valid = false;
        }

        return valid;
    }

    private void resetErrors() {
        tilNome.setError(null); tilEmail.setError(null); tilTelefone.setError(null);
        tilCPF.setError(null); tilNascimento.setError(null); tilCEP.setError(null);
        tilLogradouro.setError(null); tilNumero.setError(null); tilBairro.setError(null);
        tilCidade.setError(null); tilEstado.setError(null); tilSenha.setError(null);
        tilConfirmarSenha.setError(null);
    }

    private boolean isEmpty(TextInputEditText et) {
        return et.getText() == null || TextUtils.isEmpty(et.getText().toString().trim());
    }
}
