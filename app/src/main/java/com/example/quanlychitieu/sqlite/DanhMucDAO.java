package com.example.quanlychitieu.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.quanlychitieu.model.DanhMuc;

import java.util.ArrayList;
import java.util.List;

public class DanhMucDAO {

    private final DBHelper dbHelper;

    public DanhMucDAO(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    // INSERT
    public long insert(DanhMuc danhMuc) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_DM_TEN, danhMuc.getTen());
        values.put(DBHelper.COL_DM_ICON, danhMuc.getIcon());
        values.put(DBHelper.COL_DM_LOAI, normalizeLoai(danhMuc.getLoai()));
        return db.insert(DBHelper.TABLE_DANH_MUC, null, values);
    }

    // UPDATE
    public int update(DanhMuc danhMuc) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_DM_TEN, danhMuc.getTen());
        values.put(DBHelper.COL_DM_ICON, danhMuc.getIcon());
        values.put(DBHelper.COL_DM_LOAI, normalizeLoai(danhMuc.getLoai()));
        return db.update(DBHelper.TABLE_DANH_MUC, values,
                DBHelper.COL_DM_ID + " = ?",
                new String[]{String.valueOf(danhMuc.getId())});
    }

    // DELETE
    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues unlinkValues = new ContentValues();
        unlinkValues.putNull(DBHelper.COL_GD_DANH_MUC_ID);
        db.update(DBHelper.TABLE_GIAO_DICH, unlinkValues,
                DBHelper.COL_GD_DANH_MUC_ID + " = ?",
                new String[]{String.valueOf(id)});
        return db.delete(DBHelper.TABLE_DANH_MUC,
                DBHelper.COL_DM_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // GET ALL
    public List<DanhMuc> getAll() {
        List<DanhMuc> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DBHelper.TABLE_DANH_MUC +
                " ORDER BY " + DBHelper.COL_DM_TEN + " ASC";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToDanhMuc(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<DanhMuc> getByLoai(String loai) {
        List<DanhMuc> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DBHelper.TABLE_DANH_MUC +
                " WHERE " + DBHelper.COL_DM_LOAI + " = ?" +
                " ORDER BY " + DBHelper.COL_DM_TEN + " ASC";
        Cursor cursor = db.rawQuery(query, new String[]{normalizeLoai(loai)});
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToDanhMuc(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int countTransactions(int danhMucId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DBHelper.TABLE_GIAO_DICH +
                " WHERE " + DBHelper.COL_GD_DANH_MUC_ID + " = ?",
                new String[]{String.valueOf(danhMucId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // GET BY ID
    public DanhMuc getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_DANH_MUC, null,
                DBHelper.COL_DM_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        DanhMuc danhMuc = null;
        if (cursor.moveToFirst()) {
            danhMuc = cursorToDanhMuc(cursor);
        }
        cursor.close();
        return danhMuc;
    }

    private DanhMuc cursorToDanhMuc(Cursor cursor) {
        DanhMuc dm = new DanhMuc();
        dm.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_DM_ID)));
        dm.setTen(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_DM_TEN)));
        int iconIdx = cursor.getColumnIndex(DBHelper.COL_DM_ICON);
        if (iconIdx != -1 && !cursor.isNull(iconIdx)) {
            dm.setIcon(cursor.getString(iconIdx));
        }
        int loaiIdx = cursor.getColumnIndex(DBHelper.COL_DM_LOAI);
        if (loaiIdx != -1 && !cursor.isNull(loaiIdx)) {
            dm.setLoai(cursor.getString(loaiIdx));
        } else {
            dm.setLoai("chi");
        }
        return dm;
    }

    private String normalizeLoai(String loai) {
        return "thu".equals(loai) ? "thu" : "chi";
    }
}
