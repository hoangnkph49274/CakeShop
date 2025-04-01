package com.pro.cakeshop.Model;

public class GioHang {
    private long maGioHang;
    private String maKH;
    private String maBanh;
    private int soLuong;

    public GioHang() { }

    public GioHang(long maGioHang, String maKH, String maBanh, int soLuong) {
        this.maGioHang = maGioHang;
        this.maKH = maKH;
        this.maBanh = maBanh;
        this.soLuong = soLuong;
    }

//    public String getMaGioHang() { return maGioHang; }
    public long getMaGioHang() { return maGioHang; }
    public String getMaKH() { return maKH; }
    public String getMaBanh() { return maBanh; }
    public int getSoLuong() { return soLuong; }

    public void setMaGioHang(long maGioHang) { this.maGioHang = maGioHang; }
    public void setMaKH(String maKH) { this.maKH = maKH; }
    public void setMaBanh(String maBanh) { this.maBanh = maBanh; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
}
