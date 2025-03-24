package com.pro.cakeshop.Model;

public class DonHangChiTiet {
    private String maDHCT;
    private String maDonHang;
    private String maBanh;
    private int soLuong;
    private double thanhTien;

    public DonHangChiTiet() {}

    public DonHangChiTiet(String maDHCT, String maDonHang, String maBanh, int soLuong, double thanhTien) {
        this.maDHCT = maDHCT;
        this.maDonHang = maDonHang;
        this.maBanh = maBanh;
        this.soLuong = soLuong;
        this.thanhTien = thanhTien;
    }

    public String getMaDHCT() { return maDHCT; }
    public void setMaDHCT(String maDHCT) { this.maDHCT = maDHCT; }

    public String getMaDonHang() { return maDonHang; }
    public void setMaDonHang(String maDonHang) { this.maDonHang = maDonHang; }

    public String getMaBanh() { return maBanh; }
    public void setMaBanh(String maBanh) { this.maBanh = maBanh; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }
}
