package com.app.pertodemim;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViewById(R.id.btnSairLayout).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_perfil);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_mapa) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ChatActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_vitrine) {
                startActivity(new Intent(this, VitrineActivity.class));
                finish();
                return true;
            }
            return true;
        });

        setupInteractions();
    }

    private void setupInteractions() {
        findViewById(R.id.btnEditProfile).setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));
        findViewById(R.id.ivSettings).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        findViewById(R.id.tvManageAddresses).setOnClickListener(v -> startActivity(new Intent(this, ManageAddressesActivity.class)));
        findViewById(R.id.btnNotifications).setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));
        findViewById(R.id.btnPrivacy).setOnClickListener(v -> startActivity(new Intent(this, PrivacyActivity.class)));
        findViewById(R.id.btnHelp).setOnClickListener(v -> startActivity(new Intent(this, HelpActivity.class)));

        findViewById(R.id.cardHistory1).setOnClickListener(v -> openServiceDetail("Corte + Escova", "Salão Bela Forma", "R$ 80,00", "15/04/2026", "Rua das Flores, 123 - Centro", "Beleza"));
        findViewById(R.id.cardHistory2).setOnClickListener(v -> openServiceDetail("Manutenção de Notebook", "TechFix Consertos", "R$ 150,00", "10/04/2026", "Av. Central, 500 - Sala 4", "Tecnologia"));
        findViewById(R.id.cardHistory3).setOnClickListener(v -> openServiceDetail("Pizza Grande", "Pizzaria Napolitana", "R$ 45,00", "08/04/2026", "Rua Gastronômica, 10 - Bairro Alto", "Alimentação"));
    }

    private void openServiceDetail(String name, String provider, String price, String date, String address, String category) {
        Intent intent = new Intent(this, ServiceDetailActivity.class);
        intent.putExtra("serviceName", name);
        intent.putExtra("providerName", provider);
        intent.putExtra("price", price);
        intent.putExtra("date", date);
        intent.putExtra("address", address);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}
