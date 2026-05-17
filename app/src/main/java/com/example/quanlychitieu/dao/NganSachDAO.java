package com.example.quanlychitieu.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.quanlychitieu.database.DatabaseHelper;

public class NganSachDAO {

    private final DatabaseHelper dbHelper;

    public NganSachDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    /**
     * Get budget limit for a given month (format: "MM/yyyy").
     * Returns 0 if not set.
     */
    public double getHanMuc(String thang) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NGAN_SACH, null,
                DatabaseHelper.COL_NS_THANG + " = ?",
                new String[]{thang}, null, null, null);
        double hanMuc = 0;
        if (cursor.moveToFirst()) {
            hanMuc = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NS_HAN_MUC));
        }
        cursor.close();
        return hanMuc;
    }

    /**
     * Insert or update budget for a month.
     */
    public void upsert(String thang, double hanMuc) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_NS_THANG, thang);
        values.put(DatabaseHelper.COL_NS_HAN_MUC, hanMuc);

        int rows = db.update(DatabaseHelper.TABLE_NGAN_SACH, values,
                DatabaseHelper.COL_NS_THANG + " = ?", new String[]{thang});
        if (rows == 0) {
            db.insert(DatabaseHelper.TABLE_NGAN_SACH, null, values);
        }
    }
}
