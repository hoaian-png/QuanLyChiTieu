package com.example.quanlychitieu.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.quanlychitieu.R;
import com.example.quanlychitieu.model.DanhMuc;
import com.example.quanlychitieu.model.GiaoDich;
import com.example.quanlychitieu.sqlite.DanhMucDAO;
import com.example.quanlychitieu.sqlite.GiaoDichDAO;
import com.example.quanlychitieu.util.FormatUtil;

import java.util.Calendar;
import java.util.List;

public class ThemGiaoDichActivity extends AppCompatActivity {

    private TextView btnExpense, btnIncome;
    private EditText etAmount;
    private GridLayout gridCategories;
    private TextView tvSelectedDate;
    private EditText etNote;
    private Button btnCancel, btnSave;
    private ImageButton btnClose;

    private GiaoDichDAO giaoDichDAO;
    private DanhMucDAO danhMucDAO;

    private String currentLoai = "chi";
    private int selectedDanhMucId = -1;
    private Calendar selectedDate;
    private List<DanhMuc> danhMucList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_giao_dich);

        giaoDichDAO = new GiaoDichDAO(this);
        danhMucDAO = new DanhMucDAO(this);
        selectedDate = Calendar.getInstance();

        initViews();
        setupTypeToggle();
        loadCategories();
        setupDatePicker();
        setupButtons();
    }

    private void initViews() {
        btnExpense = findViewById(R.id.btnExpense);
        btnIncome = findViewById(R.id.btnIncome);
        etAmount = findViewById(R.id.etAmount);
        gridCategories = findViewById(R.id.gridCategories);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        etNote = findViewById(R.id.etNote);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
        btnClose = findViewById(R.id.btnClose);
    }

    private void setupTypeToggle() {
        btnExpense.setOnClickListener(v -> setLoai("chi"));
        btnIncome.setOnClickListener(v -> setLoai("thu"));
        // Default: chi
        setLoai("chi");
    }

    private void setLoai(String loai) {
        currentLoai = loai;
        if ("chi".equals(loai)) {
            btnExpense.setBackgroundResource(R.drawable.bg_pill_active);
            btnExpense.setTextColor(ContextCompat.getColor(this, R.color.on_surface));
            btnIncome.setBackgroundResource(android.R.color.transparent);
            btnIncome.setTextColor(ContextCompat.getColor(this, R.color.on_surface_variant));

            // Amount color: red for expense
            etAmount.setTextColor(ContextCompat.getColor(this, R.color.error));
        } else {
            btnIncome.setBackgroundResource(R.drawable.bg_pill_active);
            btnIncome.setTextColor(ContextCompat.getColor(this, R.color.on_surface));
            btnExpense.setBackgroundResource(android.R.color.transparent);
            btnExpense.setTextColor(ContextCompat.getColor(this, R.color.on_surface_variant));

            // Amount color: green for income
            etAmount.setTextColor(ContextCompat.getColor(this, R.color.primary_container));
        }
        if (danhMucDAO != null && gridCategories != null) {
            loadCategories();
        }
    }

    private void loadCategories() {
        danhMucList = danhMucDAO.getByLoai(currentLoai);
        selectedDanhMucId = -1;
        gridCategories.removeAllViews();

        for (int i = 0; i < danhMucList.size(); i++) {
            DanhMuc dm = danhMucList.get(i);
            addCategoryCell(dm, i == 0); // first selected by default
        }
        addAddCategoryCell();

        if (!danhMucList.isEmpty()) {
            selectedDanhMucId = danhMucList.get(0).getId();
        }
    }

    private void addCategoryCell(DanhMuc dm, boolean isSelected) {
        LinearLayout cell = new LinearLayout(this);
        cell.setOrientation(LinearLayout.VERTICAL);
        cell.setGravity(android.view.Gravity.CENTER);
        cell.setPadding(dp(8), dp(12), dp(8), dp(12));
        cell.setBackgroundResource(isSelected ? R.drawable.bg_category_active : R.drawable.bg_category_inactive);

        GridLayout.LayoutParams cellParams = new GridLayout.LayoutParams();
        cellParams.width = 0;
        cellParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        cellParams.setMargins(dp(4), dp(4), dp(4), dp(4));
        cell.setLayoutParams(cellParams);

        // Icon
        ImageView icon = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(36), dp(36));
        icon.setLayoutParams(iconParams);
        icon.setBackgroundResource(R.drawable.bg_icon_circle);
        icon.setPadding(dp(8), dp(8), dp(8), dp(8));
        icon.setImageResource(android.R.drawable.ic_menu_gallery);
        if (isSelected) {
            icon.setColorFilter(ContextCompat.getColor(this, R.color.primary_container));
        } else {
            icon.setColorFilter(ContextCompat.getColor(this, R.color.on_surface_variant));
        }
        cell.addView(icon);

        // Name
        TextView tvName = new TextView(this);
        tvName.setText(dm.getTen());
        tvName.setTextSize(11);
        tvName.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nameParams.topMargin = dp(6);
        tvName.setLayoutParams(nameParams);
        tvName.setTextColor(ContextCompat.getColor(this,
                isSelected ? R.color.primary_fixed : R.color.on_surface_variant));
        cell.addView(tvName);

        // Click listener
        cell.setOnClickListener(v -> {
            selectedDanhMucId = dm.getId();
            refreshCategorySelection(dm.getId());
        });

        gridCategories.addView(cell);
    }

    private void addAddCategoryCell() {
        LinearLayout cell = new LinearLayout(this);
        cell.setOrientation(LinearLayout.VERTICAL);
        cell.setGravity(android.view.Gravity.CENTER);
        cell.setPadding(dp(8), dp(12), dp(8), dp(12));
        cell.setBackgroundResource(R.drawable.bg_category_add);

        GridLayout.LayoutParams cellParams = new GridLayout.LayoutParams();
        cellParams.width = 0;
        cellParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        cellParams.setMargins(dp(4), dp(4), dp(4), dp(4));
        cell.setLayoutParams(cellParams);

        ImageView icon = new ImageView(this);
        icon.setLayoutParams(new LinearLayout.LayoutParams(dp(36), dp(36)));
        icon.setPadding(dp(8), dp(8), dp(8), dp(8));
        icon.setImageResource(android.R.drawable.ic_input_add);
        icon.setColorFilter(ContextCompat.getColor(this, R.color.primary));
        cell.addView(icon);

        TextView tvName = new TextView(this);
        tvName.setText("Thêm loại");
        tvName.setTextSize(11);
        tvName.setGravity(android.view.Gravity.CENTER);
        tvName.setTextColor(ContextCompat.getColor(this, R.color.on_surface_variant));
        cell.addView(tvName);

        cell.setOnClickListener(v -> startActivity(new Intent(this, DanhMucActivity.class)));
        gridCategories.addView(cell);
    }

    private void refreshCategorySelection(int selectedId) {
        for (int i = 0; i < gridCategories.getChildCount(); i++) {
            View child = gridCategories.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout cell = (LinearLayout) child;
                if (i >= danhMucList.size()) {
                    continue;
                }
                DanhMuc dm = danhMucList.get(i);
                boolean isSelected = dm.getId() == selectedId;

                cell.setBackgroundResource(isSelected ? R.drawable.bg_category_active : R.drawable.bg_category_inactive);

                if (cell.getChildCount() >= 2) {
                    // Update icon tint
                    if (cell.getChildAt(0) instanceof ImageView) {
                        ImageView icon = (ImageView) cell.getChildAt(0);
                        icon.setColorFilter(ContextCompat.getColor(this,
                                isSelected ? R.color.primary_container : R.color.on_surface_variant));
                    }
                    // Update text color
                    if (cell.getChildAt(1) instanceof TextView) {
                        TextView tv = (TextView) cell.getChildAt(1);
                        tv.setTextColor(ContextCompat.getColor(this,
                                isSelected ? R.color.primary_fixed : R.color.on_surface_variant));
                    }
                }
            }
        }
    }

    private void setupDatePicker() {
        // Update display
        updateDateDisplay();

        // Clickable date row
        View dateRow = findViewById(R.id.dateRow);
        if (dateRow != null) {
            dateRow.setOnClickListener(v -> showDatePicker());
        }
        if (tvSelectedDate != null) {
            tvSelectedDate.setOnClickListener(v -> showDatePicker());
        }
    }

    private void showDatePicker() {
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            selectedDate.set(y, m, d);
            updateDateDisplay();
        }, year, month, day);
        dialog.show();
    }

    private void updateDateDisplay() {
        if (tvSelectedDate != null) {
            tvSelectedDate.setText(FormatUtil.formatNgay(selectedDate.getTimeInMillis()));
        }
    }

    private void setupButtons() {
        btnCancel.setOnClickListener(v -> finish());
        btnClose.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void saveTransaction() {
        String amountStr = etAmount.getText().toString().trim().replace(".", "").replace(",", "");
        if (amountStr.isEmpty()) {
            etAmount.setError("Vui lòng nhập số tiền");
            etAmount.requestFocus();
            return;
        }

        double soTien;
        try {
            soTien = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            etAmount.setError("Số tiền không hợp lệ");
            return;
        }

        if (soTien <= 0) {
            etAmount.setError("Số tiền phải lớn hơn 0");
            return;
        }

        if (selectedDanhMucId == -1) {
            Toast.makeText(this, "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show();
            return;
        }

        String ghiChu = etNote != null ? etNote.getText().toString().trim() : "";

        GiaoDich gd = new GiaoDich(soTien, currentLoai, selectedDanhMucId,
                selectedDate.getTimeInMillis(), ghiChu);
        long result = giaoDichDAO.insert(gd);

        if (result > 0) {
            Toast.makeText(this, "Đã lưu giao dịch!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi lưu. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
