package com.example.quanlychitieu.util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtil {

    private static final NumberFormat VND_FORMAT;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));
    private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("dd MMM", new Locale("vi", "VN"));
    private static final SimpleDateFormat MONTH_YEAR_FORMAT = new SimpleDateFormat("MM/yyyy", new Locale("vi", "VN"));

    static {
        VND_FORMAT = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        VND_FORMAT.setMaximumFractionDigits(0);
    }

    public static String formatTien(double amount) {
        return VND_FORMAT.format(amount) + "₫";
    }

    public static String formatTienSign(double amount, boolean isChi) {
        return (isChi ? "- " : "+ ") + formatTien(amount);
    }

    public static String formatNgay(long millis) {
        return DATE_FORMAT.format(new Date(millis));
    }

    public static String formatNgayNgan(long millis) {
        return SHORT_DATE_FORMAT.format(new Date(millis));
    }

    public static String getCurrentThang() {
        return MONTH_YEAR_FORMAT.format(new Date());
    }

    public static String formatThangHienThi(String thangMM_yyyy) {
        try {
            Date date = MONTH_YEAR_FORMAT.parse(thangMM_yyyy);
            SimpleDateFormat display = new SimpleDateFormat("'Tháng' MM, yyyy", new Locale("vi", "VN"));
            return display.format(date);
        } catch (Exception e) {
            return thangMM_yyyy;
        }
    }

    public static boolean isHoiNay(long millis) {
        long nowDay = System.currentTimeMillis() / 86400000L;
        long thatDay = millis / 86400000L;
        return nowDay == thatDay;
    }

    public static boolean isHomQua(long millis) {
        long nowDay = System.currentTimeMillis() / 86400000L;
        long thatDay = millis / 86400000L;
        return nowDay - thatDay == 1;
    }

    public static String formatNgayRelative(long millis) {
        if (isHoiNay(millis)) return "Hôm nay";
        if (isHomQua(millis)) return "Hôm qua";
        return formatNgay(millis);
    }
}
