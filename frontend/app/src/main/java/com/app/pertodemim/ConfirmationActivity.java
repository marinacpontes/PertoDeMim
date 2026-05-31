package com.app.pertodemim;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        loadOrderData();
        findViewById(R.id.btnBackHome).setOnClickListener(v -> goHome());
        new Handler(Looper.getMainLooper()).postDelayed(this::goHome, 5000);
    }

    private void loadOrderData() {
        Intent intent = getIntent();
        String provider = intent.getStringExtra("providerName"), summary = intent.getStringExtra("serviceSummary"), price = intent.getStringExtra("totalPrice"), date = intent.getStringExtra("date"), time = intent.getStringExtra("time");
        if (provider != null) ((TextView) findViewById(R.id.tvConfProviderName)).setText(provider);
        if (summary != null) ((TextView) findViewById(R.id.tvConfServiceName)).setText(summary);
        if (price != null) ((TextView) findViewById(R.id.tvConfPrice)).setText(price);
        if (date != null && time != null) ((TextView) findViewById(R.id.tvConfDateTime)).setText(date + " às " + time);
    }

    private void goHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
