package com.app.pertodemim;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class FaqActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
