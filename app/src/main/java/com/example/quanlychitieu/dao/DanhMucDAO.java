package com.example.quanlychitieu.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.quanlychitieu.database.DatabaseHelper;
import com.example.quanlychitieu.model.DanhMuc;

import java.util.ArrayList;
import java.util.List;

public class DanhMucDAO {

    private final DatabaseHelper dbHelper;

    public DanhMucDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // INSERT
    public long insert(DanhMuc danhMuc) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_DM_TEN, danhMuc.getTen());
        values.put(DatabaseHelper.COL_DM_ICON, danhMuc.getIcon());
        return db.insert(DatabaseHelper.TABLE_DANH_MUC, null, values);
    }

    // UPDATE
    public int update(DanhMuc danhMuc) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_DM_TEN, danhMuc.getTen());
        values.put(DatabaseHelper.COL_DM_ICON, danhMuc.getIcon());
        return db.update(DatabaseHelper.TABLE_DANH_MUC, values,
                DatabaseHelper.COL_DM_ID + " = ?",
                new String[]{String.valueOf(danhMuc.getId())});
    }

    // DELETE
    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_DANH_MUC,
                DatabaseHelper.COL_DM_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // GET ALL
    public List<DanhMuc> getAll() {
        List<DanhMuc> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_DANH_MUC +
                " ORDER BY " + DatabaseHelper.COL_DM_TEN + " ASC";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToDanhMuc(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // GET BY ID
    public DanhMuc getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_DANH_MUC, null,
                DatabaseHelper.COL_DM_ID + " = ?",
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
        dm.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DM_ID)));
        dm.setTen(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DM_TEN)));
        int iconIdx = cursor.getColumnIndex(DatabaseHelper.COL_DM_ICON);
        if (iconIdx != -1 && !cursor.isNull(iconIdx)) {
            dm.setIcon(cursor.getString(iconIdx));
        }
        return dm;
    }
}
