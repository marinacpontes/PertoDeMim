package com.app.pertodemim;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

// Tela de confirmação de pedido realizado com sucesso
public class ConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        // Botão para voltar imediatamente sem esperar o timer
        findViewById(R.id.btnBackHome).setOnClickListener(v -> goHome());

        // Aguarda 3 segundos e redireciona automaticamente para a tela do mapa (Home)
        new Handler(Looper.getMainLooper()).postDelayed(this::goHome, 3000);
    }

    // Função que executa a navegação para a Home limpando a pilha de telas
    private void goHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}