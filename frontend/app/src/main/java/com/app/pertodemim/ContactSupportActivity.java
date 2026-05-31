package com.app.pertodemim;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ContactSupportActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_support);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnWhatsAppSupport).setOnClickListener(v -> Toast.makeText(this, "Redirecionando para o WhatsApp...", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnEmailSupport).setOnClickListener(v -> Toast.makeText(this, "Abrindo seu app de e-mail...", Toast.LENGTH_SHORT).show());
    }
}
