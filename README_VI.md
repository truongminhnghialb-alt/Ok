# Hive Macro Pro Android — bản thử nghiệm điều khiển

## Bản này có gì?
- Ứng dụng Android Java với lựa chọn Field/chế độ và nhật ký.
- Dịch vụ Trợ năng có khả năng gửi cử chỉ vuốt.
- Bảng nổi Start/Stop có thể kéo trên màn hình.
- Khi Start, bản thử nghiệm lặp một mẫu di chuyển joystick (lên/phải/xuống/trái) ở vùng joystick thường gặp trên Roblox mobile.

## Những gì CHƯA có
- Chưa nhận diện chính xác Field hoặc tự đi đến Field đã chọn.
- Chưa phát hiện túi pollen đầy, chưa tự tìm đường về tổ/chuyển mật/quay lại Field.
- Chưa có OCR hoặc Auto Quest.
- Vị trí joystick dùng tọa độ tương đối phổ biến; có thể không khớp mọi độ phân giải/giao diện. Hãy thử ở nơi an toàn và dừng nếu nhân vật di chuyển sai.
- Đây là mã nguồn, không phải APK đã biên dịch.

## Quyền cần bật trên điện thoại
1. Cài APK sau khi build.
2. Mở Hive Macro Pro > Mở cài đặt Trợ năng > bật dịch vụ Hive Macro Pro.
3. Cấp quyền "Hiển thị trên ứng dụng khác" khi Android yêu cầu.
4. Mở Roblox, vào game, rồi mở bảng nổi và bấm START. STOP để dừng.

## Build
Mở thư mục này trong Android IDE tương thích với Android Gradle Plugin 8.7.3, JDK 17 và Android SDK 35. Build task: `:app:assembleDebug`. APK debug đầu ra thường là `app/build/outputs/apk/debug/app-debug.apk`.
