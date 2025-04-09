package com.pro.cakeshop.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.pro.cakeshop.Adapter.admin.KhachHangAdapter;
import com.pro.cakeshop.Database.FirebaseHelper;
import com.pro.cakeshop.Model.KhachHang;
import com.pro.cakeshop.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerManageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private KhachHangAdapter adapter;
    private List<KhachHang> khachHangList;
    private FirebaseHelper firebaseHelper;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_manage);

        // Initialize Firebase Helper
        firebaseHelper = new FirebaseHelper();
        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Set up UI components
        setupUI();

        // Load customer data
        loadCustomers();
    }

    private void setupUI() {
        View toolbarView = findViewById(R.id.toolbar);
        // Find the back button and title within the toolbar layout if needed
        ImageView imgBack = toolbarView.findViewById(R.id.img_toolbar_back);
        TextView tvTitle = toolbarView.findViewById(R.id.tv_toolbar_title);

        if (tvTitle != null) {
            tvTitle.setText("Quản lý khách hàng");
        }

        if (imgBack != null) {
            imgBack.setOnClickListener(v -> onBackPressed());
        }

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewCustomers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize list and adapter
        khachHangList = new ArrayList<>();
        adapter = new KhachHangAdapter(khachHangList, this::confirmDeleteCustomer);
        recyclerView.setAdapter(adapter);
    }

    private void loadCustomers() {
        // Show loading indicator if needed

        firebaseHelper.getListKhachHang(new FirebaseHelper.FirebaseCallback<List<KhachHang>>() {
            @Override
            public void onSuccess(List<KhachHang> result) {
                // Hide loading indicator if needed

                khachHangList.clear();
                khachHangList.addAll(result);
                adapter.notifyDataSetChanged();

                if (result.isEmpty()) {
                    // Show empty state
                    findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    // Show list
                    findViewById(R.id.emptyView).setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(String message) {
                // Hide loading indicator if needed

                Toast.makeText(CustomerManageActivity.this,
                        "Lỗi: " + message, Toast.LENGTH_SHORT).show();

                // Show error state
                findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void confirmDeleteCustomer(KhachHang khachHang) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa khách hàng " + khachHang.getTenKH() + "? Tất cả dữ liệu liên quan như giỏ hàng, đơn hàng và tài khoản Authentication cũng sẽ bị xóa.");

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            deleteCustomer(khachHang);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteCustomer(KhachHang khachHang) {
        // Hiển thị thông báo đang xóa
        Toast.makeText(CustomerManageActivity.this,
                "Đang xóa khách hàng...", Toast.LENGTH_SHORT).show();

        String maKH = khachHang.getMaKH();
        String email = khachHang.getEmail();

        // Kiểm tra nếu email của khách hàng tồn tại
        if (email != null && !email.isEmpty()) {
            // Đăng nhập với tư cách admin để có quyền xóa user
            // Lưu ý: Đây là một cách thực hiện đơn giản, bạn cần điều chỉnh theo cơ chế xác thực của ứng dụng
            deleteUserFromAuthentication(email, maKH);
        } else {
            // Nếu không có email, chỉ xóa dữ liệu từ database
            deleteCustomerData(maKH);
        }
    }

    private void deleteUserFromAuthentication(String email, String maKH) {
        // Trong thực tế, bạn nên sử dụng Firebase Admin SDK hoặc Cloud Functions
        // để xóa người dùng từ Authentication vì hạn chế của client SDK
        // Đây là cách tiếp cận đơn giản sử dụng Firebase Admin Authentication Token

        // Sử dụng Firebase Functions hoặc API riêng của bạn để xóa user từ Authentication
        // Ví dụ: gọi Firebase Function

        // Cách tạm thời (không khuyến nghị cho production):
        // Lưu ý: Cách này chỉ hoạt động nếu bạn đang đăng nhập với tài khoản có quyền admin
        // và có thể xóa tài khoản khác

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            // Sử dụng Cloud Functions để xóa user (bạn cần tạo function này)
            // Hoặc gọi API của bạn để xóa user

            // Tạm thời xóa dữ liệu database trước
            deleteCustomerData(maKH);

            // Hiển thị thông báo
            Toast.makeText(CustomerManageActivity.this,
                    "Đã xóa dữ liệu khách hàng. Cần xóa riêng tài khoản Authentication thông qua Firebase Console hoặc Admin SDK.",
                    Toast.LENGTH_LONG).show();
        } else {
            // Nếu không có quyền xóa Authentication, vẫn xóa dữ liệu database
            deleteCustomerData(maKH);
            Toast.makeText(CustomerManageActivity.this,
                    "Không thể xóa tài khoản Authentication (cần quyền admin). Đã xóa dữ liệu khách hàng.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void deleteCustomerData(String maKH) {
        // Chuẩn bị xóa dữ liệu từ nhiều bảng
        Map<String, Object> deleteUpdates = new HashMap<>();

        // Đường dẫn đến các bảng cần xóa
        deleteUpdates.put("khachHang/" + maKH, null);          // Xóa khách hàng
        deleteUpdates.put("gioHang/" + maKH, null);            // Xóa giỏ hàng
        deleteUpdates.put("donHang/" + maKH, null);            // Xóa đơn hàng

        // Thực hiện xóa đồng thời trên nhiều bảng
        firebaseHelper.getDatabase().getReference().updateChildren(deleteUpdates)
                .addOnSuccessListener(aVoid -> {
                    // Tiếp tục xóa lịch sử mua hàng (cần truy vấn trước để lấy các mã lsmh)
                    deleteLichSuMuaHang(maKH);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CustomerManageActivity.this,
                            "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteLichSuMuaHang(String maKH) {
        // Truy vấn tất cả lịch sử mua hàng của khách hàng này
        DatabaseReference lsmhRef = firebaseHelper.getLichSuMuaHangReference();
        lsmhRef.orderByChild("maKH").equalTo(maKH).get()
                .addOnSuccessListener(dataSnapshot -> {
                    // Xóa từng mục lịch sử mua hàng
                    Map<String, Object> updates = new HashMap<>();
                    for (com.google.firebase.database.DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        if (key != null) {
                            updates.put(key, null);
                        }
                    }

                    if (!updates.isEmpty()) {
                        lsmhRef.updateChildren(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(CustomerManageActivity.this,
                                            "Đã xóa khách hàng và dữ liệu liên quan thành công", Toast.LENGTH_SHORT).show();
                                    loadCustomers(); // Tải lại danh sách khách hàng
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(CustomerManageActivity.this,
                                            "Đã xóa khách hàng nhưng có lỗi khi xóa lịch sử mua hàng", Toast.LENGTH_SHORT).show();
                                    loadCustomers(); // Vẫn tải lại danh sách
                                });
                    } else {
                        // Không có lịch sử mua hàng nào để xóa
                        Toast.makeText(CustomerManageActivity.this,
                                "Đã xóa khách hàng và dữ liệu liên quan thành công", Toast.LENGTH_SHORT).show();
                        loadCustomers(); // Tải lại danh sách khách hàng
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CustomerManageActivity.this,
                            "Đã xóa khách hàng nhưng có lỗi khi truy vấn lịch sử mua hàng", Toast.LENGTH_SHORT).show();
                    loadCustomers(); // Vẫn tải lại danh sách
                });
    }
}