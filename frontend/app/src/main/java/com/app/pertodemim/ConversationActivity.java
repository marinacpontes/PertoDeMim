package com.app.pertodemim;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConversationActivity extends AppCompatActivity {

    private LinearLayout llMessagesContainer;
    private EditText editMessage;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        llMessagesContainer = findViewById(R.id.llMessagesContainer);
        editMessage = findViewById(R.id.editMessage);
        scrollView = findViewById(R.id.scrollView);
        TextView tvChatTitle = findViewById(R.id.tvChatTitle);
        ImageView ivChatAvatar = findViewById(R.id.ivChatAvatar);

        String contactName = getIntent().getStringExtra("contactName");
        if (contactName != null && !contactName.isEmpty()) {
            tvChatTitle.setText(contactName);
            setAvatar(contactName, ivChatAvatar);
            loadInitialMessages(contactName);
        }

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        findViewById(R.id.btnSend).setOnClickListener(v -> sendMessage());
        editMessage.setOnEditorActionListener((v, actionId, event) -> { sendMessage(); return true; });

        llMessagesContainer.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) scrollView.postDelayed(() -> scrollView.fullScroll(View.FOCUS_DOWN), 100);
        });
    }

    private void setAvatar(String name, ImageView ivAvatar) {
        if (ivAvatar == null) return;
        switch (name) {
            case "Salão Bela Forma": ivAvatar.setImageResource(R.drawable.logobelaforma); break;
            case "TechFix Consertos": ivAvatar.setImageResource(R.drawable.logotechfix); break;
            case "Pizzaria Napolitana": ivAvatar.setImageResource(R.drawable.logopizzarianapolitana); break;
            case "Academia FitLife": ivAvatar.setImageResource(R.drawable.logofitlife); break;
            case "Auto Mecânica Silva": ivAvatar.setImageResource(R.drawable.logoautomecanicasilva); break;
            case "Escola de Idiomas Global": ivAvatar.setImageResource(R.drawable.logoescoladeidiomasglobal); break;
        }
    }

    private void loadInitialMessages(String name) {
        llMessagesContainer.removeAllViews();
        switch (name) {
            case "Salão Bela Forma":
                addMessageToUI("Olá! Gostaria de agendar um horário", "15:25", true);
                addMessageToUI("Olá! Claro, temos horários disponíveis amanhã. Qual período prefere?", "15:26", false);
                addMessageToUI("Pode ser às 14h?", "15:28", true);
                addMessageToUI("Perfeito! Agendado para amanhã às 14h. Qual serviço deseja?", "15:29", false);
                addMessageToUI("Corte e escova, por favor", "15:29", true);
                addMessageToUI("Obrigada! Até amanhã às 14h!", "15:30", false);
                break;
            case "TechFix Consertos":
                addMessageToUI("Bom dia! Alguma novidade sobre meu notebook?", "09:00", true);
                addMessageToUI("Bom dia! Sim, a manutenção foi concluída.", "14:15", false);
                addMessageToUI("Seu notebook está pronto para retirar!", "14:20", false);
                break;
            case "Pizzaria Napolitana":
                addMessageToUI("Boa noite! Gostaria de uma pizza grande", "12:30", true);
                addMessageToUI("Pedido confirmado! Tempo estimado: 30 min", "12:45", false);
                break;
            case "Academia FitLife":
                addMessageToUI("Olá! Quais são os planos mensais?", "Ontem", true);
                addMessageToUI("Temos uma promoção especial este mês!", "Ontem", false);
                break;
        }
    }

    private void sendMessage() {
        String text = editMessage.getText().toString().trim();
        if (text.isEmpty()) return;
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        addMessageToUI(text, currentTime, true);
        editMessage.setText("");
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void addMessageToUI(String text, String time, boolean isMe) {
        View bubble = LayoutInflater.from(this).inflate(isMe ? R.layout.item_message_me : R.layout.item_message_other, llMessagesContainer, false);
        ((TextView) bubble.findViewById(R.id.tvMessageText)).setText(text);
        ((TextView) bubble.findViewById(R.id.tvMessageTime)).setText(time);
        llMessagesContainer.addView(bubble);
    }
}
