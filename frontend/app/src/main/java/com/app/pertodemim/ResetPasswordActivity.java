package com.app.pertodemim;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputLayout layoutEmailReset;
    private TextInputEditText textEmailReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        layoutEmailReset = findViewById(R.id.layoutEmailReset);
        textEmailReset = findViewById(R.id.textEmailReset);

        textEmailReset.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) layoutEmailReset.setError(null); });
        textEmailReset.setOnClickListener(v -> layoutEmailReset.setError(null));
        findViewById(R.id.tvVoltar).setOnClickListener(v -> finish());
        findViewById(R.id.btEnviarReset).setOnClickListener(v -> { if (validateFields()) startActivity(new Intent(this, VerifyCodeActivity.class)); });
    }

    private boolean validateFields() {
        layoutEmailReset.setError(null);
        String email = textEmailReset.getText() != null ? textEmailReset.getText().toString().trim() : "";
        if (TextUtils.isEmpty(email)) { layoutEmailReset.setError(getString(R.string.error_required)); return false; }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { layoutEmailReset.setError(getString(R.string.error_invalid_email)); return false; }
        return true;
    }
}
