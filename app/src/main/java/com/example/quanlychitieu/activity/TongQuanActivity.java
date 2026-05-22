package com.example.quanlychitieu.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.quanlychitieu.R;
import com.example.quanlychitieu.model.GiaoDich;
import com.example.quanlychitieu.sqlite.GiaoDichDAO;
import com.example.quanlychitieu.sqlite.NganSachDAO;
import com.example.quanlychitieu.util.FormatUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TongQuanActivity extends AppCompatActivity {

    // Summary cards
    private TextView tvTongThu, tvTongChi, tvConLai;

    // Budget section
    private TextView tvDaChi, tvHanMuc, tvConLaiBudget;
    private ProgressBar progressBudget;

    // Filter chips
    private TextView chipTatCa, chipThuNhap, chipChiTieu;

    // Search & filter
    private EditText etSearch;
    private Spinner spinnerThang;

    // Transaction list
    private LinearLayout llTransactions;

    // Bottom nav
    private LinearLayout navHome, navDanhMuc, navNganSach;

    // FAB
    private FloatingActionButton fabAdd;

    // Data
    private GiaoDichDAO giaoDichDAO;
    private NganSachDAO nganSachDAO;

    private String currentThang;
    private String currentFilter = "all"; // "all", "thu", "chi"
    private List<String> thangList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tong_quan);

        giaoDichDAO = new GiaoDichDAO(this);
        nganSachDAO = new NganSachDAO(this);
        currentThang = FormatUtil.getCurrentThang();

        initViews();
        setupSpinner();
        setupFilters();
        setupSearch();
        setupBottomNav();
        setupFab();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void initViews() {
        tvTongThu = findViewById(R.id.tvTongThu);
        tvTongChi = findViewById(R.id.tvTongChi);
        tvConLai = findViewById(R.id.tvConLai);

        tvDaChi = findViewById(R.id.tvDaChi);
        tvHanMuc = findViewById(R.id.tvHanMuc);
        tvConLaiBudget = findViewById(R.id.tvConLaiBudget);
        progressBudget = findViewById(R.id.progressBudget);

        chipTatCa = findViewById(R.id.chipTatCa);
        chipThuNhap = findViewById(R.id.chipThuNhap);
        chipChiTieu = findViewById(R.id.chipChiTieu);

        etSearch = findViewById(R.id.etSearch);
        spinnerThang = findViewById(R.id.spinnerThang);

        llTransactions = findViewById(R.id.llTransactions);
        navHome = findViewById(R.id.navHome);
        navDanhMuc = findViewById(R.id.navDanhMuc);
        navNganSach = findViewById(R.id.navNganSach);
        fabAdd = findViewById(R.id.fabAdd);
    }

    private void setupSpinner() {
        // Build last 12 months
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 12; i++) {
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            thangList.add(String.format("%02d/%04d", month, year));
            cal.add(Calendar.MONTH, -1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, thangList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(ContextCompat.getColor(TongQuanActivity.this, R.color.on_surface));
                tv.setTextSize(12);
                tv.setText(FormatUtil.formatThangHienThi(thangList.get(position)));
                return tv;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
                tv.setTextColor(ContextCompat.getColor(TongQuanActivity.this, R.color.on_surface));
                tv.setBackgroundColor(ContextCompat.getColor(TongQuanActivity.this, R.color.surface_container));
                tv.setPadding(24, 16, 24, 16);
                tv.setText(FormatUtil.formatThangHienThi(thangList.get(position)));
                return tv;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerThang.setAdapter(adapter);
        spinnerThang.setSelection(0);

        spinnerThang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentThang = thangList.get(position);
                loadData();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupFilters() {
        chipTatCa.setOnClickListener(v -> {
            currentFilter = "all";
            updateChipUI();
            loadTransactions();
        });
        chipThuNhap.setOnClickListener(v -> {
            currentFilter = "thu";
            updateChipUI();
            loadTransactions();
        });
        chipChiTieu.setOnClickListener(v -> {
            currentFilter = "chi";
            updateChipUI();
            loadTransactions();
        });
    }

    private void updateChipUI() {
        // Reset all
        chipTatCa.setBackgroundResource(R.drawable.bg_chip_inactive);
        chipTatCa.setTextColor(ContextCompat.getColor(this, R.color.on_surface_variant));
        chipThuNhap.setBackgroundResource(R.drawable.bg_chip_inactive);
        chipThuNhap.setTextColor(ContextCompat.getColor(this, R.color.on_surface_variant));
        chipChiTieu.setBackgroundResource(R.drawable.bg_chip_inactive);
        chipChiTieu.setTextColor(ContextCompat.getColor(this, R.color.on_surface_variant));

        // Activate selected
        switch (currentFilter) {
            case "all":
                chipTatCa.setBackgroundResource(R.drawable.bg_chip_active);
                chipTatCa.setTextColor(ContextCompat.getColor(this, R.color.on_primary_container));
                break;
            case "thu":
                chipThuNhap.setBackgroundResource(R.drawable.bg_chip_active);
                chipThuNhap.setTextColor(ContextCompat.getColor(this, R.color.on_primary_container));
                break;
            case "chi":
                chipChiTieu.setBackgroundResource(R.drawable.bg_chip_active);
                chipChiTieu.setTextColor(ContextCompat.getColor(this, R.color.on_primary_container));
                break;
        }
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadTransactions();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupBottomNav() {
        navHome.setOnClickListener(v -> { /* Already here */ });
        navDanhMuc.setOnClickListener(v -> {
            startActivity(new Intent(this, DanhMucActivity.class));
        });
        navNganSach.setOnClickListener(v -> {
            startActivity(new Intent(this, NganSachActivity.class));
        });
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, ThemGiaoDichActivity.class));
        });
    }

    private void loadData() {
        loadSummaryCards();
        loadBudget();
        loadTransactions();
    }

    private void loadSummaryCards() {
        double tongThu = giaoDichDAO.getTongByLoaiAndThang("thu", currentThang);
        double tongChi = giaoDichDAO.getTongByLoaiAndThang("chi", currentThang);
        double conLai = tongThu - tongChi;

        tvTongThu.setText(FormatUtil.formatTien(tongThu));
        tvTongChi.setText(FormatUtil.formatTien(tongChi));
        tvConLai.setText(FormatUtil.formatTienSigned(conLai));
        tvConLai.setTextColor(ContextCompat.getColor(this,
                conLai >= 0 ? R.color.primary_container : R.color.error));
    }

    private void loadBudget() {
        double hanMuc = nganSachDAO.getHanMuc(currentThang);
        double daChi = giaoDichDAO.getTongByLoaiAndThang("chi", currentThang);
        double conLai = hanMuc - daChi;

        tvDaChi.setText("Đã chi: " + FormatUtil.formatTien(daChi));
        tvHanMuc.setText("Giới hạn: " + FormatUtil.formatTien(hanMuc));
        tvConLaiBudget.setText("Còn lại: " + FormatUtil.formatTien(Math.max(0, conLai)));

        if (hanMuc > 0) {
            int progress = (int) Math.min(100, (daChi / hanMuc) * 100);
            progressBudget.setProgress(progress);
        } else {
            progressBudget.setProgress(0);
        }
    }

    private void loadTransactions() {
        String keyword = etSearch.getText().toString().trim();
        List<GiaoDich> list;

        if (!keyword.isEmpty()) {
            list = giaoDichDAO.search(keyword, currentThang, "all".equals(currentFilter) ? null : currentFilter);
        } else if ("thu".equals(currentFilter)) {
            list = giaoDichDAO.getByThangAndLoai(currentThang, "thu");
        } else if ("chi".equals(currentFilter)) {
            list = giaoDichDAO.getByThangAndLoai(currentThang, "chi");
        } else {
            list = giaoDichDAO.getByThang(currentThang);
        }

        llTransactions.removeAllViews();

        if (list.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Không có giao dịch nào");
            tvEmpty.setTextColor(ContextCompat.getColor(this, R.color.on_surface_variant));
            tvEmpty.setGravity(Gravity.CENTER);
            tvEmpty.setPadding(0, 48, 0, 48);
            llTransactions.addView(tvEmpty);
            return;
        }

        for (GiaoDich gd : list) {
            addTransactionItem(gd);
        }
    }

    private void addTransactionItem(GiaoDich gd) {
        // Container
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackgroundResource(R.drawable.bg_transaction_item);
        row.setPadding(dp(12), dp(12), dp(12), dp(12));

        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.bottomMargin = dp(8);
        row.setLayoutParams(rowParams);

        // Icon
        ImageView icon = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(40), dp(40));
        iconParams.setMarginEnd(dp(12));
        icon.setLayoutParams(iconParams);
        icon.setBackgroundResource(R.drawable.bg_icon_circle);
        icon.setPadding(dp(8), dp(8), dp(8), dp(8));
        icon.setImageResource(android.R.drawable.ic_menu_gallery);
        icon.setColorFilter(ContextCompat.getColor(this, R.color.on_surface_variant));
        row.addView(icon);

        // Text block
        LinearLayout textBlock = new LinearLayout(this);
        textBlock.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        textBlock.setLayoutParams(textParams);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(gd.getTenDanhMuc() != null ? gd.getTenDanhMuc() : "Không có danh mục");
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.on_surface));
        tvTitle.setTextSize(16);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        textBlock.addView(tvTitle);

        TextView tvSub = new TextView(this);
        String ghiChu = (gd.getGhiChu() != null && !gd.getGhiChu().isEmpty()) ? gd.getGhiChu() : "";
        String ngayStr = FormatUtil.formatNgayRelative(gd.getNgay());
        tvSub.setText(ghiChu.isEmpty() ? ngayStr : ghiChu + " • " + ngayStr);
        tvSub.setTextColor(ContextCompat.getColor(this, R.color.on_surface_variant));
        tvSub.setTextSize(12);
        textBlock.addView(tvSub);

        row.addView(textBlock);

        // Amount
        TextView tvAmount = new TextView(this);
        boolean isChi = gd.isChi();
        tvAmount.setText(FormatUtil.formatTienSign(gd.getSoTien(), isChi));
        tvAmount.setTextColor(ContextCompat.getColor(this, isChi ? R.color.error : R.color.primary_container));
        tvAmount.setTextSize(16);
        tvAmount.setTypeface(null, android.graphics.Typeface.BOLD);
        row.addView(tvAmount);

        // Long click to delete
        row.setOnLongClickListener(v -> {
            showDeleteDialog(gd);
            return true;
        });

        llTransactions.addView(row);
    }

    private void showDeleteDialog(GiaoDich gd) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa giao dịch")
                .setMessage("Bạn có chắc muốn xóa giao dịch \"" +
                        (gd.getTenDanhMuc() != null ? gd.getTenDanhMuc() : "") + " - " +
                        FormatUtil.formatTien(gd.getSoTien()) + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    giaoDichDAO.delete(gd.getId());
                    loadData();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
