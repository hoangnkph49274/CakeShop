package com.pro.cakeshop.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.pro.cakeshop.Fragment.OrderFragment;
import com.pro.cakeshop.Model.Banh;
import com.pro.cakeshop.R;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvProductName, tvProductDescription, tvProductPrice, tvCount, tvTotal;
    private ImageView ivProductImage;
    private TextView tvSub, tvAdd;
    private FirebaseHelper firebaseHelper;
    private String productId;
    private int quantity = 1;
    private double price = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        firebaseHelper = new FirebaseHelper();
        productId = "1";

        tvProductName = findViewById(R.id.tv_name);
        tvProductDescription = findViewById(R.id.tv_description);
        tvProductPrice = findViewById(R.id.tv_price_sale);
        ivProductImage = findViewById(R.id.img_product);
        tvCount = findViewById(R.id.tv_count);
        tvTotal = findViewById(R.id.tv_total);
        tvSub = findViewById(R.id.tv_sub);
        tvAdd = findViewById(R.id.tv_add);

        if (productId != null && !productId.isEmpty()) {
            fetchProductDetails(productId);
        } else {
            tvProductName.setText("Lỗi: Không tìm thấy ID sản phẩm");
        }

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
    }

    private void fetchProductDetails(String productId) {
        DatabaseReference banhRef = firebaseHelper.getBanhReference().child(productId);

        banhRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Banh banh = snapshot.getValue(Banh.class);
                    Log.d("FirebaseData", "Dữ liệu sản phẩm: " + snapshot.getValue());

                    if (banh != null) {
                        tvProductName.setText(banh.getTenBanh());
                        tvProductDescription.setText(banh.getMoTa());
                        price = banh.getGia();
                        tvProductPrice.setText(String.format("%,.0f VNĐ", price));
                        updateQuantityAndTotal();

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
                tvProductName.setText("Lỗi: " + error.getMessage());
            }
        });
    }

    private void updateQuantityAndTotal() {
        tvCount.setText(String.valueOf(quantity));
        double totalPrice = quantity * price;
        tvTotal.setText(String.format("%,.0f VNĐ", totalPrice));
    }

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
         Intent intent = new Intent(ProductDetailActivity.this, OrderFragment.class);
         startActivity(intent);

        // For now, just finish the current activity

    }
}

