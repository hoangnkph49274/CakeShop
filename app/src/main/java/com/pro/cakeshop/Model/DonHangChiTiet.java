package com.pro.cakeshop.Model;

public class DonHangChiTiet {
    private String maDonHang;
    private String maBanh;
    private int soLuong;
    private int thanhTien;

    // Constructor
    public DonHangChiTiet() {
    }

    public DonHangChiTiet(String maDonHang, String maBanh, int soLuong, int thanhTien) {
        this.maDonHang = maDonHang;
        this.maBanh = maBanh;
        this.soLuong = soLuong;
        this.thanhTien = thanhTien;
    }

    // Getters and Setters
    public String getMaDonHang() {
        return maDonHang;
    }

    public void setMaDonHang(String maDonHang) {
        this.maDonHang = maDonHang;
    }

    public String getMaBanh() {
        return maBanh;
    }

    public void setMaBanh(String maBanh) {
        this.maBanh = maBanh;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public int getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(int thanhTien) {
        this.thanhTien = thanhTien;
    }
}