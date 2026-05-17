package com.example.quanlychitieu.model;

public class GiaoDich {
    private int id;
    private double soTien;
    private String loai;        // "chi" or "thu"
    private int danhMucId;
    private long ngay;          // milliseconds since epoch
    private String ghiChu;

    // joined field
    private String tenDanhMuc;

    public GiaoDich() {}

    public GiaoDich(double soTien, String loai, int danhMucId, long ngay, String ghiChu) {
        this.soTien = soTien;
        this.loai = loai;
        this.danhMucId = danhMucId;
        this.ngay = ngay;
        this.ghiChu = ghiChu;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getSoTien() { return soTien; }
    public void setSoTien(double soTien) { this.soTien = soTien; }

    public String getLoai() { return loai; }
    public void setLoai(String loai) { this.loai = loai; }

    public int getDanhMucId() { return danhMucId; }
    public void setDanhMucId(int danhMucId) { this.danhMucId = danhMucId; }

    public long getNgay() { return ngay; }
    public void setNgay(long ngay) { this.ngay = ngay; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String getTenDanhMuc() { return tenDanhMuc; }
    public void setTenDanhMuc(String tenDanhMuc) { this.tenDanhMuc = tenDanhMuc; }

    public boolean isChi() {
        return "chi".equals(loai);
    }
}
