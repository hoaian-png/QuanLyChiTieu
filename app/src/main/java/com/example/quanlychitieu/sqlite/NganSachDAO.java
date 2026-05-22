package com.example.quanlychitieu.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.quanlychitieu.model.NganSach;

public class NganSachDAO {

    private final DBHelper dbHelper;

    public NganSachDAO(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    /**
     * Get budget limit for a given month (format: "MM/yyyy").
     * Returns 0 if not set.
     */
    public double getHanMuc(String thang) {
        NganSach nganSach = getByThang(thang);
        return nganSach != null ? nganSach.getHanMuc() : 0;
    }

    public NganSach getByThang(String thang) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_NGAN_SACH, null,
                DBHelper.COL_NS_THANG + " = ?",
                new String[]{thang}, null, null, null);
        NganSach nganSach = null;
        if (cursor.moveToFirst()) {
            nganSach = new NganSach();
            nganSach.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_NS_ID)));
            nganSach.setThang(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NS_THANG)));
            nganSach.setHanMuc(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_NS_HAN_MUC)));
        }
        cursor.close();
        return nganSach;
    }

    /**
     * Insert or update budget for a month.
     */
    public void upsert(String thang, double hanMuc) {
        upsert(new NganSach(thang, hanMuc));
    }

    public void upsert(NganSach nganSach) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_NS_THANG, nganSach.getThang());
        values.put(DBHelper.COL_NS_HAN_MUC, nganSach.getHanMuc());

        int rows = db.update(DBHelper.TABLE_NGAN_SACH, values,
                DBHelper.COL_NS_THANG + " = ?", new String[]{nganSach.getThang()});
        if (rows == 0) {
            db.insert(DBHelper.TABLE_NGAN_SACH, null, values);
        }
    }
}
