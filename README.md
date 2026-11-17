# Quản lý chi tiêu

Ứng dụng Android viết bằng Java để ghi thu chi, quản lý danh mục và theo dõi ngân sách theo tháng. Dữ liệu lưu trên thiết bị bằng SQLite (`quan_ly_chi_tieu.db`).

## Cài đặt và chạy

1. Cài [Android Studio](https://developer.android.com/studio) tương thích với [Android Gradle Plugin 9.1.1](https://developer.android.com/build/releases/agp-9-1-0-release-notes). Cài **Android SDK Platform 36.1** và **Android SDK Build-Tools 36.0.0** trong **SDK Manager**.
2. Cấu hình **Gradle JDK** trong Android Studio hoặc đặt `JAVA_HOME` trỏ đến **JDK 21**, theo cấu hình Gradle của dự án.
3. Tải mã nguồn và mở thư mục `QuanLyChiTieu` trong Android Studio.

   ```powershell
   git clone https://github.com/hoaian-png/QuanLyChiTieu.git
   cd QuanLyChiTieu
   ```

4. Chờ Gradle sync. Chọn cấu hình **app**, chọn máy ảo hoặc điện thoại chạy **Android 8.0 (API 26)** trở lên, rồi bấm **Run**. Với điện thoại kết nối qua USB, bật USB debugging.

Build APK debug từ PowerShell sau khi đã cấu hình JDK và SDK:

```powershell
.\gradlew.bat :app:assembleDebug
```

APK nằm tại `app/build/outputs/apk/debug/app-debug.apk`.

## Cách sử dụng

1. Đăng nhập bằng tài khoản demo tích hợp `admin@gmail.com`, mật khẩu `123`.
2. Tại **Trang chủ**, bấm **+** để thêm giao dịch. Chọn **Thu nhập** hoặc **Chi tiêu**, nhập số tiền, chọn danh mục và ngày, rồi lưu.
3. Dùng bộ chọn tháng và bộ lọc **Tất cả**, **Thu nhập**, **Chi tiêu** để xem lại giao dịch. Nhấn giữ giao dịch để mở hộp thoại xóa.
4. Mở **Danh mục** để thêm, sửa hoặc xóa danh mục. Mở **Ngân sách**, nhập hạn mức tháng và bấm **Cập nhật ngân sách** để theo dõi số đã chi và số còn lại.
