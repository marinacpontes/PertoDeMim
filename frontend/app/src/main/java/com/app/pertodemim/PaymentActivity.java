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
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvDate, tvTime;
    private View layoutCard, layoutPix, layoutCash;
    private ImageView ivCheckCard, ivCheckPix, ivCheckCash;
    private LinearLayout llItemsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        initViews();
        loadServiceData();
        setupDateTimePickers();
        setupPaymentSelection();
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            Intent confirmIntent = new Intent(this, ConfirmationActivity.class);
            confirmIntent.putExtra("providerName", ((TextView)findViewById(R.id.tvPaymentProviderName)).getText().toString());
            ArrayList<String> serviceNames = getIntent().getStringArrayListExtra("serviceNames");
            if (serviceNames == null) confirmIntent.putExtra("serviceSummary", getIntent().getStringExtra("serviceName"));
            else confirmIntent.putExtra("serviceSummary", String.join(", ", serviceNames));
            confirmIntent.putExtra("totalPrice", ((TextView)findViewById(R.id.tvPaymentTotal)).getText().toString());
            confirmIntent.putExtra("date", tvDate.getText().toString());
            confirmIntent.putExtra("time", tvTime.getText().toString());
            startActivity(confirmIntent);
        });
    }

    private void loadServiceData() {
        Intent intent = getIntent();
        ArrayList<String> names = intent.getStringArrayListExtra("serviceNames"), prices = intent.getStringArrayListExtra("servicePrices"), durations = intent.getStringArrayListExtra("serviceDurations");
        String providerName = intent.getStringExtra("providerName"), category = intent.getStringExtra("category"), address = intent.getStringExtra("address");

        if (names == null && intent.getStringExtra("serviceName") != null) {
            names = new ArrayList<>(); names.add(intent.getStringExtra("serviceName"));
            prices = new ArrayList<>(); prices.add(intent.getStringExtra("price"));
            durations = new ArrayList<>(); durations.add(intent.getStringExtra("duration"));
        }
        if (providerName != null) { ((TextView)findViewById(R.id.tvPaymentProviderName)).setText(providerName); setProviderLogo(providerName); }
        if (category != null) ((TextView)findViewById(R.id.tvPaymentCategory)).setText(category);
        if (address != null) ((TextView)findViewById(R.id.tvPaymentAddress)).setText(address);

        double totalValue = 0; llItemsContainer.removeAllViews();
        if (names != null && prices != null) {
            for (int i = 0; i < names.size(); i++) { addItemToUI(names.get(i), prices.get(i)); totalValue += parsePrice(prices.get(i)); }
        }
        String formattedTotal = String.format(Locale.getDefault(), "R$ %.2f", totalValue);
        ((TextView)findViewById(R.id.tvPaymentPrice)).setText(formattedTotal);
        ((TextView)findViewById(R.id.tvPaymentSubtotal)).setText(formattedTotal);
        ((TextView)findViewById(R.id.tvPaymentTotal)).setText(formattedTotal);
        if (durations != null && !durations.isEmpty()) ((TextView)findViewById(R.id.tvPaymentDuration)).setText(String.join(" + ", durations));
    }

    private void setProviderLogo(String name) {
        ImageView ivLogo = findViewById(R.id.ivPaymentProviderLogo);
        if (ivLogo == null) return;
        switch (name) {
            case "Salão Bela Forma": ivLogo.setImageResource(R.drawable.logobelaforma); break;
            case "TechFix Consertos": ivLogo.setImageResource(R.drawable.logotechfix); break;
            case "Pizzaria Napolitana": ivLogo.setImageResource(R.drawable.logopizzarianapolitana); break;
            case "Academia FitLife": ivLogo.setImageResource(R.drawable.logofitlife); break;
            case "Auto Mecânica Silva": ivLogo.setImageResource(R.drawable.logoautomecanicasilva); break;
            case "Escola de Idiomas Global": ivLogo.setImageResource(R.drawable.logoescoladeidiomasglobal); break;
        }
    }

    private void addItemToUI(String name, String price) {
        TextView tv = new TextView(this); tv.setText(name + " - " + price);
        tv.setTextColor(ContextCompat.getColor(this, R.color.verde_petroleo_profundo));
        tv.setPadding(0, 8, 0, 8); llItemsContainer.addView(tv);
    }

    private double parsePrice(String priceText) {
        try { return Double.parseDouble(priceText.replaceAll("[^0-9,]", "").replace(",", ".")); } catch (Exception e) { return 0; }
    }

    private void initViews() {
        tvDate = findViewById(R.id.tvPaymentDate); tvTime = findViewById(R.id.tvPaymentTime); llItemsContainer = findViewById(R.id.llPaymentItemsContainer);
        layoutCard = findViewById(R.id.layoutPaymentCard); layoutPix = findViewById(R.id.layoutPaymentPix); layoutCash = findViewById(R.id.layoutPaymentCash);
        ivCheckCard = findViewById(R.id.ivCheckCard); ivCheckPix = findViewById(R.id.ivCheckPix); ivCheckCash = findViewById(R.id.ivCheckCash);
    }

    private void setupDateTimePickers() {
        tvDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> tvDate.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year)), c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
        tvTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, hourOfDay, minute) -> tvTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });
    }

    private void setupPaymentSelection() {
        layoutCard.setOnClickListener(v -> selectPayment("card")); layoutPix.setOnClickListener(v -> selectPayment("pix")); layoutCash.setOnClickListener(v -> selectPayment("cash"));
    }

    private void selectPayment(String method) {
        layoutCard.setBackgroundResource(R.drawable.bg_payment_normal); layoutPix.setBackgroundResource(R.drawable.bg_payment_normal); layoutCash.setBackgroundResource(R.drawable.bg_payment_normal);
        ivCheckCard.setVisibility(View.GONE); ivCheckPix.setVisibility(View.GONE); ivCheckCash.setVisibility(View.GONE);
        switch (method) {
            case "card": layoutCard.setBackgroundResource(R.drawable.bg_payment_selected); ivCheckCard.setVisibility(View.VISIBLE); break;
            case "pix": layoutPix.setBackgroundResource(R.drawable.bg_payment_selected); ivCheckPix.setVisibility(View.VISIBLE); break;
            case "cash": layoutCash.setBackgroundResource(R.drawable.bg_payment_selected); ivCheckCash.setVisibility(View.VISIBLE); break;
        }
    }
}
