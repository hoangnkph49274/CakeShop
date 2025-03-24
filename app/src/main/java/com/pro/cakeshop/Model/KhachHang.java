package com.pro.cakeshop.Model;

public class KhachHang {
    private String maKH;
    private String email;
    private String tenKH;
    private String sdt;
    private String diaChi;

    public KhachHang() {}

    public KhachHang(String maKH, String email, String tenKH, String sdt, String diaChi) {
        this.maKH = maKH;
        this.email = email;
        this.tenKH = tenKH;
        this.sdt = sdt;
        this.diaChi = diaChi;
    }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTenKH() { return tenKH; }
    public void setTenKH(String tenKH) { this.tenKH = tenKH; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
}
