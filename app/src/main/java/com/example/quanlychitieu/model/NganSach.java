package com.example.quanlychitieu.model;

public class NganSach {
    private int id;
    private String thang;
    private double hanMuc;

    public NganSach() {}

    public NganSach(String thang, double hanMuc) {
        this.thang = thang;
        this.hanMuc = hanMuc;
    }

    public NganSach(int id, String thang, double hanMuc) {
        this.id = id;
        this.thang = thang;
        this.hanMuc = hanMuc;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getThang() { return thang; }
    public void setThang(String thang) { this.thang = thang; }

    public double getHanMuc() { return hanMuc; }
    public void setHanMuc(double hanMuc) { this.hanMuc = hanMuc; }
}
