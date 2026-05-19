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

// Tela de detalhes da conversa (Chat individual)
public class ConversationActivity extends AppCompatActivity {

    // Componentes de interface (lista de mensagens, campo de texto e rolagem)
    private LinearLayout llMessagesContainer;
    private EditText editMessage;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Define o layout da tela de detalhe da conversa
        setContentView(R.layout.activity_chat_detail);

        // Inicializa os componentes
        llMessagesContainer = findViewById(R.id.llMessagesContainer);
        editMessage = findViewById(R.id.editMessage);
        scrollView = findViewById(R.id.scrollView);
        TextView tvChatTitle = findViewById(R.id.tvChatTitle);

        // Recupera o nome do contato enviado pela tela anterior
        String contactName = getIntent().getStringExtra("contactName");
        if (contactName != null && !contactName.isEmpty()) {
            tvChatTitle.setText(contactName);
            loadInitialMessages(contactName);
        }

        // Configura o botão de voltar: fecha a tela atual e retorna à lista de chats
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Configura o botão de enviar mensagem
        View btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> sendMessage());

        // Permite enviar a mensagem ao apertar o botão "Enter" do teclado virtual
        editMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });

        // Garante que a conversa role para a última mensagem quando o teclado abrir (layout mudar)
        llMessagesContainer.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                scrollView.postDelayed(() -> scrollView.fullScroll(View.FOCUS_DOWN), 100);
            }
        });
    }

    // Simula o carregamento de mensagens iniciais dependendo do contato
    private void loadInitialMessages(String name) {
        llMessagesContainer.removeAllViews(); // Limpa as mensagens estáticas do XML
        
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

    // Função que processa e exibe a mensagem enviada pelo usuário
    private void sendMessage() {
        String text = editMessage.getText().toString().trim();
        if (text.isEmpty()) return; // Não envia se estiver vazio

        // Obtém o horário atual no formato HH:mm
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        
        // Adiciona a mensagem visualmente na tela (simulação de front-end)
        addMessageToUI(text, currentTime, true);

        // Limpa o campo de entrada e rola para o fim da lista
        editMessage.setText("");
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    // Cria e adiciona um "balão" de mensagem no container da tela
    private void addMessageToUI(String text, String time, boolean isMe) {
        // Escolhe o layout do balão (Direita se for "Eu", Esquerda se for "Outro")
        int layoutId = isMe ? R.layout.item_message_me : R.layout.item_message_other;
        View bubble = LayoutInflater.from(this).inflate(layoutId, llMessagesContainer, false);
        
        // Preenche o texto da mensagem e a hora
        TextView tvText = bubble.findViewById(R.id.tvMessageText);
        TextView tvTime = bubble.findViewById(R.id.tvMessageTime);
        
        tvText.setText(text);
        tvTime.setText(time);

        // Adiciona o novo balão na lista de mensagens da tela
        llMessagesContainer.addView(bubble);
    }
}