package com.example.quanlychitieu.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quan_ly_chi_tieu.db";
    private static final int DATABASE_VERSION = 1;

    // Table: danh_muc
    public static final String TABLE_DANH_MUC = "danh_muc";
    public static final String COL_DM_ID = "id";
    public static final String COL_DM_TEN = "ten";
    public static final String COL_DM_ICON = "icon";     // resource name string

    // Table: giao_dich
    public static final String TABLE_GIAO_DICH = "giao_dich";
    public static final String COL_GD_ID = "id";
    public static final String COL_GD_SO_TIEN = "so_tien";
    public static final String COL_GD_LOAI = "loai";         // "chi" or "thu"
    public static final String COL_GD_DANH_MUC_ID = "danh_muc_id";
    public static final String COL_GD_NGAY = "ngay";          // milliseconds
    public static final String COL_GD_GHI_CHU = "ghi_chu";

    // Table: ngan_sach
    public static final String TABLE_NGAN_SACH = "ngan_sach";
    public static final String COL_NS_ID = "id";
    public static final String COL_NS_THANG = "thang";        // "MM/yyyy"
    public static final String COL_NS_HAN_MUC = "han_muc";

    private static final String CREATE_TABLE_DANH_MUC =
            "CREATE TABLE " + TABLE_DANH_MUC + " (" +
            COL_DM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_DM_TEN + " TEXT NOT NULL, " +
            COL_DM_ICON + " TEXT" +
            ");";

    private static final String CREATE_TABLE_GIAO_DICH =
            "CREATE TABLE " + TABLE_GIAO_DICH + " (" +
            COL_GD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_GD_SO_TIEN + " REAL NOT NULL, " +
            COL_GD_LOAI + " TEXT NOT NULL, " +
            COL_GD_DANH_MUC_ID + " INTEGER, " +
            COL_GD_NGAY + " INTEGER NOT NULL, " +
            COL_GD_GHI_CHU + " TEXT, " +
            "FOREIGN KEY(" + COL_GD_DANH_MUC_ID + ") REFERENCES " +
            TABLE_DANH_MUC + "(" + COL_DM_ID + ")" +
            ");";

    private static final String CREATE_TABLE_NGAN_SACH =
            "CREATE TABLE " + TABLE_NGAN_SACH + " (" +
            COL_NS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_NS_THANG + " TEXT NOT NULL UNIQUE, " +
            COL_NS_HAN_MUC + " REAL NOT NULL" +
            ");";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DANH_MUC);
        db.execSQL(CREATE_TABLE_GIAO_DICH);
        db.execSQL(CREATE_TABLE_NGAN_SACH);
        insertDefaultData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GIAO_DICH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NGAN_SACH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DANH_MUC);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private void insertDefaultData(SQLiteDatabase db) {
        // Default categories
        String[] categories = {"Ăn uống", "Nhà cửa", "Học tập", "Di chuyển", "Y tế", "Mua sắm", "Giải trí", "Lương"};
        for (String cat : categories) {
            db.execSQL("INSERT INTO " + TABLE_DANH_MUC + " (" + COL_DM_TEN + ") VALUES ('" + cat + "')");
        }

        // Default budget for current month
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String thangHienTai = String.format("%02d/%04d", cal.get(java.util.Calendar.MONTH) + 1,
                cal.get(java.util.Calendar.YEAR));
        db.execSQL("INSERT INTO " + TABLE_NGAN_SACH + " (" + COL_NS_THANG + ", " + COL_NS_HAN_MUC + ") VALUES ('" +
                thangHienTai + "', 10000000)");

        // Sample transactions
        long now = System.currentTimeMillis();
        long oneDayMs = 86400000L;
        db.execSQL("INSERT INTO " + TABLE_GIAO_DICH + " (so_tien, loai, danh_muc_id, ngay, ghi_chu) VALUES (50000, 'chi', 1, " + now + ", 'Cơm trưa văn phòng')");
        db.execSQL("INSERT INTO " + TABLE_GIAO_DICH + " (so_tien, loai, danh_muc_id, ngay, ghi_chu) VALUES (5000000, 'chi', 2, " + (now - oneDayMs) + ", 'Tiền nhà tháng này')");
        db.execSQL("INSERT INTO " + TABLE_GIAO_DICH + " (so_tien, loai, danh_muc_id, ngay, ghi_chu) VALUES (15000000, 'thu', 8, " + (now - 2 * oneDayMs) + ", 'Lương tháng này')");
        db.execSQL("INSERT INTO " + TABLE_GIAO_DICH + " (so_tien, loai, danh_muc_id, ngay, ghi_chu) VALUES (200000, 'chi', 4, " + (now - 3 * oneDayMs) + ", 'Xe buýt và Grab')");
        db.execSQL("INSERT INTO " + TABLE_GIAO_DICH + " (so_tien, loai, danh_muc_id, ngay, ghi_chu) VALUES (500000, 'chi', 6, " + (now - 4 * oneDayMs) + ", 'Quần áo')");
    }
}
