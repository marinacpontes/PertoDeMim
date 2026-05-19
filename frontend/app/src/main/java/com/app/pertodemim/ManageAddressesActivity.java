package com.app.pertodemim;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

// Tela para gerenciar os endereços salvos do usuário
public class ManageAddressesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Define o layout da tela de gerenciamento de endereços
        setContentView(R.layout.activity_manage_addresses);

        // Botão de voltar
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        setupInteractions();
    }

    // Configura os cliques dos botões de editar e adicionar
    private void setupInteractions() {
        // Botão "Editar endereço" (dentro do card)
        findViewById(R.id.btnEditAddress).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressDetailActivity.class);
            intent.putExtra("mode", "edit");
            startActivity(intent);
        });

        // Botão "Adicionar novo endereço" (no topo)
        findViewById(R.id.btnAddAddress).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressDetailActivity.class);
            intent.putExtra("mode", "add");
            startActivity(intent);
        });
    }
}