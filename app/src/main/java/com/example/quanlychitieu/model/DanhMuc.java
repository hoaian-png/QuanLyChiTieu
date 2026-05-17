package com.example.quanlychitieu.model;

public class DanhMuc {
    private int id;
    private String ten;
    private String icon;

    public DanhMuc() {}

    public DanhMuc(int id, String ten, String icon) {
        this.id = id;
        this.ten = ten;
        this.icon = icon;
    }

    public DanhMuc(String ten) {
        this.ten = ten;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    @Override
    public String toString() {
        return ten;
    }
}
