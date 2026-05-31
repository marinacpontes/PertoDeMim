package com.app.pertodemim;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ManageAddressesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_addresses);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnEditAddress).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressDetailActivity.class); intent.putExtra("mode", "edit"); startActivity(intent);
        });
        findViewById(R.id.btnAddAddress).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressDetailActivity.class); intent.putExtra("mode", "add"); startActivity(intent);
        });
    }
}
