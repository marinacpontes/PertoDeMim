package com.app.pertodemim;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddressDetailActivity extends AppCompatActivity {

    private TextInputLayout tilCEP, tilLogradouro, tilNumero, tilBairro, tilCidade, tilEstado;
    private TextInputEditText editCEP, editLogradouro, editNumero, editBairro, editComplemento, editLabel, editCidade, editEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_detail);

        tilCEP = findViewById(R.id.tilCEP); tilLogradouro = findViewById(R.id.tilLogradouro); tilNumero = findViewById(R.id.tilNumero);
        tilBairro = findViewById(R.id.tilBairro); tilCidade = findViewById(R.id.tilCidade); tilEstado = findViewById(R.id.tilEstado);
        editLabel = findViewById(R.id.editAddressLabel); editCEP = findViewById(R.id.editCEP); editLogradouro = findViewById(R.id.editLogradouro);
        editNumero = findViewById(R.id.editNumero); editComplemento = findViewById(R.id.editComplemento); editBairro = findViewById(R.id.editBairro);
        editCidade = findViewById(R.id.editCidade); editEstado = findViewById(R.id.editEstado);

        String mode = getIntent().getStringExtra("mode");
        if ("edit".equals(mode)) {
            ((TextView) findViewById(R.id.tvTitle)).setText(R.string.editar_endereco_title);
            editLabel.setText("Minha Casa"); editCEP.setText("60000000"); editLogradouro.setText("Rua das Palmeiras");
            editNumero.setText("456"); editComplemento.setText("Apto 101"); editBairro.setText("Jardim Paulista");
            editCidade.setText("São Paulo"); editEstado.setText("SP");
        } else { ((TextView) findViewById(R.id.tvTitle)).setText(R.string.novo_endereco_title); }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSaveAddress).setOnClickListener(v -> {
            if (validateFields()) {
                Toast.makeText(this, "edit".equals(mode) ? "Endereço atualizado!" : "Endereço salvo com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private boolean validateFields() {
        tilCEP.setError(null); tilLogradouro.setError(null); tilNumero.setError(null); tilBairro.setError(null); tilCidade.setError(null); tilEstado.setError(null);
        String cep = editCEP.getText().toString().replaceAll("[^0-9]", ""), log = editLogradouro.getText().toString().trim(), num = editNumero.getText().toString().trim(), bai = editBairro.getText().toString().trim(), cid = editCidade.getText().toString().trim(), est = editEstado.getText().toString().trim();
        boolean valid = true;
        if (TextUtils.isEmpty(cep)) { tilCEP.setError(getString(R.string.error_required)); valid = false; } else if (cep.length() != 8) { tilCEP.setError(getString(R.string.error_invalid_cep)); valid = false; }
        if (TextUtils.isEmpty(log)) { tilLogradouro.setError(getString(R.string.error_required)); valid = false; }
        if (TextUtils.isEmpty(num)) { tilNumero.setError(getString(R.string.error_required)); valid = false; }
        if (TextUtils.isEmpty(bai)) { tilBairro.setError(getString(R.string.error_required)); valid = false; }
        if (TextUtils.isEmpty(cid)) { tilCidade.setError(getString(R.string.error_required)); valid = false; }
        if (TextUtils.isEmpty(est)) { tilEstado.setError(getString(R.string.error_required)); valid = false; }
        return valid;
    }
}
