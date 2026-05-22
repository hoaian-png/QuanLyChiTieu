package com.example.quanlychitieu.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.quanlychitieu.model.GiaoDich;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GiaoDichDAO {

    private final DBHelper dbHelper;

    public GiaoDichDAO(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    // INSERT
    public long insert(GiaoDich giaoDich) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_GD_SO_TIEN, giaoDich.getSoTien());
        values.put(DBHelper.COL_GD_LOAI, giaoDich.getLoai());
        values.put(DBHelper.COL_GD_DANH_MUC_ID, giaoDich.getDanhMucId());
        values.put(DBHelper.COL_GD_NGAY, giaoDich.getNgay());
        values.put(DBHelper.COL_GD_GHI_CHU, giaoDich.getGhiChu());
        return db.insert(DBHelper.TABLE_GIAO_DICH, null, values);
    }

    // DELETE
    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DBHelper.TABLE_GIAO_DICH,
                DBHelper.COL_GD_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // GET ALL (joined with danh_muc for name)
    public List<GiaoDich> getAll() {
        return query(null, null, null);
    }

    // GET BY MONTH: thang "MM/yyyy"
    public List<GiaoDich> getByThang(String thang) {
        // Parse month/year
        String[] parts = thang.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);

        Calendar start = Calendar.getInstance();
        start.set(year, month - 1, 1, 0, 0, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = Calendar.getInstance();
        end.set(year, month - 1, 1, 0, 0, 0);
        end.set(Calendar.MILLISECOND, 0);
        end.add(Calendar.MONTH, 1);

        String where = "g." + DBHelper.COL_GD_NGAY + " >= ? AND g." + DBHelper.COL_GD_NGAY + " < ?";
        String[] args = {String.valueOf(start.getTimeInMillis()), String.valueOf(end.getTimeInMillis())};
        return query(where, args, null);
    }

    // GET BY LOAI in current month
    public List<GiaoDich> getByThangAndLoai(String thang, String loai) {
        String[] parts = thang.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);

        Calendar start = Calendar.getInstance();
        start.set(year, month - 1, 1, 0, 0, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = Calendar.getInstance();
        end.set(year, month - 1, 1, 0, 0, 0);
        end.set(Calendar.MILLISECOND, 0);
        end.add(Calendar.MONTH, 1);

        String where = "g." + DBHelper.COL_GD_NGAY + " >= ? AND g." +
                DBHelper.COL_GD_NGAY + " < ? AND g." + DBHelper.COL_GD_LOAI + " = ?";
        String[] args = {String.valueOf(start.getTimeInMillis()), String.valueOf(end.getTimeInMillis()), loai};
        return query(where, args, null);
    }

    // SEARCH by keyword
    public List<GiaoDich> search(String keyword, String thang) {
        return search(keyword, thang, null);
    }

    public List<GiaoDich> search(String keyword, String thang, String loai) {
        String[] parts = thang.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);

        Calendar start = Calendar.getInstance();
        start.set(year, month - 1, 1, 0, 0, 0);
        start.set(Calendar.MILLISECOND, 0);
        Calendar end = Calendar.getInstance();
        end.set(year, month - 1, 1, 0, 0, 0);
        end.set(Calendar.MILLISECOND, 0);
        end.add(Calendar.MONTH, 1);

        String where = "g." + DBHelper.COL_GD_NGAY + " >= ? AND g." +
                DBHelper.COL_GD_NGAY + " < ? AND (d." + DBHelper.COL_DM_TEN +
                " LIKE ? OR g." + DBHelper.COL_GD_GHI_CHU + " LIKE ?)";
        String likeArg = "%" + keyword + "%";
        String[] args;
        if ("thu".equals(loai) || "chi".equals(loai)) {
            where += " AND g." + DBHelper.COL_GD_LOAI + " = ?";
            args = new String[]{String.valueOf(start.getTimeInMillis()), String.valueOf(end.getTimeInMillis()), likeArg, likeArg, loai};
        } else {
            args = new String[]{String.valueOf(start.getTimeInMillis()), String.valueOf(end.getTimeInMillis()), likeArg, likeArg};
        }
        return query(where, args, null);
    }

    // SUM by loai and thang
    public double getTongByLoaiAndThang(String loai, String thang) {
        String[] parts = thang.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);

        Calendar start = Calendar.getInstance();
        start.set(year, month - 1, 1, 0, 0, 0);
        start.set(Calendar.MILLISECOND, 0);
        Calendar end = Calendar.getInstance();
        end.set(year, month - 1, 1, 0, 0, 0);
        end.set(Calendar.MILLISECOND, 0);
        end.add(Calendar.MONTH, 1);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COALESCE(SUM(so_tien), 0) FROM " + DBHelper.TABLE_GIAO_DICH +
                " WHERE loai = ? AND ngay >= ? AND ngay < ?";
        Cursor cursor = db.rawQuery(query, new String[]{loai,
                String.valueOf(start.getTimeInMillis()), String.valueOf(end.getTimeInMillis())});
        double sum = 0;
        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        return sum;
    }

    public double getTongChiByDanhMucAndThang(int danhMucId, String thang) {
        String[] parts = thang.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);

        Calendar start = Calendar.getInstance();
        start.set(year, month - 1, 1, 0, 0, 0);
        start.set(Calendar.MILLISECOND, 0);
        Calendar end = Calendar.getInstance();
        end.set(year, month - 1, 1, 0, 0, 0);
        end.set(Calendar.MILLISECOND, 0);
        end.add(Calendar.MONTH, 1);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT COALESCE(SUM(" + DBHelper.COL_GD_SO_TIEN + "), 0) FROM " + DBHelper.TABLE_GIAO_DICH +
                " WHERE " + DBHelper.COL_GD_LOAI + " = 'chi' AND " + DBHelper.COL_GD_DANH_MUC_ID + " = ?" +
                " AND " + DBHelper.COL_GD_NGAY + " >= ? AND " + DBHelper.COL_GD_NGAY + " < ?";
        Cursor cursor = db.rawQuery(sql, new String[]{
                String.valueOf(danhMucId),
                String.valueOf(start.getTimeInMillis()),
                String.valueOf(end.getTimeInMillis())
        });
        double sum = 0;
        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        return sum;
    }

    // Private query helper (LEFT JOIN danh_muc)
    private List<GiaoDich> query(String where, String[] args, String limit) {
        List<GiaoDich> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT g.*, d." + DBHelper.COL_DM_TEN + " AS ten_danh_muc " +
                "FROM " + DBHelper.TABLE_GIAO_DICH + " g " +
                "LEFT JOIN " + DBHelper.TABLE_DANH_MUC + " d ON g." +
                DBHelper.COL_GD_DANH_MUC_ID + " = d." + DBHelper.COL_DM_ID;
        if (where != null) {
            sql += " WHERE " + where;
        }
        sql += " ORDER BY g." + DBHelper.COL_GD_NGAY + " DESC";
        if (limit != null) {
            sql += " LIMIT " + limit;
        }

        Cursor cursor = db.rawQuery(sql, args);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToGiaoDich(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    private GiaoDich cursorToGiaoDich(Cursor cursor) {
        GiaoDich gd = new GiaoDich();
        gd.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_GD_ID)));
        gd.setSoTien(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_GD_SO_TIEN)));
        gd.setLoai(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_GD_LOAI)));
        gd.setDanhMucId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_GD_DANH_MUC_ID)));
        gd.setNgay(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_GD_NGAY)));
        int ghiChuIdx = cursor.getColumnIndex(DBHelper.COL_GD_GHI_CHU);
        if (ghiChuIdx != -1) gd.setGhiChu(cursor.getString(ghiChuIdx));
        int tenIdx = cursor.getColumnIndex("ten_danh_muc");
        if (tenIdx != -1) gd.setTenDanhMuc(cursor.getString(tenIdx));
        return gd;
    }
}
