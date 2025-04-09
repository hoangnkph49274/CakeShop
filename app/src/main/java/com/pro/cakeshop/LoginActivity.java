package com.pro.cakeshop;

import static android.os.Build.VERSION_CODES.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout; // Added FrameLayout import
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private LinearLayout layoutRegister;
    private ProgressBar progressBar;
    private FrameLayout loadingOverlay; // Added loading overlay

    private Handler handler;
    private Runnable timeoutRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.pro.cakeshop.R.layout.activity_login);

        etEmail = findViewById(com.pro.cakeshop.R.id.etEmail);
        etPassword = findViewById(com.pro.cakeshop.R.id.etPassword);
        btnLogin = findViewById(com.pro.cakeshop.R.id.btnLogin);
        progressBar = findViewById(com.pro.cakeshop.R.id.progressBar);
        loadingOverlay = findViewById(com.pro.cakeshop.R.id.loading_overlay); // Initialize overlay
        mAuth = FirebaseAuth.getInstance();
        layoutRegister = findViewById(com.pro.cakeshop.R.id.layout_register);

        handler = new Handler();
        layoutRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        btnLogin.setOnClickListener(v -> loginUser());
    }

    // Method to show loading state
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            loadingOverlay.setVisibility(View.VISIBLE); // Show the overlay
            btnLogin.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            loadingOverlay.setVisibility(View.GONE); // Hide the overlay
            btnLogin.setEnabled(true);
        }
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        Log.d("zzzzzzzzzzzzz", "" + email + password);
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!email.endsWith("admin.com") && !email.endsWith("gmail.com")) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true); // Show loading overlay and disable interactions

        timeoutRunnable = () -> {
            showLoading(false); // Hide loading overlay and enable interactions
            Toast.makeText(LoginActivity.this, "Lỗi: Kết nối chậm, vui lòng thử lại!", Toast.LENGTH_LONG).show();
        };
        handler.postDelayed(timeoutRunnable, 8000);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    handler.removeCallbacks(timeoutRunnable);
                    showLoading(false); // Hide loading overlay and enable interactions

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkUserRole(user);
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            Toast.makeText(this, "Lỗi đăng nhập: sai tài khoản hoặc mật khẩu", Toast.LENGTH_LONG).show();
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