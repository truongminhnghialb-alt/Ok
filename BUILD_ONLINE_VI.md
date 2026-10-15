# Hướng dẫn build APK online (không cài IDE)

File này là mã nguồn, không phải APK. Workflow có sẵn để GitHub Actions thử biên dịch APK trên máy chủ.

1. Trên trình duyệt, đăng nhập GitHub và tạo một repository mới (Public hoặc Private).
2. Tải toàn bộ nội dung thư mục này lên repository, giữ nguyên cấu trúc, bao gồm thư mục ẩn `.github/workflows/build-apk.yml`.
3. Mở tab Actions của repository, chọn `Build Hive Macro Pro APK`, bấm `Run workflow` nếu workflow chưa tự chạy.
4. Khi workflow chạy xong thành công, mở lần chạy đó và tải artifact `HiveMacroPro-debug-apk`.
5. Giải nén artifact đã tải; file `app-debug.apk` là APK debug để cài thử.

Nếu bước build báo lỗi, APK chưa được tạo; cần sửa lỗi trước khi thử lại.

Lưu ý: biên dịch thành công chỉ tạo APK từ các tính năng hiện có. Bản này chưa có logic hoàn chỉnh tự phát hiện túi phấn đầy, tự quay về tổ, OCR nhiệm vụ hay tự hoàn thành quest. Không cấp quyền Trợ năng cho APK không rõ nguồn gốc.
