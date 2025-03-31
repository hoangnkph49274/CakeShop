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

    // Tham chiếu đến bảng Đơn Hàng Chi Tiết
    public DatabaseReference getDonHangChiTietReference() {
        return database.getReference("donHangChiTiet");
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

    // Lấy danh sách đơn hàng
    public void getListDonHang(FirebaseCallback<List<DonHang>> callback) {
        getDonHangReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DonHang> listDonHang = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DonHang donHang = dataSnapshot.getValue(DonHang.class);
                    if (donHang != null) {
                        // Lấy chi tiết đơn hàng
                        getDonHangChiTiet(donHang.getMaDonHang(), new FirebaseCallback<List<DonHangChiTiet>>() {
                            @Override
                            public void onSuccess(List<DonHangChiTiet> result) {
                                if (result != null) {
                                    donHang.setDonHangChiTiet(result);
                                }
                                listDonHang.add(donHang);

                                // Nếu đã xử lý tất cả đơn hàng, gọi callback
                                if (listDonHang.size() == snapshot.getChildrenCount()) {
                                    callback.onSuccess(listDonHang);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                // Vẫn thêm đơn hàng mặc dù không có chi tiết
                                listDonHang.add(donHang);

                                // Nếu đã xử lý tất cả đơn hàng, gọi callback
                                if (listDonHang.size() == snapshot.getChildrenCount()) {
                                    callback.onSuccess(listDonHang);
                                }
                            }
                        });
                    }
                }

                // Nếu không có đơn hàng, gọi callback ngay
                if (snapshot.getChildrenCount() == 0) {
                    callback.onSuccess(listDonHang);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    // Lấy chi tiết đơn hàng theo mã đơn hàng
    public void getDonHangChiTiet(String maDonHang, FirebaseCallback<List<DonHangChiTiet>> callback) {
        getDonHangChiTietReference().child(maDonHang).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DonHangChiTiet> listChiTiet = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DonHangChiTiet chiTiet = dataSnapshot.getValue(DonHangChiTiet.class);
                    if (chiTiet != null) {
                        listChiTiet.add(chiTiet);
                    }
                }
                callback.onSuccess(listChiTiet);
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
    public void addToCart(String maKH, GioHangItem item, FirebaseCallback<Void> callback) {
        // Tạo key mới cho item giỏ hàng nếu chưa có
        if (item.getId() == null || item.getId().isEmpty()) {
            String itemId = getGioHangReference().child(maKH).push().getKey();
            item.setId(itemId);
        }

        // Thêm item vào giỏ hàng
        getGioHangReference().child(maKH).child(item.getId()).setValue(item)
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

    // Tạo đơn hàng mới
    public void createOrder(DonHang donHang, List<DonHangChiTiet> chiTietList, FirebaseCallback<String> callback) {
        // Tạo key mới cho đơn hàng nếu chưa có
        if (donHang.getMaDonHang() == null || donHang.getMaDonHang().isEmpty()) {
            String orderId = getDonHangReference().push().getKey();
            donHang.setMaDonHang(orderId);
        }

        // Thêm đơn hàng vào database
        getDonHangReference().child(donHang.getMaDonHang()).setValue(donHang)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Thêm chi tiết đơn hàng
                        Map<String, Object> chiTietUpdates = new HashMap<>();
                        for (int i = 0; i < chiTietList.size(); i++) {
                            DonHangChiTiet chiTiet = chiTietList.get(i);
                            chiTiet.setMaDonHang(donHang.getMaDonHang());
                            chiTietUpdates.put(String.valueOf(i), chiTiet);
                        }

                        getDonHangChiTietReference().child(donHang.getMaDonHang()).updateChildren(chiTietUpdates)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Tạo lịch sử mua hàng
                                        String lsmhId = getLichSuMuaHangReference().push().getKey();
                                        LichSuMuaHang lichSu = new LichSuMuaHang(lsmhId, donHang.getMaKH(),
                                                donHang.getMaDonHang(), donHang.getNgayDat());

                                        getLichSuMuaHangReference().child(lsmhId).setValue(lichSu)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Xóa giỏ hàng sau khi đặt hàng thành công
                                                        getGioHangReference().child(donHang.getMaKH()).removeValue()
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
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    // Cập nhật trạng thái đơn hàng
    public void updateOrderStatus(String maDonHang, String trangThai, FirebaseCallback<Void> callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("trangThai", trangThai);

        getDonHangReference().child(maDonHang).updateChildren(updates)
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

    // Interface callback để xử lý dữ liệu từ Firebase
    public interface FirebaseCallback<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }
}