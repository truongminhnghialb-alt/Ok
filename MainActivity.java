package com.hivemacro.pro;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private LinearLayout root, logsBox;
    private Spinner fieldSpinner, modeSpinner;
    private TextView statusText, serviceText;
    private boolean running = false;
    private final String[] fields = {
        "Sunflower Field", "Dandelion Field", "Mushroom Field", "Blue Flower Field",
        "Strawberry Field", "Spider Field", "Bamboo Field", "Pineapple Patch",
        "Pumpkin Patch", "Pine Tree Forest", "Coconut Field", "Mountain Top Field"
    };

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(16, 27, 24));
        getWindow().setNavigationBarColor(Color.rgb(16, 27, 24));
        buildUi();
        addLog("Hive Macro Pro đã khởi động.");
        addLog("Bản 0.2: có bảng nổi và thử nghiệm điều khiển joystick theo mẫu.");
    }

    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density); }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setPadding(0, dp(5), 0, dp(5));
        return t;
    }

    private LinearLayout section() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(dp(14), dp(12), dp(14), dp(12));
        l.setBackgroundColor(Color.rgb(27, 42, 36));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(7), 0, dp(7));
        root.addView(l, p);
        return l;
    }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextSize(15);
        return b;
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(Color.rgb(16, 27, 24));
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(20));
        scroll.addView(root);
        setContentView(scroll);

        TextView title = text("🐝 HIVE MACRO PRO", 25, Color.rgb(225, 248, 216), true);
        root.addView(title);
        root.addView(text("BEE SWARM SIMULATOR • ANDROID • BẢN 0.1", 12, Color.LTGRAY, false));

        LinearLayout status = section();
        statusText = text("● STANDBY", 19, Color.rgb(185, 243, 151), true);
        status.addView(statusText);
        serviceText = text("Dịch vụ Trợ năng: chưa xác minh", 14, Color.LTGRAY, false);
        status.addView(serviceText);
        Button accessibility = button("Mở cài đặt Trợ năng");
        accessibility.setOnClickListener(v -> {
            addLog("Mở cài đặt Trợ năng Android.");
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        });
        status.addView(accessibility);
        Button refresh = button("Kiểm tra trạng thái dịch vụ");
        refresh.setOnClickListener(v -> refreshServiceStatus());
        status.addView(refresh);

        LinearLayout config = section();
        config.addView(text("1. CHỌN FIELD", 17, Color.WHITE, true));
        fieldSpinner = new Spinner(this);
        fieldSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, fields));
        config.addView(fieldSpinner);
        config.addView(text("2. CHẾ ĐỘ", 17, Color.WHITE, true));
        modeSpinner = new Spinner(this);
        modeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
            new String[]{"Auto Farm + tự về tổ (dự kiến)", "Auto Quest (đang phát triển)", "Farm thủ công có hỗ trợ"}));
        config.addView(modeSpinner);
        config.addView(text("Bản thử nghiệm hiện chỉ gửi thao tác joystick theo mẫu. Chưa tự nhận diện Field, túi phấn đầy, tổ ong hoặc nhiệm vụ.", 13, Color.LTGRAY, false));

        LinearLayout controls = section();
        controls.addView(text("3. ĐIỀU KHIỂN", 17, Color.WHITE, true));
        Button start = button("▶  MỞ BẢNG NỔI MACRO");
        start.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(104, 160, 76)));
        start.setTextColor(Color.WHITE);
        start.setOnClickListener(v -> {
            if (!MacroAccessibilityService.isEnabled) {
                Toast.makeText(this, "Hãy bật dịch vụ Trợ năng của Hive Macro Pro trước.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                addLog("Cần bật dịch vụ Trợ năng để gửi thao tác chạm.");
                return;
            }
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Cấp quyền hiển thị trên ứng dụng khác rồi quay lại.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
                return;
            }
            Intent i = new Intent(this, FloatingMacroService.class);
            i.putExtra("field", String.valueOf(fieldSpinner.getSelectedItem()));
            startService(i);
            statusText.setText("● BẢNG NỔI ĐÃ MỞ");
            addLog("Đã yêu cầu mở bảng nổi • Field đã chọn: " + fieldSpinner.getSelectedItem());
            addLog("Macro thử nghiệm chỉ di chuyển joystick theo mẫu; chưa có nhận diện pollen/OCR.");
        });
        controls.addView(start);
        Button stop = button("■  STOP MACRO");
        controls.addView(stop);
        stop.setOnClickListener(v -> {
            running = false;
            MacroAccessibilityService.setMacroRunning(false);
            stopService(new Intent(this, FloatingMacroService.class));
            statusText.setText("● STANDBY");
            addLog("Đã yêu cầu dừng và đóng bảng nổi.");
        });

        LinearLayout logSection = section();
        logSection.addView(text("4. NHẬT KÝ", 17, Color.WHITE, true));
        Button clear = button("Xóa nhật ký");
        clear.setOnClickListener(v -> {
            logsBox.removeAllViews();
            addLog("Nhật ký đã được xóa.");
        });
        logSection.addView(clear);
        logsBox = new LinearLayout(this);
        logsBox.setOrientation(LinearLayout.VERTICAL);
        logSection.addView(logsBox);

        root.addView(text("LƯU Ý: Bản thử nghiệm có bảng nổi và thao tác joystick theo mẫu. Chưa tự tìm đường đến Field, phát hiện túi đầy, tự về tổ/chuyển mật hoặc đọc nhiệm vụ bằng OCR.", 12, Color.rgb(240, 200, 120), false));
    }

    private void refreshServiceStatus() {
        boolean enabled = MacroAccessibilityService.isEnabled;
        serviceText.setText(enabled ? "Dịch vụ Trợ năng: ĐÃ KẾT NỐI" : "Dịch vụ Trợ năng: chưa bật");
        addLog(enabled ? "Trợ năng đã kết nối." : "Trợ năng chưa kết nối; bật trong cài đặt Android.");
    }

    private void addLog(String message) {
        if (logsBox == null) return;
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        TextView line = text(time + "  " + message, 12, Color.rgb(193, 211, 196), false);
        logsBox.addView(line, 0);
        while (logsBox.getChildCount() > 40) logsBox.removeViewAt(logsBox.getChildCount() - 1);
    }

    @Override protected void onResume() {
        super.onResume();
        if (serviceText != null) refreshServiceStatus();
    }
}
