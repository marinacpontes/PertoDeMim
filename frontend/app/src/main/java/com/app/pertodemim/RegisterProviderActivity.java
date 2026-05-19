package com.app.pertodemim;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.util.Calendar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.pertodemim.model.User;
import com.app.pertodemim.model.UserResponse;
import com.app.pertodemim.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Tela para cadastro de Fornecedores integrada com a API
public class RegisterProviderActivity extends AppCompatActivity {

    private TextInputLayout tilNomeFantasia, tilRazaoSocial, tilCNPJ, tilNascimento, tilCategoria,
            tilDescricao, tilEmail, tilWhatsapp, tilCEP,
            tilLogradouro, tilNumero, tilBairro, tilCidade, tilEstado,
            tilDias, tilAbertura, tilFechamento, tilSenha, tilConfirmarSenha;

    private TextInputEditText editNomeFantasia, editRazaoSocial, editCNPJ, editNascimento,
            editDescricao, editEmail, editWhatsapp, editCEP,
            editLogradouro, editNumero, editBairro, editCidade, editEstado,
            editSenha, editConfirmarSenha, editComplemento;

    private AutoCompleteTextView editCategoria, editDias, editAbertura, editFechamento;
    private MaterialButton btnRegister;
    private ProgressBar pbRegister;

    private boolean[] selectedDays;
    private final ArrayList<Integer> dayList = new ArrayList<>();
    private String[] daysArray;
    private final Set<String> selectedCategories = new HashSet<>();
    private String lastOtherCategoryText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_provider);

        initViews();
        setupClearErrorOnTouch();

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        setupCategoriasDialog();
        setupDayPicker();
        setupDatePicker();
        setupTimePickers();

        btnRegister.setOnClickListener(v -> {
            if (validateFields()) {
                registerProvider();
            }
        });
    }

    private void initViews() {
        tilNomeFantasia = findViewById(R.id.tilNomeFantasia);
        tilRazaoSocial = findViewById(R.id.tilRazaoSocial);
        tilCNPJ = findViewById(R.id.tilCNPJ);
        tilNascimento = findViewById(R.id.tilNascimento);
        tilCategoria = findViewById(R.id.tilCategoria);
        tilDescricao = findViewById(R.id.tilDescricao);
        tilEmail = findViewById(R.id.tilEmail);
        tilWhatsapp = findViewById(R.id.tilWhatsapp);
        tilCEP = findViewById(R.id.tilCEP);
        tilLogradouro = findViewById(R.id.tilLogradouro);
        tilNumero = findViewById(R.id.tilNumero);
        tilBairro = findViewById(R.id.tilBairro);
        tilCidade = findViewById(R.id.tilCidade);
        tilEstado = findViewById(R.id.tilEstado);
        tilDias = findViewById(R.id.tilDias);
        tilAbertura = findViewById(R.id.tilAbertura);
        tilFechamento = findViewById(R.id.tilFechamento);
        tilSenha = findViewById(R.id.tilSenha);
        tilConfirmarSenha = findViewById(R.id.tilConfirmarSenha);

        editNomeFantasia = findViewById(R.id.editNomeFantasia);
        editRazaoSocial = findViewById(R.id.editRazaoSocial);
        editCNPJ = findViewById(R.id.editCNPJ);
        editNascimento = findViewById(R.id.editNascimento);
        editDescricao = findViewById(R.id.editDescricao);
        editEmail = findViewById(R.id.editEmail);
        editWhatsapp = findViewById(R.id.editWhatsapp);
        editCEP = findViewById(R.id.editCEP);
        editLogradouro = findViewById(R.id.editLogradouro);
        editNumero = findViewById(R.id.editNumero);
        editBairro = findViewById(R.id.editBairro);
        editCidade = findViewById(R.id.editCidade);
        editEstado = findViewById(R.id.editEstado);
        editSenha = findViewById(R.id.editSenha);
        editConfirmarSenha = findViewById(R.id.editConfirmarSenha);
        editComplemento = findViewById(R.id.editComplemento);

        editCategoria = findViewById(R.id.editCategoria);
        editDias = findViewById(R.id.editDias);
        editAbertura = findViewById(R.id.editAbertura);
        editFechamento = findViewById(R.id.editFechamento);
        
        btnRegister = findViewById(R.id.btnRegister);
        pbRegister = findViewById(R.id.pbRegister);

        daysArray = new String[]{
                getString(R.string.segunda), getString(R.string.terca), getString(R.string.quarta),
                getString(R.string.quinta), getString(R.string.sexta), getString(R.string.sabado),
                getString(R.string.domingo)
        };
        selectedDays = new boolean[daysArray.length];
    }

    private void setupClearErrorOnTouch() {
        View[] fields = {editNomeFantasia, editRazaoSocial, editCNPJ, editNascimento, editCategoria, 
                editDescricao, editEmail, editWhatsapp, editCEP, editLogradouro, editNumero, 
                editBairro, editCidade, editEstado, editDias, editAbertura, editFechamento, editSenha, editConfirmarSenha};
        TextInputLayout[] layouts = {tilNomeFantasia, tilRazaoSocial, tilCNPJ, tilNascimento, tilCategoria, 
                tilDescricao, tilEmail, tilWhatsapp, tilCEP, tilLogradouro, tilNumero, 
                tilBairro, tilCidade, tilEstado, tilDias, tilAbertura, tilFechamento, tilSenha, tilConfirmarSenha};

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
            int year = calendar.get(Calendar.YEAR) - 18;
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

    private void registerProvider() {
        showLoading(true);

        User user = new User();
        String nomeEmpresa = getValue(editNomeFantasia);
        if (nomeEmpresa.isEmpty()) nomeEmpresa = getValue(editRazaoSocial);
        
        user.setNome(nomeEmpresa);
        user.setEmail(getValue(editEmail));
        user.setSenha(getValue(editSenha));
        user.setTipo("fornecedor");
        user.setTelefone(removeMask(getValue(editWhatsapp)));
        user.setCpfCnpj(removeMask(getValue(editCNPJ)));
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
                    Toast.makeText(RegisterProviderActivity.this, response.body().getMensagem(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterProviderActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    String errorMsg = "Erro no cadastro. Verifique os dados.";
                    try {
                        UserResponse res = new com.google.gson.Gson().fromJson(response.errorBody().string(), UserResponse.class);
                        if (res != null) {
                            if (res.getErros() != null) {
                                StringBuilder sb = new StringBuilder();
                                for (String e : res.getErros()) sb.append(e).append("\n");
                                errorMsg = sb.toString();
                            } else if (res.getErro() != null) {
                                errorMsg = res.getErro();
                            }
                        }
                    } catch (Exception ignored) {}
                    Toast.makeText(RegisterProviderActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(RegisterProviderActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private String getValue(AutoCompleteTextView et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void setupCategoriasDialog() {
        View.OnClickListener listener = v -> {
            tilCategoria.setError(null);
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_filtros_multi, null);
            LinearLayout container = view.findViewById(R.id.llOptionsContainer);
            
            String[] categorias = {
                getString(R.string.beleza_estetica), getString(R.string.saude), 
                getString(R.string.alimentacao), getString(R.string.educacao), 
                getString(R.string.manutencao), getString(R.string.tecnologia)
            };
            
            List<CheckBox> checkBoxes = new ArrayList<>();
            for (String cat : categorias) {
                CheckBox cb = new CheckBox(this);
                cb.setText(cat);
                cb.setPadding(16, 16, 16, 16);
                cb.setChecked(selectedCategories.contains(cat));
                container.addView(cb);
                checkBoxes.add(cb);
            }

            CheckBox cbOutros = new CheckBox(this);
            cbOutros.setText(R.string.outra_categoria);
            cbOutros.setPadding(16, 16, 16, 16);
            cbOutros.setChecked(selectedCategories.contains("Outros"));
            container.addView(cbOutros);
            
            EditText editOther = new EditText(this);
            editOther.setHint(R.string.adicionar_categoria);
            editOther.setText(lastOtherCategoryText);
            editOther.setTextSize(16);
            editOther.setVisibility(cbOutros.isChecked() ? View.VISIBLE : View.GONE);
            container.addView(editOther);
            
            cbOutros.setOnCheckedChangeListener((buttonView, isChecked) -> editOther.setVisibility(isChecked ? View.VISIBLE : View.GONE));

            new AlertDialog.Builder(this)
                .setTitle(R.string.selecione_categoria)
                .setView(view)
                .setPositiveButton("OK", (dialog, which) -> {
                    selectedCategories.clear();
                    StringBuilder sb = new StringBuilder();
                    for (CheckBox cb : checkBoxes) {
                        if (cb.isChecked()) {
                            selectedCategories.add(cb.getText().toString());
                            if (sb.length() > 0) sb.append(", ");
                            sb.append(cb.getText().toString());
                        }
                    }
                    if (cbOutros.isChecked()) {
                        selectedCategories.add("Outros");
                        lastOtherCategoryText = editOther.getText().toString();
                        if (!lastOtherCategoryText.isEmpty()) {
                            if (sb.length() > 0) sb.append(", ");
                            sb.append(lastOtherCategoryText);
                        }
                    }
                    editCategoria.setText(sb.toString());
                })
                .setNegativeButton("Cancelar", null).show();
        };
        editCategoria.setOnClickListener(listener);
        tilCategoria.setEndIconOnClickListener(listener);
    }

    private void setupDayPicker() {
        View.OnClickListener listener = v -> {
            tilDias.setError(null);
            new AlertDialog.Builder(this)
                .setTitle(R.string.selecione_dias)
                .setCancelable(false)
                .setMultiChoiceItems(daysArray, selectedDays, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        if (!dayList.contains(which)) { dayList.add(which); Collections.sort(dayList); }
                    } else {
                        dayList.remove(Integer.valueOf(which));
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < dayList.size(); i++) {
                        sb.append(daysArray[dayList.get(i)]).append(i == dayList.size() - 1 ? "" : ", ");
                    }
                    editDias.setText(sb.toString());
                })
                .setNegativeButton("Cancelar", null)
                .setNeutralButton("Limpar", (dialog, which) -> {
                    Arrays.fill(selectedDays, false);
                    dayList.clear(); editDias.setText("");
                }).show();
        };
        editDias.setOnClickListener(listener);
        tilDias.setEndIconOnClickListener(listener);
    }

    private void setupTimePickers() {
        View.OnClickListener aperturaListener = v -> showTimePicker(editAbertura);
        editAbertura.setOnClickListener(aperturaListener);
        tilAbertura.setEndIconOnClickListener(aperturaListener);

        View.OnClickListener fechamentoListener = v -> showTimePicker(editFechamento);
        editFechamento.setOnClickListener(fechamentoListener);
        tilFechamento.setEndIconOnClickListener(fechamentoListener);
    }

    private void showTimePicker(AutoCompleteTextView et) {
        new TimePickerDialog(this, (view, h, m) -> {
            et.setText(String.format(Locale.getDefault(), "%02d:%02d", h, m));
            if (et == editAbertura) tilAbertura.setError(null); else tilFechamento.setError(null);
        }, 12, 0, true).show();
    }

    private boolean validateFields() {
        boolean valid = true;
        resetErrors();

        if (isEmpty(editRazaoSocial)) { tilRazaoSocial.setError(getString(R.string.error_required)); valid = false; }
        if (isEmpty(editDescricao)) { tilDescricao.setError(getString(R.string.error_required)); valid = false; }
        if (isEmpty(editWhatsapp)) { tilWhatsapp.setError(getString(R.string.error_required)); valid = false; }
        if (isEmpty(editLogradouro)) { tilLogradouro.setError(getString(R.string.error_required)); valid = false; }
        if (isEmpty(editNumero)) { tilNumero.setError(getString(R.string.error_required)); valid = false; }
        if (isEmpty(editBairro)) { tilBairro.setError(getString(R.string.error_required)); valid = false; }
        if (isEmpty(editCidade)) { tilCidade.setError(getString(R.string.error_required)); valid = false; }
        if (isEmpty(editEstado)) { tilEstado.setError(getString(R.string.error_required)); valid = false; }
        if (isEmpty(editNascimento)) { tilNascimento.setError(getString(R.string.error_required)); valid = false; }
        if (isEmpty(editDias)) { tilDias.setError(getString(R.string.error_required)); valid = false; }
        if (isEmpty(editCategoria)) { tilCategoria.setError(getString(R.string.error_required)); valid = false; }
        if (isEmpty(editAbertura)) { tilAbertura.setError(getString(R.string.error_required)); valid = false; }
        if (isEmpty(editFechamento)) { tilFechamento.setError(getString(R.string.error_required)); valid = false; }

        if (!isEmpty(editAbertura) && !isEmpty(editFechamento)) {
            if (getValue(editAbertura).compareTo(getValue(editFechamento)) >= 0) {
                tilAbertura.setError(getString(R.string.error_opening_time));
                tilFechamento.setError(getString(R.string.error_closing_time));
                valid = false;
            }
        }

        String email = getValue(editEmail);
        if (TextUtils.isEmpty(email)) { tilEmail.setError(getString(R.string.error_required)); valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { tilEmail.setError(getString(R.string.error_invalid_email)); valid = false; }

        String idFiscal = removeMask(getValue(editCNPJ));
        if (idFiscal.length() != 11 && idFiscal.length() != 14) {
            tilCNPJ.setError("CPF (11) ou CNPJ (14) inválido");
            valid = false;
        }
        if (getValue(editCEP).length() != 8) { tilCEP.setError(getString(R.string.error_invalid_cep)); valid = false; }

        String s = getValue(editSenha);
        String c = getValue(editConfirmarSenha);
        if (s.isEmpty()) { tilSenha.setError(getString(R.string.error_required)); valid = false; }
        else if (s.length() < 6) { tilSenha.setError("Mínimo 6 caracteres"); valid = false; }
        
        if (c.isEmpty()) { tilConfirmarSenha.setError(getString(R.string.error_required)); valid = false; }
        else if (!s.equals(c)) { tilSenha.setError(getString(R.string.error_password_mismatch)); tilConfirmarSenha.setError(getString(R.string.error_password_mismatch)); valid = false; }

        return valid;
    }

    private void resetErrors() {
        TextInputLayout[] layouts = {tilNomeFantasia, tilRazaoSocial, tilCNPJ, tilNascimento, tilCategoria,
                tilDescricao, tilEmail, tilWhatsapp, tilCEP, tilLogradouro, tilNumero, tilBairro, 
                tilCidade, tilEstado, tilDias, tilAbertura, tilFechamento, tilSenha, tilConfirmarSenha};
        for (TextInputLayout l : layouts) if (l != null) l.setError(null);
    }

    private boolean isEmpty(View v) {
        if (v instanceof EditText) {
            CharSequence text = ((EditText) v).getText();
            return text == null || TextUtils.isEmpty(text.toString().trim());
        }
        return true;
    }
}
