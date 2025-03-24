package com.pro.cakeshop.Model;

public class Banh {
    private String maBanh;
    private String tenBanh;
    private double gia;
    private String moTa;
    private String hinhAnh;
    private String maLoai;

    public Banh() {}

    public Banh(String maBanh, String tenBanh, double gia, String moTa, String hinhAnh, String maLoai) {
        this.maBanh = maBanh;
        this.tenBanh = tenBanh;
        this.gia = gia;
        this.moTa = moTa;
        this.hinhAnh = hinhAnh;
        this.maLoai = maLoai;
    }

    public String getMaBanh() { return maBanh; }
    public void setMaBanh(String maBanh) { this.maBanh = maBanh; }

    public String getTenBanh() { return tenBanh; }
    public void setTenBanh(String tenBanh) { this.tenBanh = tenBanh; }

    public double getGia() { return gia; }
    public void setGia(double gia) { this.gia = gia; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public String getMaLoai() { return maLoai; }
    public void setMaLoai(String maLoai) { this.maLoai = maLoai; }
}
