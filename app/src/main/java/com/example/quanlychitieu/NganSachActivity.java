package com.example.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlychitieu.dao.GiaoDichDAO;
import com.example.quanlychitieu.dao.NganSachDAO;
import com.example.quanlychitieu.util.FormatUtil;

public class NganSachActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText etBudgetAmount;
    private TextView tvDaChi, tvConLai;
    private Button btnUpdateBudget;

    private NganSachDAO nganSachDAO;
    private GiaoDichDAO giaoDichDAO;
    private String currentThang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngan_sach);

        nganSachDAO = new NganSachDAO(this);
        giaoDichDAO = new GiaoDichDAO(this);
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
                String raw = s.toString().replace(".", "").replace(",", "");
                if (!raw.isEmpty()) {
                    try {
                        long val = Long.parseLong(raw);
                        // Format with dots: e.g. 10000000 → "10.000.000"
                        String formatted = String.format("%,d", val).replace(',', '.');
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
            String formatted = String.format("%,.0f", hanMuc).replace(',', '.');
            etBudgetAmount.setText(formatted);
            etBudgetAmount.setSelection(etBudgetAmount.getText().length());
        }

        if (tvDaChi != null) {
            tvDaChi.setText(FormatUtil.formatTien(daChi));
        }
        if (tvConLai != null) {
            tvConLai.setText(FormatUtil.formatTien(Math.max(0, conLai)));
        }
    }

    private void saveBudget() {
        String raw = etBudgetAmount.getText().toString().replace(".", "").replace(",", "").trim();
        if (raw.isEmpty()) {
            etBudgetAmount.setError("Vui lòng nhập hạn mức");
            etBudgetAmount.requestFocus();
            return;
        }

        double hanMuc;
        try {
            hanMuc = Double.parseDouble(raw);
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
}
