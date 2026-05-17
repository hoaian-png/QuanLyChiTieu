package com.example.quanlychitieu;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.quanlychitieu.dao.DanhMucDAO;
import com.example.quanlychitieu.model.DanhMuc;

import java.util.List;

public class DanhMucActivity extends AppCompatActivity {

    private EditText etAddCategory;
    private ImageButton btnAddCategory;
    private LinearLayout llCategories;
    private LinearLayout navHome, navDanhMuc, navNganSach;

    private DanhMucDAO danhMucDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_muc);

        danhMucDAO = new DanhMucDAO(this);

        initViews();
        setupAddCategory();
        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
    }

    private void initViews() {
        etAddCategory = findViewById(R.id.etAddCategory);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        llCategories = findViewById(R.id.llCategories);
        navHome = findViewById(R.id.navHome);
        navDanhMuc = findViewById(R.id.navDanhMuc);
        navNganSach = findViewById(R.id.navNganSach);
    }

    private void setupAddCategory() {
        btnAddCategory.setOnClickListener(v -> {
            String ten = etAddCategory.getText().toString().trim();
            if (ten.isEmpty()) {
                etAddCategory.setError("Vui lòng nhập tên danh mục");
                etAddCategory.requestFocus();
                return;
            }
            DanhMuc dm = new DanhMuc(ten);
            long result = danhMucDAO.insert(dm);
            if (result > 0) {
                etAddCategory.setText("");
                loadCategories();
                Toast.makeText(this, "Đã thêm danh mục \"" + ten + "\"", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi khi thêm danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNav() {
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(this, TongQuanActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }
        if (navDanhMuc != null) {
            navDanhMuc.setOnClickListener(v -> { /* Already here */ });
        }
        if (navNganSach != null) {
            navNganSach.setOnClickListener(v -> {
                startActivity(new Intent(this, NganSachActivity.class));
            });
        }
    }

    private void loadCategories() {
        List<DanhMuc> list = danhMucDAO.getAll();
        llCategories.removeAllViews();

        if (list.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Chưa có danh mục nào. Hãy thêm mới!");
            tvEmpty.setTextColor(ContextCompat.getColor(this, R.color.on_surface_variant));
            tvEmpty.setGravity(Gravity.CENTER);
            tvEmpty.setPadding(0, dp(32), 0, dp(32));
            llCategories.addView(tvEmpty);
            return;
        }

        for (DanhMuc dm : list) {
            addCategoryRow(dm);
        }
    }

    private void addCategoryRow(DanhMuc dm) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackgroundResource(R.drawable.bg_transaction_item);
        row.setClickable(true);
        row.setFocusable(true);
        int pad = dp(16);
        row.setPadding(pad, pad, pad, pad);

        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.bottomMargin = dp(8);
        row.setLayoutParams(rowParams);

        // Icon
        ImageView icon = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(48), dp(48));
        icon.setLayoutParams(iconParams);
        icon.setBackgroundResource(R.drawable.bg_icon_circle);
        icon.setPadding(dp(12), dp(12), dp(12), dp(12));
        icon.setImageResource(android.R.drawable.ic_menu_agenda);
        icon.setColorFilter(ContextCompat.getColor(this, R.color.on_surface_variant));
        row.addView(icon);

        // Name
        TextView tvName = new TextView(this);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        nameParams.setMarginStart(dp(16));
        tvName.setLayoutParams(nameParams);
        tvName.setText(dm.getTen());
        tvName.setTextColor(ContextCompat.getColor(this, R.color.on_surface));
        tvName.setTextSize(18);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        row.addView(tvName);

        // Edit button
        ImageButton btnEdit = new ImageButton(this);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(dp(40), dp(40));
        btnEdit.setLayoutParams(btnParams);
        btnEdit.setBackground(ContextCompat.getDrawable(this, android.R.color.transparent));
        btnEdit.setImageResource(android.R.drawable.ic_menu_edit);
        btnEdit.setColorFilter(ContextCompat.getColor(this, R.color.outline));
        btnEdit.setOnClickListener(v -> showEditDialog(dm));
        row.addView(btnEdit);

        // Delete button
        ImageButton btnDelete = new ImageButton(this);
        btnDelete.setLayoutParams(btnParams);
        btnDelete.setBackground(ContextCompat.getDrawable(this, android.R.color.transparent));
        btnDelete.setImageResource(android.R.drawable.ic_menu_delete);
        btnDelete.setColorFilter(ContextCompat.getColor(this, R.color.error));
        btnDelete.setOnClickListener(v -> showDeleteDialog(dm));
        row.addView(btnDelete);

        llCategories.addView(row);
    }

    private void showEditDialog(DanhMuc dm) {
        EditText input = new EditText(this);
        input.setText(dm.getTen());
        input.setSelection(input.getText().length());
        input.setPadding(dp(16), dp(16), dp(16), dp(16));

        new AlertDialog.Builder(this)
                .setTitle("Sửa danh mục")
                .setView(input)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        dm.setTen(newName);
                        danhMucDAO.update(dm);
                        loadCategories();
                        Toast.makeText(this, "Đã cập nhật!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDeleteDialog(DanhMuc dm) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa danh mục")
                .setMessage("Xóa danh mục \"" + dm.getTen() + "\"? Các giao dịch liên quan sẽ mất liên kết.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    danhMucDAO.delete(dm.getId());
                    loadCategories();
                    Toast.makeText(this, "Đã xóa danh mục!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
