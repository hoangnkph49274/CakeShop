package com.pro.cakeshop.Activity;

import android.content.DialogInterface;
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
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.cakeshop.Database.FirebaseHelper;
import com.pro.cakeshop.Model.Banh;
import com.pro.cakeshop.R;
import com.pro.cakeshop.UserActivity;

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
    private FirebaseAuth mAuth;
    private String userId ; // Using default "0" as in database structure
    private Banh currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        firebaseHelper = new FirebaseHelper();
        mAuth = FirebaseAuth.getInstance();
        loadData();

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

    private void loadData(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();
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

        // Tham chiếu đến giỏ hàng của người dùng
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                .child("gioHang")
                .child(userId);

        // Kiểm tra xem sản phẩm đã tồn tại trong giỏ hàng chưa
        cartRef.orderByChild("maBanh").equalTo(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Sản phẩm đã tồn tại trong giỏ hàng, cập nhật số lượng
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        String itemId = itemSnapshot.getKey();
                        int currentQuantity = itemSnapshot.child("soLuong").getValue(Integer.class);

                        // Cập nhật số lượng mới
                        int newQuantity = currentQuantity + quantity;

                        // Cập nhật số lượng trong Firebase
                        cartRef.child(itemId).child("soLuong").setValue(newQuantity)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ProductDetailActivity.this,
                                                "Đã cập nhật số lượng trong giỏ hàng",
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

                        // Chỉ xử lý một mục đầu tiên tìm thấy
                        return;
                    }
                } else {
                    // Sản phẩm chưa tồn tại, thêm mới vào giỏ hàng
                    addNewItemToCart(cartRef);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDetailActivity.this,
                        "Lỗi kiểm tra giỏ hàng: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức phụ để thêm mới sản phẩm vào giỏ hàng
    private void addNewItemToCart(DatabaseReference cartRef) {
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
        Toast.makeText(ProductDetailActivity.this, "Đang chuyển đến giỏ hàng", Toast.LENGTH_SHORT).show();
        dialog();

        // For now, just finish the current activity

    }
    private void dialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Bạn có muốn tiếp tục mua hàng không?");

        builder.setPositiveButton("Tiếp tục mua", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Start UserActivity and indicate to show the Home tab
                Intent intent = new Intent(ProductDetailActivity.this, UserActivity.class);
                intent.putExtra("show_home_tab", true);
                startActivity(intent);
                finish(); // Optional: close this activity
            }
        });

        builder.setNegativeButton("Giỏ hàng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Chuyển sang UserActivity và yêu cầu hiển thị OrderFragment
                Intent intent = new Intent(ProductDetailActivity.this, UserActivity.class);
                intent.putExtra("show_order_tab", true);
                startActivity(intent);
                finish();
            }
        });


        // Hiển thị dialog
        builder.create().show();
    }
}