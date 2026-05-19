package com.app.pertodemim;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

// Tela para adicionar ou editar um endereço
public class AddressDetailActivity extends AppCompatActivity {

    private TextInputLayout tilCEP, tilLogradouro, tilNumero, tilBairro;
    private TextInputEditText editCEP, editLogradouro, editNumero, editBairro, editComplemento, editLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_detail);

        TextView tvTitle = findViewById(R.id.tvTitle);
        
        tilCEP = findViewById(R.id.tilCEP);
        tilLogradouro = findViewById(R.id.tilLogradouro);
        tilNumero = findViewById(R.id.tilNumero);
        tilBairro = findViewById(R.id.tilBairro);

        editLabel = findViewById(R.id.editAddressLabel);
        editCEP = findViewById(R.id.editCEP);
        editLogradouro = findViewById(R.id.editLogradouro);
        editNumero = findViewById(R.id.editNumero);
        editComplemento = findViewById(R.id.editComplemento);
        editBairro = findViewById(R.id.editBairro);
        Button btnSave = findViewById(R.id.btnSaveAddress);

        // Verifica se é edição ou novo endereço
        String mode = getIntent().getStringExtra("mode");
        if ("edit".equals(mode)) {
            tvTitle.setText(R.string.editar_endereco_title);
            // Preenche com dados fictícios para simular edição
            editLabel.setText("Minha Casa");
            editCEP.setText("60000000");
            editLogradouro.setText("Rua das Palmeiras");
            editNumero.setText("456");
            editComplemento.setText("Apto 101");
            editBairro.setText("Jardim Paulista");
        } else {
            tvTitle.setText(R.string.novo_endereco_title);
        }

        // Botão voltar
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Botão salvar com validação
        btnSave.setOnClickListener(v -> {
            if (validateFields()) {
                String successMsg = "edit".equals(mode) ? "Endereço atualizado!" : "Endereço salvo com sucesso!";
                Toast.makeText(this, successMsg, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private boolean validateFields() {
        boolean valid = true;
        
        // Reset erros
        tilCEP.setError(null);
        tilLogradouro.setError(null);
        tilNumero.setError(null);
        tilBairro.setError(null);

        String cep = editCEP.getText().toString().replaceAll("[^0-9]", "");
        String logradouro = editLogradouro.getText().toString().trim();
        String numero = editNumero.getText().toString().trim();
        String bairro = editBairro.getText().toString().trim();

        if (TextUtils.isEmpty(cep)) {
            tilCEP.setError(getString(R.string.error_required));
            valid = false;
        } else if (cep.length() != 8) {
            tilCEP.setError(getString(R.string.error_invalid_cep));
            valid = false;
        }

        if (TextUtils.isEmpty(logradouro)) {
            tilLogradouro.setError(getString(R.string.error_required));
            valid = false;
        }

        if (TextUtils.isEmpty(numero)) {
            tilNumero.setError(getString(R.string.error_required));
            valid = false;
        }

        if (TextUtils.isEmpty(bairro)) {
            tilBairro.setError(getString(R.string.error_required));
            valid = false;
        }

        return valid;
    }
}
