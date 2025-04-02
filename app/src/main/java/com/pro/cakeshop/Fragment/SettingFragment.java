package com.pro.cakeshop.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.cakeshop.Activity.UserInfoActivity;
import com.pro.cakeshop.LoginActivity;
import com.pro.cakeshop.R;

public class SettingFragment extends Fragment {
    private static final String TAG = "SettingFragment";
    private TextView tvUsername;
    private LinearLayout layoutChangeInformation;
    private LinearLayout layoutSignOut;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        initUI(view);

        // Set click listeners
        setListeners();

        // Load user data after initializing views
        loadUserData();
    }

    private void initUI(View view) {
        tvUsername = view.findViewById(R.id.tv_username);
        layoutChangeInformation = view.findViewById(R.id.layout_change_information);
        layoutSignOut = view.findViewById(R.id.layout_sign_out);

        // Verify view was found
        if (tvUsername == null) {
            Log.e(TAG, "tv_username view not found! Check your layout file.");
            // Try alternative ID if possible
            tvUsername = view.findViewById(R.id.tv_username);
            if (tvUsername == null) {
                Log.e(TAG, "Alternative tvUsername view not found either!");
            }
        }
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && tvUsername != null) {
            String email = currentUser.getEmail();
            Log.d(TAG, "Current user email: " + email);

            if (email != null && !email.isEmpty()) {
                tvUsername.setText(email);
                Log.d(TAG, "Set username text to: " + email);
            } else {
                Log.e(TAG, "Email is null or empty");
                tvUsername.setText("Email không có sẵn");
            }

            // Also try to get user data from database to be more reliable
            String userId = currentUser.getUid();
            mDatabase.child("khachHang").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String dbEmail = snapshot.child("email").getValue(String.class);
                        Log.d(TAG, "Database email: " + dbEmail);

                        if (dbEmail != null && !dbEmail.isEmpty() && tvUsername != null) {
                            tvUsername.setText(dbEmail);
                            Log.d(TAG, "Updated username text from DB to: " + dbEmail);
                        }
                    } else {
                        Log.e(TAG, "User data not found in database");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Database error: " + error.getMessage());
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Không thể tải dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Log.e(TAG, "Current user is null or tvUsername is null");
            if (tvUsername != null) {
                tvUsername.setText("Chưa đăng nhập");
            }
        }
    }

    private void setListeners() {
        layoutChangeInformation.setOnClickListener(v -> {
            // Navigate to change information activity/fragment
            navigateToChangeInformation();
        });

        layoutSignOut.setOnClickListener(v -> {
            // Handle sign out
            signOut();
        });
    }

    private void navigateToChangeInformation() {
        // TODO: Implement navigation to user information edit screen
        // Example implementation:
        if (getContext() != null) {
            Toast.makeText(getContext(), "Đang chuyển đến trang cập nhật thông tin", Toast.LENGTH_SHORT).show();
        }
         Intent intent = new Intent(getActivity(), UserInfoActivity.class);
         startActivity(intent);
    }

    private void signOut() {
        // Sign out from Firebase Auth
        mAuth.signOut();

        // Navigate to login screen
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Show a toast message
        if (getContext() != null) {
            Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        }
    }
}