package com.pro.cakeshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pro.cakeshop.Model.KhachHang;

import java.util.HashMap;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private LinearLayout layoutLogin;
    private FrameLayout loadingOverlay;
    private ProgressBar progressBar;
    private Handler handler;
    private Runnable timeoutRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        handler = new Handler();

        // Initialize UI components
        etEmail = findViewById(R.id.etMail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        layoutLogin = findViewById(R.id.layout_login);
        loadingOverlay = findViewById(R.id.loading_overlay);
        progressBar = findViewById(R.id.progressBar);

        layoutLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            loadingOverlay.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            btnRegister.setEnabled(false);
        } else {
            loadingOverlay.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            btnRegister.setEnabled(true);
        }
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        } else if (email.endsWith("admin.com")) {
            Toast.makeText(this, "Khong the tao tai khoan voi duoi admin.com", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading and disable interactions
        showLoading(true);

        // Add timeout for slow connections
        timeoutRunnable = () -> {
            showLoading(false);
            Toast.makeText(RegisterActivity.this, "Lỗi: Kết nối chậm, vui lòng thử lại!", Toast.LENGTH_LONG).show();
        };
        handler.postDelayed(timeoutRunnable, 8000); // 8 second timeout

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    // Remove the timeout callback
                    handler.removeCallbacks(timeoutRunnable);

                    // Hide loading and enable interactions
                    showLoading(false);

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Add user to khachHang table
                        if (user != null) {
                            String userId = user.getUid();
                            addUserToKhachHang(userId, email);
                        }

                        Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addUserToKhachHang(String userId, String email) {
        // Create KhachHang object with empty fields as required
        KhachHang khachHang = new KhachHang();
        khachHang.setMaKH(userId);
        khachHang.setEmail(email);
        khachHang.setTenKH(""); // Empty as required
        khachHang.setDiaChi(""); // Empty as required
        khachHang.setSdt(""); // Empty as required

        // Add to Firebase
        mDatabase.child("khachHang").child(userId).setValue(khachHang)
                .addOnSuccessListener(aVoid -> {
                    // Successfully added to the database
                    // We can leave this empty since we already show a toast for successful registration
                })
                .addOnFailureListener(e -> {
                    // If there's a specific need to handle failure to add to khachHang table,
                    // we could add a separate toast here, but it's optional
                });
    }

    // The createEmptyShoppingCart method is commented out in the original code, so I'm keeping it commented
}