package com.app.pertodemim;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            // Aqui seria implementada a lógica para salvar as alterações
            Toast.makeText(this, "Alterações salvas com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
