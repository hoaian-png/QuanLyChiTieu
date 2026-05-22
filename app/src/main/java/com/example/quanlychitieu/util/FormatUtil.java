package com.example.quanlychitieu.util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FormatUtil {

    private static final Locale VI_LOCALE = new Locale("vi", "VN");
    private static final NumberFormat VND_FORMAT;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", VI_LOCALE);
    private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("dd MMM", VI_LOCALE);
    private static final SimpleDateFormat MONTH_YEAR_FORMAT = new SimpleDateFormat("MM/yyyy", VI_LOCALE);

    static {
        VND_FORMAT = NumberFormat.getNumberInstance(VI_LOCALE);
        VND_FORMAT.setMaximumFractionDigits(0);
    }

    public static String formatTien(double amount) {
        return VND_FORMAT.format(amount) + "₫";
    }

    public static String formatTienSign(double amount, boolean isChi) {
        return (isChi ? "- " : "+ ") + formatTien(amount);
    }

    public static String formatTienSigned(double amount) {
        if (amount > 0) {
            return "+ " + formatTien(amount);
        }
        if (amount < 0) {
            return "- " + formatTien(Math.abs(amount));
        }
        return formatTien(0);
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
            SimpleDateFormat display = new SimpleDateFormat("'Tháng' MM, yyyy", VI_LOCALE);
            return display.format(date);
        } catch (Exception e) {
            return thangMM_yyyy;
        }
    }

    public static boolean isHoiNay(long millis) {
        Calendar now = Calendar.getInstance();
        Calendar that = Calendar.getInstance();
        that.setTimeInMillis(millis);
        return now.get(Calendar.YEAR) == that.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == that.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isHomQua(long millis) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        Calendar that = Calendar.getInstance();
        that.setTimeInMillis(millis);
        return yesterday.get(Calendar.YEAR) == that.get(Calendar.YEAR)
                && yesterday.get(Calendar.DAY_OF_YEAR) == that.get(Calendar.DAY_OF_YEAR);
    }

    public static String formatNgayRelative(long millis) {
        if (isHoiNay(millis)) return "Hôm nay";
        if (isHomQua(millis)) return "Hôm qua";
        return formatNgay(millis);
    }
}
