package com.pro.cakeshop.activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pro.cakeshop.Database.FirebaseHelper;
import com.pro.cakeshop.Model.Banh;
import com.pro.cakeshop.R;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvProductName;
    private TextView tvProductDescription;
    private TextView tvProductPrice;
    private ImageView ivProductImage;

    private FirebaseHelper firebaseHelper;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail); // Giả sử bạn có tệp bố cục tên là activity_product_detail.xml

        // Khởi tạo FirebaseHelper
        firebaseHelper = new FirebaseHelper();

        // Lấy ID sản phẩm từ Intent
//        productId = getIntent().getStringExtra("productId");
        productId = "B002"; // Gán giá trị ID cứng

        // Khởi tạo các thành phần giao diện người dùng
        tvProductName = findViewById(R.id.tv_name);
        tvProductDescription = findViewById(R.id.tv_description);
        tvProductPrice = findViewById(R.id.tv_price_sale);
        ivProductImage = findViewById(R.id.img_product);

        // Kiểm tra xem ID sản phẩm có khác null và không rỗng không
        if (productId != null && !productId.isEmpty()) {
            // Lấy chi tiết sản phẩm từ Firebase
            fetchProductDetails(productId);
        } else {
            // Xử lý trường hợp ID sản phẩm không được cung cấp
            tvProductName.setText("Lỗi: Không tìm thấy ID sản phẩm");
            // Bạn có thể muốn kết thúc hoạt động hoặc hiển thị thông báo lỗi
        }
    }

    private void fetchProductDetails(String productId) {
        DatabaseReference banhRef = firebaseHelper.getBanhReference().child(productId);

        banhRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Banh banh = snapshot.getValue(Banh.class);
                    if (banh != null) {
                        // Cập nhật giao diện người dùng với chi tiết sản phẩm
                        tvProductName.setText(banh.getTenBanh());
                        tvProductDescription.setText(banh.getMoTa());
                        tvProductPrice.setText(String.format("%,.0f VNĐ", banh.getGia())); // Định dạng giá
                        if (banh.getHinhAnh() != null && !banh.getHinhAnh().isEmpty()) {
                            Glide.with(ProductDetailActivity.this)
                                    .load(banh.getHinhAnh())

                                    .into(ivProductImage);
                        }
                    } else {
                        tvProductName.setText("Lỗi: Không thể truy xuất dữ liệu sản phẩm");
                    }
                } else {
                    tvProductName.setText("Sản phẩm không tồn tại");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvProductName.setText("Lỗi: Không thể tải dữ liệu sản phẩm: " + error.getMessage());
                // Ghi log lỗi để gỡ lỗi
                // Log.e("ProductDetail", "Failed to load product", error.toException());
            }
        });
    }
}