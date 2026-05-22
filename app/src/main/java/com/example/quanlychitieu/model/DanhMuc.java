package com.example.quanlychitieu.model;

public class DanhMuc {
    private int id;
    private String ten;
    private String icon;
    private String loai;

    public DanhMuc() {}

    public DanhMuc(int id, String ten, String icon) {
        this.id = id;
        this.ten = ten;
        this.icon = icon;
        this.loai = "chi";
    }

    public DanhMuc(int id, String ten, String icon, String loai) {
        this.id = id;
        this.ten = ten;
        this.icon = icon;
        this.loai = loai;
    }

    public DanhMuc(String ten) {
        this.ten = ten;
        this.loai = "chi";
    }

    public DanhMuc(String ten, String loai) {
        this.ten = ten;
        this.loai = loai;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getLoai() { return loai; }
    public void setLoai(String loai) { this.loai = loai; }

    @Override
    public String toString() {
        return ten;
    }
}
