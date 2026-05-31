package com.app.pertodemim;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigationChat);
        bottomNavigation.setSelectedItemId(R.id.nav_chat);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_mapa) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_vitrine) {
                startActivity(new Intent(this, VitrineActivity.class));
                finish();
                return true;
            }
            return true;
        });

        findViewById(R.id.chatItem1).setOnClickListener(v -> openConversation("Salão Bela Forma"));
        findViewById(R.id.chatItem2).setOnClickListener(v -> openConversation("TechFix Consertos"));
        findViewById(R.id.chatItem3).setOnClickListener(v -> openConversation("Pizzaria Napolitana"));
        findViewById(R.id.chatItem4).setOnClickListener(v -> openConversation("Academia FitLife"));
    }

    private void openConversation(String name) {
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra("contactName", name);
        startActivity(intent);
    }
}
