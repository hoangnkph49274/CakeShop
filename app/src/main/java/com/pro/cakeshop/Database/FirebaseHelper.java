package com.pro.cakeshop.Database;

import android.util.Log;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.cakeshop.Model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {

    private final FirebaseDatabase database;

    public FirebaseHelper() {
        this.database = FirebaseDatabase.getInstance();
    }
    public FirebaseDatabase getDatabase() {
        return database;
    }
    public interface FirebaseCallback<T> {
        void onSuccess(T result);
        void onFailure(String message); // Thêm phương thức này
    }


    // Tham chiếu đến bảng Khách Hàng
    public DatabaseReference getKhachHangReference() {
        return database.getReference("khachHang");
    }

    // Tham chiếu đến bảng Lịch Sử Mua Hàng
    public DatabaseReference getLichSuMuaHangReference() {
        return database.getReference("lichSuMuaHang");
    }

    // Tham chiếu đến bảng Đơn Hàng
    public DatabaseReference getDonHangReference() {
        return database.getReference("donHang");
    }

    // Tham chiếu đến bảng Giỏ Hàng
    public DatabaseReference getGioHangReference() {
        return database.getReference("gioHang");
    }

    // Tham chiếu đến bảng Bánh
    public DatabaseReference getBanhReference() {
        return database.getReference("banh");
    }

    // Tham chiếu đến bảng Loại
    public DatabaseReference getLoaiReference() {
        return database.getReference("loai");
    }

    // Lấy danh sách bánh
    public void getListBanh(FirebaseCallback<List<Banh>> callback) {
        getBanhReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Banh> listBanh = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Banh banh = dataSnapshot.getValue(Banh.class);
                    if (banh != null) {
                        listBanh.add(banh);
                    }
                }
                callback.onSuccess(listBanh);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }
    public void getBanhByMa(String maBanh, FirebaseCallback<Banh> callback) {
        if (maBanh == null || maBanh.isEmpty()) {
            callback.onFailure("Mã bánh trống");
            return;
        }

        Log.d("FirebaseHelper", "Getting bánh with mã: " + maBanh);

        // Replace banhReference with getBanhReference()
        getBanhReference().child(maBanh).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Banh banh = snapshot.getValue(Banh.class);
                    if (banh != null) {
                        // Make sure the maBanh is set in case it's not included in the Firebase data
                        banh.setMaBanh(snapshot.getKey());
                        Log.d("FirebaseHelper", "Found bánh: " + banh.getTenBanh());
                        callback.onSuccess(banh);
                    } else {
                        Log.e("FirebaseHelper", "Could not convert snapshot to Banh object");
                        callback.onFailure("Lỗi chuyển đổi dữ liệu");
                    }
                } else {
                    Log.e("FirebaseHelper", "No data found for maBanh: " + maBanh);
                    callback.onFailure("Không tìm thấy bánh");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseHelper", "Database error: " + error.getMessage());
                callback.onFailure("Lỗi cơ sở dữ liệu: " + error.getMessage());
            }
        });
    }
    // Lấy danh sách khách hàng
    public void getListKhachHang(FirebaseCallback<List<KhachHang>> callback) {
        getKhachHangReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<KhachHang> listKhachHang = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    KhachHang khachHang = dataSnapshot.getValue(KhachHang.class);
                    if (khachHang != null) {
                        listKhachHang.add(khachHang);
                    }
                }
                callback.onSuccess(listKhachHang);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    // Lấy danh sách loại bánh
    public void getListLoai(FirebaseCallback<List<Loai>> callback) {
        getLoaiReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Loai> listLoai = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Loai loai = dataSnapshot.getValue(Loai.class);
                    if (loai != null) {
                        listLoai.add(loai);
                    }
                }
                callback.onSuccess(listLoai);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    // Lấy danh sách đơn hàng cho một khách hàng cụ thể
    public void getListDonHangByKhachHang(String maKH, FirebaseCallback<List<DonHang>> callback) {
        getDonHangReference().child(maKH).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DonHang> listDonHang = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DonHang donHang = dataSnapshot.getValue(DonHang.class);
                    if (donHang != null) {
                        // Đảm bảo đơn hàng có mã đơn hàng
                        donHang.setMaDonHang(dataSnapshot.getKey());
                        // Đảm bảo đơn hàng có mã khách hàng
                        donHang.setMaKH(maKH);

                        // Chi tiết đơn hàng đã được Firebase tự động mapping dựa trên cấu trúc JSON
                        listDonHang.add(donHang);
                    }
                }
                callback.onSuccess(listDonHang);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    // Lấy tất cả đơn hàng từ tất cả khách hàng (cho admin)
    public void getAllDonHang(FirebaseCallback<List<DonHang>> callback) {
        getDonHangReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DonHang> allDonHang = new ArrayList<>();

                // Đếm số khách hàng cần duyệt qua
                long customerCount = snapshot.getChildrenCount();
                if (customerCount == 0) {
                    callback.onSuccess(allDonHang);
                    return;
                }

                final long[] processedCustomers = {0};

                // Duyệt qua từng khách hàng
                for (DataSnapshot customerSnapshot : snapshot.getChildren()) {
                    String maKH = customerSnapshot.getKey();

                    // Duyệt qua đơn hàng của khách hàng này
                    for (DataSnapshot orderSnapshot : customerSnapshot.getChildren()) {
                        DonHang donHang = orderSnapshot.getValue(DonHang.class);
                        if (donHang != null) {
                            donHang.setMaDonHang(orderSnapshot.getKey());
                            donHang.setMaKH(maKH);
                            allDonHang.add(donHang);
                        }
                    }

                    // Tăng số khách hàng đã xử lý
                    processedCustomers[0]++;

                    // Nếu đã xử lý tất cả khách hàng, trả về kết quả
                    if (processedCustomers[0] == customerCount) {
                        callback.onSuccess(allDonHang);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    // Lấy giỏ hàng của khách hàng
    public void getGioHangByKhachHang(String maKH, FirebaseCallback<List<GioHangItem>> callback) {
        getGioHangReference().child(maKH).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<GioHangItem> listItems = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    GioHangItem item = dataSnapshot.getValue(GioHangItem.class);
                    if (item != null) {
                        listItems.add(item);
                    }
                }
                callback.onSuccess(listItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    // Thêm bánh
    public void addBanh(Banh banh, FirebaseCallback<Void> callback) {
        // Tạo key mới
        DatabaseReference newBanhRef = getBanhReference().push();

        // Đặt mã bánh bằng key vừa tạo
        banh.setMaBanh(newBanhRef.getKey());

        // Thêm bánh vào Firebase
        newBanhRef.setValue(banh)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    // Cập nhật tên bánh
    public void updateBanh(String maBanh, String newName, FirebaseCallback<Void> callback) {
        // Tạo map chứa các giá trị cần cập nhật
        Map<String, Object> updates = new HashMap<>();
        updates.put("tenBanh", newName);

        // Thực hiện cập nhật
        getBanhReference().child(maBanh).updateChildren(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    // Xóa bánh
    public void deleteBanh(String maBanh, FirebaseCallback<Void> callback) {
        getBanhReference().child(maBanh).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    // Cập nhật toàn bộ thông tin bánh
    public void updateBanhFull(Banh banh, FirebaseCallback<Void> callback) {
        // Tạo map chứa các giá trị cần cập nhật
        Map<String, Object> updates = new HashMap<>();
        updates.put("tenBanh", banh.getTenBanh());
        updates.put("gia", banh.getGia());
        updates.put("hinhAnh", banh.getHinhAnh());
        updates.put("maLoai", banh.getMaLoai());
        updates.put("moTa", banh.getMoTa());

        // Thực hiện cập nhật
        getBanhReference().child(banh.getMaBanh()).updateChildren(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    // Thêm item vào giỏ hàng


    // Xóa item khỏi giỏ hàng
    public void removeFromCart(String maKH, String itemId, FirebaseCallback<Void> callback) {
        getGioHangReference().child(maKH).child(itemId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    public void updateCartItemQuantity(String maKH, String itemId, int newQuantity, FirebaseCallback<Void> callback) {
        getGioHangReference().child(maKH).child(itemId).child("soLuong")
                .setValue(newQuantity)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    // Tạo đơn hàng mới
    public void createOrder(String maKH, DonHang donHang, List<DonHangChiTiet> chiTietList, FirebaseCallback<String> callback) {
        // Tạo key mới cho đơn hàng nếu chưa có
        if (donHang.getMaDonHang() == null || donHang.getMaDonHang().isEmpty()) {
            String orderId = getDonHangReference().child(maKH).push().getKey();
            donHang.setMaDonHang(orderId);
        }

        // Đảm bảo đơn hàng có mã khách hàng
        donHang.setMaKH(maKH);

        // Thiết lập chi tiết đơn hàng trong đơn hàng
        donHang.setDonHangChiTiet(chiTietList);

        // Thêm đơn hàng vào database theo cấu trúc nested
        getDonHangReference().child(maKH).child(donHang.getMaDonHang()).setValue(donHang)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Tạo lịch sử mua hàng
                        String lsmhId = getLichSuMuaHangReference().push().getKey();
                        LichSuMuaHang lichSu = new LichSuMuaHang(lsmhId, maKH,
                                donHang.getMaDonHang(), String.valueOf(donHang.getNgayDat()));

                        getLichSuMuaHangReference().child(lsmhId).setValue(lichSu)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Xóa giỏ hàng sau khi đặt hàng thành công
                                        getGioHangReference().child(maKH).removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        callback.onSuccess(donHang.getMaDonHang());
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Vẫn gọi callback thành công vì đơn hàng đã được tạo
                                                        callback.onSuccess(donHang.getMaDonHang());
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        callback.onFailure(e.getMessage());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    // Cập nhật trạng thái đơn hàng
    public void updateOrderStatus(String maKH, String maDonHang, String trangThai, FirebaseCallback<Void> callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("trangThai", trangThai);

        getDonHangReference().child(maKH).child(maDonHang).updateChildren(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }


}