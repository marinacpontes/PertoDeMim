package com.app.pertodemim;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

// Tela que exibe detalhes de um serviço realizado no passado
public class ServiceDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        // Inicializa os textos da tela
        TextView tvServiceName = findViewById(R.id.tvServiceName);
        TextView tvProviderName = findViewById(R.id.tvProviderName);
        TextView tvPrice = findViewById(R.id.tvServicePrice);
        TextView tvTotal = findViewById(R.id.tvTotalPaid);
        TextView tvDate = findViewById(R.id.tvOrderDate);

        // Recupera os dados enviados pela tela de Perfil
        String name = getIntent().getStringExtra("serviceName");
        String provider = getIntent().getStringExtra("providerName");
        String price = getIntent().getStringExtra("price");
        String date = getIntent().getStringExtra("date");
        String address = getIntent().getStringExtra("address");
        String category = getIntent().getStringExtra("category");

        // Preenche a tela com os dados (ou usa valores padrão se vier vazio)
        if (name != null) tvServiceName.setText(name);
        if (provider != null) tvProviderName.setText(provider);
        if (price != null) {
            tvPrice.setText(price);
            tvTotal.setText(price);
        }
        if (date != null) tvDate.setText("Realizado em " + date);
        
        TextView tvAddress = findViewById(R.id.tvProviderAddress);
        if (address != null) tvAddress.setText(address);

        // Botão voltar
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Botão para falar com o fornecedor
        findViewById(R.id.btnChatProvider).setOnClickListener(v -> {
            Intent intent = new Intent(this, ConversationActivity.class);
            intent.putExtra("contactName", provider != null ? provider : "Fornecedor");
            startActivity(intent);
        });

        // Botão para contratar novamente
        findViewById(R.id.btnRepetirPedido).setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra("serviceName", name);
            intent.putExtra("providerName", provider);
            intent.putExtra("price", price);
            intent.putExtra("address", address);
            intent.putExtra("category", category);
            intent.putExtra("duration", "1h 30min"); // Exemplo de duração
            startActivity(intent);
        });
    }
}