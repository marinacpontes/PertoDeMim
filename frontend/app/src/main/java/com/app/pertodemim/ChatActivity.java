package com.app.pertodemim;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Tela de Chat que exibe a lista de conversas recentes do usuário
public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Define o layout da tela de chat (lista de mensagens)
        setContentView(R.layout.activity_chat);

        // Inicializa a barra de navegação inferior (Menu)
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigationChat);
        // Marca o ícone do Chat como o item selecionado no momento
        bottomNavigation.setSelectedItemId(R.id.nav_chat);

        // Define o que acontece ao clicar nos itens do menu inferior
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_mapa) {
                // Abre a tela inicial (Mapa) e encerra a atual
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_perfil) {
                // Abre a tela de Perfil do usuário e encerra a atual
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_vitrine) {
                // Abre a vitrine de serviços e encerra a atual
                startActivity(new Intent(this, VitrineActivity.class));
                finish();
                return true;
            }
            return true;
        });

        // Configura o clique nas conversas para abrir o chat detalhado passando o nome do contato
        findViewById(R.id.chatItem1).setOnClickListener(v -> openConversation("Salão Bela Forma"));
        findViewById(R.id.chatItem2).setOnClickListener(v -> openConversation("TechFix Consertos"));
        findViewById(R.id.chatItem3).setOnClickListener(v -> openConversation("Pizzaria Napolitana"));
        findViewById(R.id.chatItem4).setOnClickListener(v -> openConversation("Academia FitLife"));
    }

    // Função auxiliar para abrir a tela de conversa com um nome específico
    private void openConversation(String name) {
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra("contactName", name);
        startActivity(intent);
    }
}