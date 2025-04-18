package com.pro.cakeshop.Model;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Model Đơn Hàng (Order) with DonHangChiTiet relationship
public class DonHang implements Serializable {
    private String maDonHang;
    private String maKH;
    private String diaChi;
    private Object ngayDat;
    private long tongTien;
    private String trangThai;
    private List<DonHangChiTiet> donHangChiTiet; // List of order details

    // Constructor
    public DonHang() {
        this.donHangChiTiet = new ArrayList<>();
    }

    public DonHang(String maDonHang, String maKH, String diaChi, String ngayDat, long tongTien, String trangThai) {
        this.maDonHang = maDonHang;
        this.maKH = maKH;
        this.diaChi = diaChi;
        this.ngayDat = ngayDat;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
        this.donHangChiTiet = new ArrayList<>();
    }

    // Getters and Setters
    public String getMaDonHang() {
        return maDonHang;
    }

    public void setMaDonHang(String maDonHang) {
        this.maDonHang = maDonHang;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getNgayDat() {
        if (ngayDat instanceof Long) {
            long timestamp = (Long) ngayDat;
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            return sdf.format(date);
        } else if (ngayDat instanceof String) {
            return (String) ngayDat;
        } else {
            return "";
        }
    }

    // Add a new method to get the date as a formatted string for display
    public String getFormattedDate() {
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        try {
            if (ngayDat instanceof Long) {
                long timestamp = (Long) ngayDat;
                Date date = new Date(timestamp);
                return displayFormat.format(date);
            } else if (ngayDat instanceof String) {
                // Try to parse the ISO string
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                Date date = isoFormat.parse((String) ngayDat);
                return displayFormat.format(date);
            }
        } catch (Exception e) {
            // Return the raw value if parsing fails
            return String.valueOf(ngayDat);
        }

        return "Không xác định";
    }

    // Add a method to get the date as a timestamp (for filtering)
    public long getDateTimestamp() {
        if (ngayDat instanceof Long) {
            return (Long) ngayDat;
        } else if (ngayDat instanceof String) {
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                Date date = isoFormat.parse((String) ngayDat);
                return date.getTime();
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    // Keep your current setter
    public void setNgayDat(Object ngayDat) {
        this.ngayDat = ngayDat;
    }
    public long getTongTien() {
        return tongTien;
    }

    public void setTongTien(long tongTien) {
        this.tongTien = tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public List<DonHangChiTiet> getDonHangChiTiet() {
        return donHangChiTiet;
    }

    public void setDonHangChiTiet(List<DonHangChiTiet> donHangChiTiet) {
        this.donHangChiTiet = donHangChiTiet;
    }

    // Helper method to add a single order detail
    public void addDonHangChiTiet(DonHangChiTiet chiTiet) {
        if (this.donHangChiTiet == null) {
            this.donHangChiTiet = new ArrayList<>();
        }
        this.donHangChiTiet.add(chiTiet);
    }

}
