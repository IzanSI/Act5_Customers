package com.example.act5_customers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.act5_customers.OdooLoginService;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvResult;
    private final String DB_NAME = "bitnami_odoo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvResult = findViewById(R.id.tvResult);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            OdooLoginService loginService = new OdooLoginService("http://192.168.231.253", DB_NAME);

            loginService.login(username, password,
                    uid -> {
                        if (uid > 0) {
                            Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                            intent.putExtra("uid", uid);
                            intent.putExtra("username", username);
                            intent.putExtra("password", password);
                            intent.putExtra("db", DB_NAME);
                            startActivity(intent);
                            finish();;
                        } else {
                            tvResult.setText("Credenciales incorrectas.");
                        }
                    },
                    errorMsg -> tvResult.setText("Error: " + errorMsg)
            );
        });
    }
}

