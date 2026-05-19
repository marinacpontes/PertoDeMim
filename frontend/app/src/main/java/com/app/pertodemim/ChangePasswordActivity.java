package com.app.pertodemim;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;

// Tela para alterar a senha do usuário logado
public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputLayout tilCurrent, tilNew, tilConfirm;
    private TextInputEditText editCurrent, editNew, editConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initViews();

        // Botão voltar
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Botão salvar
        Button btnSave = findViewById(R.id.btnSavePassword);
        btnSave.setOnClickListener(v -> {
            if (validateFields()) {
                Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void initViews() {
        tilCurrent = findViewById(R.id.tilCurrentPassword);
        tilNew = findViewById(R.id.tilNewPassword);
        tilConfirm = findViewById(R.id.tilConfirmNewPassword);
        
        editCurrent = findViewById(R.id.editCurrentPassword);
        editNew = findViewById(R.id.editNewPassword);
        editConfirm = findViewById(R.id.editConfirmNewPassword);
    }

    private boolean validateFields() {
        boolean valid = true;
        tilCurrent.setError(null);
        tilNew.setError(null);
        tilConfirm.setError(null);

        String current = Objects.requireNonNull(editCurrent.getText()).toString();
        String newPass = Objects.requireNonNull(editNew.getText()).toString();
        String confirm = Objects.requireNonNull(editConfirm.getText()).toString();

        if (TextUtils.isEmpty(current)) {
            tilCurrent.setError(getString(R.string.error_required));
            valid = false;
        }
        if (TextUtils.isEmpty(newPass)) {
            tilNew.setError(getString(R.string.error_required));
            valid = false;
        }
        if (TextUtils.isEmpty(confirm)) {
            tilConfirm.setError(getString(R.string.error_required));
            valid = false;
        } else if (!newPass.equals(confirm)) {
            tilNew.setError(getString(R.string.error_password_mismatch));
            tilConfirm.setError(getString(R.string.error_password_mismatch));
            valid = false;
        }

        return valid;
    }
}