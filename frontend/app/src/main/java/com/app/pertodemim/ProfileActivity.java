package com.app.pertodemim;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Tela de Perfil do usuário logado
public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Define o layout da tela de perfil
        setContentView(R.layout.activity_profile);

        // Inicializa o botão de sair (Logout)
        View btnSairLayout = findViewById(R.id.btnSairLayout);
        btnSairLayout.setOnClickListener(v -> {
            // Volta para a tela de login limpando o histórico de telas abertas
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Configura a barra de navegação inferior
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        // Marca o item "Perfil" como o selecionado no momento
        bottomNavigation.setSelectedItemId(R.id.nav_perfil);

        // Define as ações ao clicar nos itens da barra de navegação
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_mapa) {
                // Abre a tela inicial (Mapa) e fecha a tela atual
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_chat) {
                // Abre a tela de Chat e fecha a tela atual
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

    // Adiciona interatividade aos botões de configuração e histórico
    private void setupInteractions() {
        findViewById(R.id.btnEditProfile).setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));

        findViewById(R.id.ivSettings).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        
        findViewById(R.id.tvManageAddresses).setOnClickListener(v -> startActivity(new Intent(this, ManageAddressesActivity.class)));

        findViewById(R.id.btnNotifications).setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));
        
        findViewById(R.id.btnPrivacy).setOnClickListener(v -> startActivity(new Intent(this, PrivacyActivity.class)));
        
        findViewById(R.id.btnHelp).setOnClickListener(v -> startActivity(new Intent(this, HelpActivity.class)));

        // Configura cliques nos itens do histórico
        findViewById(R.id.cardHistory1).setOnClickListener(v -> 
            openServiceDetail("Corte + Escova", "Salão Bela Forma", "R$ 80,00", "15/04/2026", "Rua das Flores, 123 - Centro", "Beleza"));
            
        findViewById(R.id.cardHistory2).setOnClickListener(v -> 
            openServiceDetail("Manutenção de Notebook", "TechFix Consertos", "R$ 150,00", "10/04/2026", "Av. Central, 500 - Sala 4", "Tecnologia"));
            
        findViewById(R.id.cardHistory3).setOnClickListener(v -> 
            openServiceDetail("Pizza Grande", "Pizzaria Napolitana", "R$ 45,00", "08/04/2026", "Rua Gastronômica, 10 - Bairro Alto", "Alimentação"));
    }

    // Abre a tela de detalhes do serviço passando as informações
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