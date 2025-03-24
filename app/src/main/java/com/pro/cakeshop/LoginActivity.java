package com.pro.cakeshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private LinearLayout layoutRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        mAuth = FirebaseAuth.getInstance();
        layoutRegister = findViewById(R.id.layout_register);

        layoutRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });


        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkUserRole(user);
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            Toast.makeText(this, "Lỗi đăng nhập: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void checkUserRole(FirebaseUser user) {
        if (user != null) {
            String email = user.getEmail();
            if (email != null) {
                if (email.endsWith("admin.com")) {
                    Toast.makeText(this, "Chào Admin!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, AdminActivity.class));
                } else if (email.endsWith("gmail.com")) {
                    Toast.makeText(this, "Chào User!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, UserActivity.class));
                }
            }
            finish();
        }
    }
}
