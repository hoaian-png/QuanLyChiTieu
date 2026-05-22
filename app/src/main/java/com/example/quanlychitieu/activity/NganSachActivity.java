package com.example.quanlychitieu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.quanlychitieu.R;
import com.example.quanlychitieu.model.DanhMuc;
import com.example.quanlychitieu.sqlite.DanhMucDAO;
import com.example.quanlychitieu.sqlite.GiaoDichDAO;
import com.example.quanlychitieu.sqlite.NganSachDAO;
import com.example.quanlychitieu.util.FormatUtil;

import java.util.List;

public class NganSachActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText etBudgetAmount;
    private TextView tvDaChi, tvConLai;
    private Button btnUpdateBudget;
    private LinearLayout llBudgetCategories;

    private NganSachDAO nganSachDAO;
    private GiaoDichDAO giaoDichDAO;
    private DanhMucDAO danhMucDAO;
    private String currentThang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngan_sach);

        nganSachDAO = new NganSachDAO(this);
        giaoDichDAO = new GiaoDichDAO(this);
        danhMucDAO = new DanhMucDAO(this);
        currentThang = FormatUtil.getCurrentThang();

        initViews();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etBudgetAmount = findViewById(R.id.etBudgetAmount);
        tvDaChi = findViewById(R.id.tvDaChiBudget);
        tvConLai = findViewById(R.id.tvConLaiBudgetDetail);
        btnUpdateBudget = findViewById(R.id.btnUpdateBudget);
        llBudgetCategories = findViewById(R.id.llBudgetCategories);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Format amount while typing
        etBudgetAmount.addTextChangedListener(new TextWatcher() {
            boolean isEditing = false;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isEditing) return;
                isEditing = true;
                String raw = normalizeMoneyInput(s.toString());
                if (!raw.isEmpty()) {
                    try {
                        long val = Long.parseLong(raw);
                        String formatted = formatBudgetInput(val);
                        etBudgetAmount.setText(formatted);
                        etBudgetAmount.setSelection(formatted.length());
                    } catch (NumberFormatException ignored) {}
                }
                isEditing = false;
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        btnUpdateBudget.setOnClickListener(v -> saveBudget());
    }

    private void loadData() {
        double hanMuc = nganSachDAO.getHanMuc(currentThang);
        double daChi = giaoDichDAO.getTongByLoaiAndThang("chi", currentThang);
        double conLai = hanMuc - daChi;

        // Display budget amount in edit field
        if (hanMuc > 0) {
            String formatted = formatBudgetInput((long) hanMuc);
            etBudgetAmount.setText(formatted);
            etBudgetAmount.setSelection(etBudgetAmount.getText().length());
        } else {
            etBudgetAmount.setText("");
        }

        if (tvDaChi != null) {
            tvDaChi.setText(FormatUtil.formatTien(daChi));
        }
        if (tvConLai != null) {
            tvConLai.setText(conLai >= 0
                    ? FormatUtil.formatTien(conLai)
                    : "Vượt " + FormatUtil.formatTien(Math.abs(conLai)));
        }
        loadBudgetCategories(hanMuc);
    }

    private void loadBudgetCategories(double hanMuc) {
        if (llBudgetCategories == null) return;
        while (llBudgetCategories.getChildCount() > 1) {
            llBudgetCategories.removeViewAt(1);
        }

        List<DanhMuc> list = danhMucDAO.getByLoai("chi");
        for (DanhMuc dm : list) {
            double daChi = giaoDichDAO.getTongChiByDanhMucAndThang(dm.getId(), currentThang);
            if (daChi > 0 || hanMuc > 0) {
                addBudgetCategoryRow(dm, daChi, hanMuc);
            }
        }

        TextView addRow = new TextView(this);
        addRow.setText("+ Thêm danh mục");
        addRow.setTextColor(ContextCompat.getColor(this, R.color.primary));
        addRow.setTextSize(16);
        addRow.setTypeface(null, android.graphics.Typeface.BOLD);
        addRow.setGravity(Gravity.CENTER_VERTICAL);
        addRow.setPadding(dp(12), dp(14), dp(12), dp(14));
        addRow.setBackgroundResource(R.drawable.bg_category_add);
        addRow.setOnClickListener(v -> startActivity(new Intent(this, DanhMucActivity.class)));
        llBudgetCategories.addView(addRow);
    }

    private void addBudgetCategoryRow(DanhMuc dm, double daChi, double hanMuc) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackgroundResource(R.drawable.bg_transaction_item);
        row.setPadding(dp(12), dp(12), dp(12), dp(12));

        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.bottomMargin = dp(12);
        row.setLayoutParams(rowParams);

        ImageView icon = new ImageView(this);
        icon.setLayoutParams(new LinearLayout.LayoutParams(dp(40), dp(40)));
        icon.setBackgroundResource(R.drawable.bg_icon_circle);
        icon.setPadding(dp(8), dp(8), dp(8), dp(8));
        icon.setImageResource(android.R.drawable.ic_menu_gallery);
        icon.setColorFilter(ContextCompat.getColor(this, R.color.on_surface_variant));
        row.addView(icon);

        TextView name = new TextView(this);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        nameParams.setMarginStart(dp(12));
        name.setLayoutParams(nameParams);
        name.setText(dm.getTen() + " - " + FormatUtil.formatTien(daChi));
        name.setTextColor(ContextCompat.getColor(this, R.color.on_surface));
        name.setTextSize(16);
        row.addView(name);

        TextView percent = new TextView(this);
        int value = hanMuc > 0 ? (int) Math.round((daChi / hanMuc) * 100) : 0;
        percent.setText(value + "%");
        percent.setTextColor(ContextCompat.getColor(this, R.color.on_surface_variant));
        percent.setTextSize(16);
        row.addView(percent);

        llBudgetCategories.addView(row);
    }

    private void saveBudget() {
        String raw = normalizeMoneyInput(etBudgetAmount.getText().toString());
        if (raw.isEmpty()) {
            etBudgetAmount.setError("Vui lòng nhập hạn mức");
            etBudgetAmount.requestFocus();
            return;
        }

        double hanMuc;
        try {
            hanMuc = Long.parseLong(raw);
        } catch (NumberFormatException e) {
            etBudgetAmount.setError("Hạn mức không hợp lệ");
            return;
        }

        if (hanMuc <= 0) {
            etBudgetAmount.setError("Hạn mức phải lớn hơn 0");
            return;
        }

        nganSachDAO.upsert(currentThang, hanMuc);
        Toast.makeText(this, "Đã cập nhật ngân sách " + FormatUtil.formatTien(hanMuc), Toast.LENGTH_SHORT).show();
        loadData();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private String normalizeMoneyInput(String value) {
        return value == null ? "" : value.replaceAll("[^0-9]", "");
    }

    private String formatBudgetInput(long value) {
        return String.format("%,d", value).replace(',', '.');
    }
}
