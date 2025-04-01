package com.pro.cakeshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.cakeshop.Database.FirebaseHelper;
import com.pro.cakeshop.Model.Banh;
import com.pro.cakeshop.R;

import java.util.HashMap;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvProductName, tvProductDescription, tvProductPrice, tvCount, tvTotal;
    private ImageView ivProductImage;
    private TextView tvSub, tvAdd;
    private Button btnAddToCart;
    private ImageView imgBack;
    private FirebaseHelper firebaseHelper;
    private String productId;
    private int quantity = 1;
    private double price = 0;
    private String userId = "KH002"; // Using default "0" as in database structure
    private Banh currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        firebaseHelper = new FirebaseHelper();

        // Get product ID from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("product_id")) {
            productId = intent.getStringExtra("product_id");
        } else {
            productId = "0"; // Default product ID if none provided
        }

        // Initialize views
        tvProductName = findViewById(R.id.tv_name);
        tvProductDescription = findViewById(R.id.tv_description);
        tvProductPrice = findViewById(R.id.tv_price_sale);
        ivProductImage = findViewById(R.id.img_product);
        tvCount = findViewById(R.id.tv_count);
        tvTotal = findViewById(R.id.tv_total);
        tvSub = findViewById(R.id.tv_sub);
        tvAdd = findViewById(R.id.tv_add);
        TextView tvAddOrder = findViewById(R.id.tv_add_order);
        imgBack = findViewById(R.id.img_toolbar_back);

        // Set back button click listener
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (productId != null && !productId.isEmpty()) {
            fetchProductDetails(productId);
        } else {
            tvProductName.setText("Lỗi: Không tìm thấy ID sản phẩm");
        }

        // Set quantity change listeners
        tvSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    quantity--;
                    updateQuantityAndTotal();
                }
            }
        });

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                updateQuantityAndTotal();
            }
        });

        // Set add to cart button click listener
        tvAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
    }

    private void fetchProductDetails(String productId) {
        DatabaseReference banhRef = FirebaseDatabase.getInstance().getReference().child("banh").child(productId);

        banhRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentProduct = snapshot.getValue(Banh.class);
                    Log.d("FirebaseData", "Dữ liệu sản phẩm: " + snapshot.getValue());

                    if (currentProduct != null) {
                        tvProductName.setText(currentProduct.getTenBanh());
                        tvProductDescription.setText(currentProduct.getMoTa());
                        price = currentProduct.getGia();
                        tvProductPrice.setText(String.format("%,.0f VNĐ", price));
                        updateQuantityAndTotal();

                        if (currentProduct.getHinhAnh() != null && !currentProduct.getHinhAnh().isEmpty()) {
                            Glide.with(ProductDetailActivity.this)
                                    .load(currentProduct.getHinhAnh())
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
                tvProductName.setText("Lỗi: " + error.getMessage());
            }
        });
    }

    private void updateQuantityAndTotal() {
        tvCount.setText(String.valueOf(quantity));
        double totalPrice = quantity * price;
        tvTotal.setText(String.format("%,.0f VNĐ", totalPrice));
    }

    private void addToCart() {
        if (productId == null || productId.isEmpty() || currentProduct == null) {
            Toast.makeText(this, "Không thể thêm sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique ID for the cart item
        String cartItemId = FirebaseDatabase.getInstance().getReference().push().getKey();

        if (cartItemId != null) {
            // Create cart item data structure matching the database
            Map<String, Object> cartItem = new HashMap<>();
            cartItem.put("id", cartItemId);
            cartItem.put("maBanh", productId);
            cartItem.put("gia", currentProduct.getGia());
            cartItem.put("tenBanh", currentProduct.getTenBanh());
            cartItem.put("hinhAnh", currentProduct.getHinhAnh());
            cartItem.put("soLuong", quantity);

            // Add to Firebase under the correct path: gioHang/userId/cartItemId
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                    .child("gioHang")
                    .child(userId);

            cartRef.child(cartItemId).setValue(cartItem)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProductDetailActivity.this,
                                    "Đã thêm vào giỏ hàng",
                                    Toast.LENGTH_SHORT).show();
                            navigateToCart();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProductDetailActivity.this,
                                    "Lỗi: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void navigateToCart() {
        // Uncomment this when you have a CartActivity
         Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
         startActivity(intent);

        // For now, just finish the current activity

    }
}