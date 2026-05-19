package com.app.pertodemim;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

// Tela para finalizar o agendamento e pagamento
public class PaymentActivity extends AppCompatActivity {

    private TextView tvDate, tvTime;
    private View layoutCard, layoutPix, layoutCash;
    private ImageView ivCheckCard, ivCheckPix, ivCheckCash;
    private LinearLayout llItemsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Define o layout da tela de agendamento e pagamento
        setContentView(R.layout.activity_payment);

        initViews(); // Liga as variáveis aos IDs do XML
        loadServiceData(); // Carrega os dados dinâmicos dos serviços selecionados
        setupDateTimePickers(); // Configura os seletores de data e hora
        setupPaymentSelection(); // Configura a lógica de escolha da forma de pagamento

        // Botão para voltar à tela anterior
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Botão para confirmar agendamento: abre a tela de confirmação de sucesso
        findViewById(R.id.btnConfirm).setOnClickListener(v -> startActivity(new Intent(this, ConfirmationActivity.class)));
    }

    // Carrega os dados enviados pelas telas anteriores
    private void loadServiceData() {
        Intent intent = getIntent();
        ArrayList<String> names = intent.getStringArrayListExtra("serviceNames");
        ArrayList<String> prices = intent.getStringArrayListExtra("servicePrices");
        ArrayList<String> durations = intent.getStringArrayListExtra("serviceDurations");
        
        String providerName = intent.getStringExtra("providerName");
        String category = intent.getStringExtra("category");
        String address = intent.getStringExtra("address");

        // Se veio apenas um serviço da Vitrine (formato antigo para compatibilidade)
        if (names == null && intent.getStringExtra("serviceName") != null) {
            names = new ArrayList<>();
            names.add(intent.getStringExtra("serviceName"));
            prices = new ArrayList<>();
            prices.add(intent.getStringExtra("price"));
            durations = new ArrayList<>();
            durations.add(intent.getStringExtra("duration"));
        }

        if (providerName != null) ((TextView)findViewById(R.id.tvPaymentProviderName)).setText(providerName);
        if (category != null) ((TextView)findViewById(R.id.tvPaymentCategory)).setText(category);
        if (address != null) ((TextView)findViewById(R.id.tvPaymentAddress)).setText(address);

        double totalValue = 0;
        llItemsContainer.removeAllViews();

        if (names != null && prices != null) {
            for (int i = 0; i < names.size(); i++) {
                addItemToUI(names.get(i), prices.get(i));
                totalValue += parsePrice(prices.get(i));
            }
        }

        String formattedTotal = String.format(Locale.getDefault(), "R$ %.2f", totalValue);
        ((TextView)findViewById(R.id.tvPaymentPrice)).setText(formattedTotal);
        ((TextView)findViewById(R.id.tvPaymentSubtotal)).setText(formattedTotal);
        ((TextView)findViewById(R.id.tvPaymentTotal)).setText(formattedTotal);
        
        // Exibe durações combinadas (simplificado)
        if (durations != null && !durations.isEmpty()) {
            ((TextView)findViewById(R.id.tvPaymentDuration)).setText(String.join(" + ", durations));
        }
    }

    // Adiciona uma linha de serviço na interface
    private void addItemToUI(String name, String price) {
        TextView tv = new TextView(this);
        String text = name + " - " + price;
        tv.setText(text);
        tv.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.verde_petroleo_profundo));
        tv.setPadding(0, 8, 0, 8);
        llItemsContainer.addView(tv);
    }

    // Converte o texto "R$ 80,00" para o número 80.0
    private double parsePrice(String priceText) {
        try {
            String clean = priceText.replaceAll("[^0-9,]", "").replace(",", ".");
            return Double.parseDouble(clean);
        } catch (Exception e) { return 0; }
    }

    // Inicializa os componentes da interface
    private void initViews() {
        tvDate = findViewById(R.id.tvPaymentDate);
        tvTime = findViewById(R.id.tvPaymentTime);
        llItemsContainer = findViewById(R.id.llPaymentItemsContainer);
        
        layoutCard = findViewById(R.id.layoutPaymentCard);
        layoutPix = findViewById(R.id.layoutPaymentPix);
        layoutCash = findViewById(R.id.layoutPaymentCash);
        
        ivCheckCard = findViewById(R.id.ivCheckCard);
        ivCheckPix = findViewById(R.id.ivCheckPix);
        ivCheckCash = findViewById(R.id.ivCheckCash);
    }

    // Configura os diálogos de Calendário e Relógio
    private void setupDateTimePickers() {
        tvDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                tvDate.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year));
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        tvTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                tvTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });
    }

    private void setupPaymentSelection() {
        layoutCard.setOnClickListener(v -> selectPayment("card"));
        layoutPix.setOnClickListener(v -> selectPayment("pix"));
        layoutCash.setOnClickListener(v -> selectPayment("cash"));
    }

    private void selectPayment(String method) {
        layoutCard.setBackgroundResource(R.drawable.bg_payment_normal);
        layoutPix.setBackgroundResource(R.drawable.bg_payment_normal);
        layoutCash.setBackgroundResource(R.drawable.bg_payment_normal);
        
        ivCheckCard.setVisibility(View.GONE);
        ivCheckPix.setVisibility(View.GONE);
        ivCheckCash.setVisibility(View.GONE);

        switch (method) {
            case "card":
                layoutCard.setBackgroundResource(R.drawable.bg_payment_selected);
                ivCheckCard.setVisibility(View.VISIBLE);
                break;
            case "pix":
                layoutPix.setBackgroundResource(R.drawable.bg_payment_selected);
                ivCheckPix.setVisibility(View.VISIBLE);
                break;
            case "cash":
                layoutCash.setBackgroundResource(R.drawable.bg_payment_selected);
                ivCheckCash.setVisibility(View.VISIBLE);
                break;
        }
    }
}