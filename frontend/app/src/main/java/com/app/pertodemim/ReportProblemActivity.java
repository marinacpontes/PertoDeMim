package com.app.pertodemim;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ReportProblemActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSendReport).setOnClickListener(v -> { Toast.makeText(this, "Seu relato foi enviado com sucesso!", Toast.LENGTH_LONG).show(); finish(); });
    }
}
