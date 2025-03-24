package com.pro.cakeshop.Database;

import android.util.Log;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.cakeshop.Model.*;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper {

    private final FirebaseDatabase database;

    public FirebaseHelper() {
        this.database = FirebaseDatabase.getInstance();
    }

    // Tham chiếu đến bảng Khách Hàng
    public DatabaseReference getKhachHangReference() {
        return database.getReference("KhachHang");
    }

    // Tham chiếu đến bảng Lịch Sử Mua Hàng
    public DatabaseReference getLichSuMuaHangReference() {
        return database.getReference("LichSuMuaHang");
    }

    // Tham chiếu đến bảng Đơn Hàng
    public DatabaseReference getDonHangReference() {
        return database.getReference("DonHang");
    }

    // Tham chiếu đến bảng Giỏ Hàng
    public DatabaseReference getGioHangReference() {
        return database.getReference("GioHang");
    }

    // Tham chiếu đến bảng Bánh
    public DatabaseReference getBanhReference() {
        return database.getReference("Banh");
    }

    // Tham chiếu đến bảng Loại
    public DatabaseReference getLoaiReference() {
        return database.getReference("Loai");
    }

    // Tham chiếu đến bảng Đơn Hàng Chi Tiết
    public DatabaseReference getDonHangChiTietReference() {
        return database.getReference("DonHangChiTiet");
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

    // Interface callback để xử lý dữ liệu từ Firebase
    public interface FirebaseCallback<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }
}