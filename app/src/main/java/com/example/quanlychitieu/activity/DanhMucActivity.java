package com.example.quanlychitieu.activity;

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

import com.example.quanlychitieu.R;
import com.example.quanlychitieu.model.DanhMuc;
import com.example.quanlychitieu.sqlite.DanhMucDAO;

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
            showAddLoaiDialog(ten);
        });
    }

    private void showAddLoaiDialog(String ten) {
        String[] labels = {"Chi tiêu", "Thu nhập"};
        String[] values = {"chi", "thu"};
        new AlertDialog.Builder(this)
                .setTitle("Chọn loại danh mục")
                .setItems(labels, (dialog, which) -> {
                    long result = danhMucDAO.insert(new DanhMuc(ten, values[which]));
                    if (result > 0) {
                        etAddCategory.setText("");
                        loadCategories();
                        Toast.makeText(this, "Đã thêm danh mục \"" + ten + "\"", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Lỗi khi thêm danh mục", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void setupBottomNav() {
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, TongQuanActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
        navDanhMuc.setOnClickListener(v -> {});
        navNganSach.setOnClickListener(v -> startActivity(new Intent(this, NganSachActivity.class)));
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

        ImageView icon = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(48), dp(48));
        icon.setLayoutParams(iconParams);
        icon.setBackgroundResource(R.drawable.bg_icon_circle);
        icon.setPadding(dp(12), dp(12), dp(12), dp(12));
        icon.setImageResource("thu".equals(dm.getLoai()) ? android.R.drawable.ic_menu_upload : android.R.drawable.ic_menu_agenda);
        icon.setColorFilter(ContextCompat.getColor(this,
                "thu".equals(dm.getLoai()) ? R.color.primary_container : R.color.on_surface_variant));
        row.addView(icon);

        LinearLayout textBlock = new LinearLayout(this);
        textBlock.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        textParams.setMarginStart(dp(16));
        textBlock.setLayoutParams(textParams);

        TextView tvName = new TextView(this);
        tvName.setText(dm.getTen());
        tvName.setTextColor(ContextCompat.getColor(this, R.color.on_surface));
        tvName.setTextSize(18);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        textBlock.addView(tvName);

        TextView tvLoai = new TextView(this);
        tvLoai.setText("thu".equals(dm.getLoai()) ? "Thu nhập" : "Chi tiêu");
        tvLoai.setTextColor(ContextCompat.getColor(this,
                "thu".equals(dm.getLoai()) ? R.color.primary_container : R.color.error));
        tvLoai.setTextSize(12);
        textBlock.addView(tvLoai);
        row.addView(textBlock);

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(dp(40), dp(40));

        ImageButton btnEdit = new ImageButton(this);
        btnEdit.setLayoutParams(btnParams);
        btnEdit.setBackground(ContextCompat.getDrawable(this, android.R.color.transparent));
        btnEdit.setImageResource(android.R.drawable.ic_menu_edit);
        btnEdit.setColorFilter(ContextCompat.getColor(this, R.color.outline));
        btnEdit.setOnClickListener(v -> showEditDialog(dm));
        row.addView(btnEdit);

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

        String[] labels = {"Chi tiêu", "Thu nhập"};
        String[] values = {"chi", "thu"};
        int checked = "thu".equals(dm.getLoai()) ? 1 : 0;

        new AlertDialog.Builder(this)
                .setTitle("Sửa danh mục")
                .setView(input)
                .setSingleChoiceItems(labels, checked, (dialog, which) -> dm.setLoai(values[which]))
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
        int count = danhMucDAO.countTransactions(dm.getId());
        new AlertDialog.Builder(this)
                .setTitle("Xóa danh mục")
                .setMessage("Xóa danh mục \"" + dm.getTen() + "\"? " +
                        count + " giao dịch liên quan sẽ chuyển sang không có danh mục.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int rows = danhMucDAO.delete(dm.getId());
                    loadCategories();
                    Toast.makeText(this,
                            rows > 0 ? "Đã xóa danh mục!" : "Không xóa được danh mục",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
