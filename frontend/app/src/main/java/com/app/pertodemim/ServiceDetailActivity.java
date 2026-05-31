package com.app.pertodemim;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ServiceDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        TextView tvServiceName = findViewById(R.id.tvServiceName), tvProviderName = findViewById(R.id.tvProviderName), tvPrice = findViewById(R.id.tvServicePrice), tvTotal = findViewById(R.id.tvTotalPaid), tvDate = findViewById(R.id.tvOrderDate), tvAddress = findViewById(R.id.tvProviderAddress);

        String name = getIntent().getStringExtra("serviceName"), provider = getIntent().getStringExtra("providerName"), price = getIntent().getStringExtra("price"), date = getIntent().getStringExtra("date"), address = getIntent().getStringExtra("address"), category = getIntent().getStringExtra("category");

        if (name != null) tvServiceName.setText(name);
        if (provider != null) tvProviderName.setText(provider);
        if (price != null) { tvPrice.setText(price); tvTotal.setText(price); }
        if (date != null) tvDate.setText("Realizado em " + date);
        if (address != null) tvAddress.setText(address);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnChatProvider).setOnClickListener(v -> {
            Intent intent = new Intent(this, ConversationActivity.class);
            intent.putExtra("contactName", provider != null ? provider : "Fornecedor");
            startActivity(intent);
        });
        findViewById(R.id.btnRepetirPedido).setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra("serviceName", name); intent.putExtra("providerName", provider); intent.putExtra("price", price);
            intent.putExtra("address", address); intent.putExtra("category", category); intent.putExtra("duration", "1h 30min");
            startActivity(intent);
        });
    }
}
