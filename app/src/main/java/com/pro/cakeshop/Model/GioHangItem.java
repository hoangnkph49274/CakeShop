package com.pro.cakeshop.Model;
public class GioHangItem {
    private String id;
    private String maBanh;
    private String tenBanh;
    private int gia;
    private String hinhAnh;
    private int soLuong;

    // Constructor
    public GioHangItem() {
    }

    public GioHangItem(String id, String maBanh, String tenBanh, int gia, String hinhAnh, int soLuong) {
        this.id = id;
        this.maBanh = maBanh;
        this.tenBanh = tenBanh;
        this.gia = gia;
        this.hinhAnh = hinhAnh;
        this.soLuong = soLuong;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaBanh() {
        return maBanh;
    }

    public void setMaBanh(String maBanh) {
        this.maBanh = maBanh;
    }

    public String getTenBanh() {
        return tenBanh;
    }

    public void setTenBanh(String tenBanh) {
        this.tenBanh = tenBanh;
    }

    public int getGia() {
        return gia;
    }

    public void setGia(int gia) {
        this.gia = gia;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }
}