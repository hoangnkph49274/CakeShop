package com.pro.cakeshop.Model;

public class DonHang {
    private String maDonHang;
    private String maKH;
    private String ngayDat;
    private double tongTien;
    private String trangThai;
    private String diaChi;

    public DonHang() {}

    public DonHang(String maDonHang, String maKH, String ngayDat, double tongTien, String trangThai, String diaChi) {
        this.maDonHang = maDonHang;
        this.maKH = maKH;
        this.ngayDat = ngayDat;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
        this.diaChi = diaChi;
    }

    public String getMaDonHang() { return maDonHang; }
    public void setMaDonHang(String maDonHang) { this.maDonHang = maDonHang; }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public String getNgayDat() { return ngayDat; }
    public void setNgayDat(String ngayDat) { this.ngayDat = ngayDat; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
}

