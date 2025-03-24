package com.pro.cakeshop.Model;

public class GioHang {
    private String maGioHang;
    private String maKH;
    private String maBanh;
    private int soLuong;

    public GioHang() {}

    public GioHang(String maGioHang, String maKH, String maBanh, int soLuong) {
        this.maGioHang = maGioHang;
        this.maKH = maKH;
        this.maBanh = maBanh;
        this.soLuong = soLuong;
    }

    public String getMaGioHang() { return maGioHang; }
    public void setMaGioHang(String maGioHang) { this.maGioHang = maGioHang; }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public String getMaBanh() { return maBanh; }
    public void setMaBanh(String maBanh) { this.maBanh = maBanh; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
}
