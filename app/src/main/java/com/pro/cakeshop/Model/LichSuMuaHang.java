package com.pro.cakeshop.Model;

public class LichSuMuaHang {
    private String maLSMH;
    private String maKH;
    private String maDonHang;
    private String ngayMua;

    public LichSuMuaHang() {}

    public LichSuMuaHang(String maLSMH, String maKH, String maDonHang, String ngayMua) {
        this.maLSMH = maLSMH;
        this.maKH = maKH;
        this.maDonHang = maDonHang;
        this.ngayMua = ngayMua;
    }

    public String getMaLSMH() { return maLSMH; }
    public void setMaLSMH(String maLSMH) { this.maLSMH = maLSMH; }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public String getMaDonHang() { return maDonHang; }
    public void setMaDonHang(String maDonHang) { this.maDonHang = maDonHang; }

    public String getNgayMua() { return ngayMua; }
    public void setNgayMua(String ngayMua) { this.ngayMua = ngayMua; }
}

