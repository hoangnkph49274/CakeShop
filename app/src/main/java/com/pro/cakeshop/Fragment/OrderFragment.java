package com.pro.cakeshop.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.cakeshop.Adapter.CartAdapter;
import com.pro.cakeshop.Model.GioHangItem;
import com.pro.cakeshop.R;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private List<GioHangItem> cartItemList;
    private FirebaseAuth mAuth;
    private TextView tvAmount; // Change from totalAmountTextView
    private TextView checkoutButton;
    private int totalAmount = 0;
    private TextView tvAddress,tvName,tvSdt;
    private String userId;  // Cập nhật từ "0" thành "KH002" như trong ProductDetailActivity

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        recyclerView = view.findViewById(R.id.rcv_cart);
        tvAmount = view.findViewById(R.id.tv_amount); // Fix: Use correct ID
        checkoutButton = view.findViewById(R.id.tv_checkout);
        tvAddress = view.findViewById(R.id.tv_address);
        tvName = view.findViewById(R.id.tv_name);
        tvSdt = view.findViewById(R.id.tv_sdt);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cartItemList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        loadData();
        fetchCartItems();


        // Thêm sự kiện cho nút thanh toán
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartItemList.size() > 0) {
                    proceedToCheckout();
                } else {
                    Toast.makeText(getContext(), "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void loadData() {
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();

        // Add this code to fetch the address
        fetchUserAddress();
    }

    private void fetchUserAddress() {
        databaseReference.child("khachHang").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Check if this is the current user
                    String maKH = dataSnapshot.child("maKH").getValue(String.class);
                    String tenKH = dataSnapshot.child("tenKH").getValue(String.class);
                    String sdt = dataSnapshot.child("sdt").getValue(String.class);
                    if (maKH != null && maKH.equals(userId)) {
                        if (sdt != null ) {
                            tvSdt.setText(sdt);
                        }
                        if (tenKH != null ) {
                            tvName.setText(tenKH);
                        }
                        // Found the user, get the address
                        String address = dataSnapshot.child("diaChi").getValue(String.class);
                        if (address != null && !address.isEmpty()) {
                            tvAddress.setText(address);
                        } else {
                            tvAddress.setText("Chưa có địa chỉ giao hàng");
                            tvName.setText("Chưa có địa chỉ giao hàng");
                            tvSdt.setText("Chưa có địa chỉ giao hàng");
                        }
                        return;
                    }
                }
                // If we get here, the user wasn't found
                tvAddress.setText("Chưa có địa chỉ giao hàng");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OrderFragment", "Failed to read address: " + error.getMessage());
                tvAddress.setText("Chưa có địa chỉ giao hàng");
            }
        });
    }
    private void fetchCartItems() {
        databaseReference.child("gioHang").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cartItemList.clear();
                        totalAmount = 0;

                        for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                            GioHangItem item = itemSnapshot.getValue(GioHangItem.class);
                            if (item != null) {
                                cartItemList.add(item);
                                totalAmount += item.getGia() * item.getSoLuong();
                            }
                        }

                        updateUI();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("OrderFragment", "Database error: " + error.getMessage());
                        Toast.makeText(getContext(), "Lỗi kết nối cơ sở dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI() {
        // Update total amount display
        if (tvAmount != null) {
            tvAmount.setText(formatPrice(totalAmount) + " VND");
        } else {
            Log.e("OrderFragment", "tvAmount is NULL!");
        }

        // Update item count
        TextView tvCountItem = getView().findViewById(R.id.tv_count_item);
        if (tvCountItem != null) {
            tvCountItem.setText(cartItemList.size() + " sản phẩm");
        }

        // Update product price
        TextView tvPriceProduct = getView().findViewById(R.id.tv_price_product);
        if (tvPriceProduct != null) {
            tvPriceProduct.setText(formatPrice(totalAmount) + " VND");
        }

        adapter = new CartAdapter(cartItemList, getContext(), new CartAdapter.CartItemListener() {
            @Override
            public void onQuantityChanged(int position, int newQuantity) {
                updateItemQuantity(position, newQuantity);
            }

            @Override
            public void onItemRemoved(int position) {
                removeCartItem(position);
            }
        });

        recyclerView.setAdapter(adapter);

        // Hiển thị thông báo nếu giỏ hàng trống
        if (cartItemList.isEmpty()) {
            Toast.makeText(getContext(), "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateItemQuantity(int position, int newQuantity) {
        if (position >= 0 && position < cartItemList.size()) {
            GioHangItem item = cartItemList.get(position);

            // Cập nhật số lượng
            if (newQuantity <= 0) {
                // Nếu số lượng <= 0, xóa sản phẩm
                removeCartItem(position);
            } else {
                // Cập nhật số lượng mới
                int oldQuantity = item.getSoLuong();
                item.setSoLuong(newQuantity);

                // Cập nhật tổng tiền
                totalAmount = totalAmount - (item.getGia() * oldQuantity) + (item.getGia() * newQuantity);

                // Update all price displays
                updatePriceDisplays();

                // Cập nhật lên Firebase
                databaseReference.child("gioHang").child(userId).child(item.getId())
                        .child("soLuong").setValue(newQuantity);

                adapter.notifyItemChanged(position);
            }
        }
    }

    private void removeCartItem(int position) {
        if (position >= 0 && position < cartItemList.size()) {
            GioHangItem item = cartItemList.get(position);

            // Cập nhật tổng tiền
            totalAmount -= item.getGia() * item.getSoLuong();

            // Update all price displays
            updatePriceDisplays();

            // Xóa sản phẩm khỏi Firebase
            databaseReference.child("gioHang").child(userId).child(item.getId()).removeValue();

            // Xóa sản phẩm khỏi danh sách và cập nhật adapter
            cartItemList.remove(position);
            adapter.notifyItemRemoved(position);

            // Update item count
            TextView tvCountItem = getView().findViewById(R.id.tv_count_item);
            if (tvCountItem != null) {
                tvCountItem.setText(cartItemList.size() + " sản phẩm");
            }

            if (cartItemList.isEmpty()) {
                Toast.makeText(getContext(), "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updatePriceDisplays() {
        // Update the total amount display
        if (tvAmount != null) {
            tvAmount.setText(formatPrice(totalAmount) + " VND");
        }

        // Update product price
        TextView tvPriceProduct = getView().findViewById(R.id.tv_price_product);
        if (tvPriceProduct != null) {
            tvPriceProduct.setText(formatPrice(totalAmount) + " VND");
        }
    }

    // Format price with commas for thousands
    private String formatPrice(int price) {
        return String.format("%,d", price).replace(",", ".");
    }

    private void proceedToCheckout() {
        // TODO: Implement checkout functionality
        // Chuyển sang màn hình thanh toán hoặc tạo đơn hàng
        Toast.makeText(getContext(), "Chuyển đến thanh toán với " + cartItemList.size() + " sản phẩm", Toast.LENGTH_SHORT).show();

        // Ví dụ code để tạo đơn hàng mới sẽ được thêm ở đây
    }
}