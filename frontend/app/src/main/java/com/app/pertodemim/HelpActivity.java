package com.app.pertodemim;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnFaq).setOnClickListener(v -> startActivity(new Intent(this, FaqActivity.class)));
        findViewById(R.id.btnSupport).setOnClickListener(v -> startActivity(new Intent(this, ContactSupportActivity.class)));
        findViewById(R.id.btnReportProblem).setOnClickListener(v -> startActivity(new Intent(this, ReportProblemActivity.class)));
    }
}
