package com.hivemacro.pro;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FloatingMacroService extends Service {
    private WindowManager wm;
    private View panel;
    private WindowManager.LayoutParams params;
    private final Handler handler = new Handler();
    private boolean running = false;
    private int directionIndex = 0;
    private final String[] directions = {"UP", "RIGHT", "DOWN", "LEFT", "UP", "LEFT", "DOWN", "RIGHT"};
    private Runnable loop;

    @Override public IBinder onBind(Intent intent) { return null; }

    @Override public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        buildPanel();
        try {
            params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP | Gravity.END;
            params.x = 12; params.y = 180;
            wm.addView(panel, params);
        } catch (Exception e) {
            Toast.makeText(this, "Không thể hiện bảng nổi. Hãy cấp quyền hiển thị trên ứng dụng khác.", Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }

    private void buildPanel() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(12, 10, 12, 10);
        box.setBackgroundColor(Color.rgb(24, 48, 36));
        TextView title = new TextView(this);
        title.setText("🐝 HIVE MACRO"); title.setTextColor(Color.WHITE); title.setTextSize(14);
        box.addView(title);
        TextView hint = new TextView(this);
        hint.setText("Farm thử nghiệm • joystick"); hint.setTextColor(Color.LTGRAY); hint.setTextSize(10);
        box.addView(hint);
        Button toggle = new Button(this); toggle.setText("▶ START"); toggle.setAllCaps(false);
        toggle.setOnClickListener(v -> {
            if (!MacroAccessibilityService.isEnabled) {
                Toast.makeText(this, "Bật dịch vụ Trợ năng của Hive Macro Pro trước.", Toast.LENGTH_LONG).show(); return;
            }
            running = !running;
            MacroAccessibilityService.setMacroRunning(running);
            toggle.setText(running ? "■ STOP" : "▶ START");
            toggle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(running ? Color.rgb(175,65,55) : Color.rgb(80,150,70)));
            if (running) startLoop(); else handler.removeCallbacksAndMessages(null);
        });
        box.addView(toggle);
        Button close = new Button(this); close.setText("Ẩn bảng nổi"); close.setAllCaps(false);
        close.setOnClickListener(v -> stopSelf()); box.addView(close);
        // Drag panel by long-pressing its title area.
        title.setOnTouchListener(new View.OnTouchListener() {
            float sx, sy; int ox, oy;
            @Override public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) { sx=e.getRawX(); sy=e.getRawY(); ox=params.x; oy=params.y; return true; }
                if (e.getAction() == MotionEvent.ACTION_MOVE) { params.x=ox+(int)(sx-e.getRawX()); params.y=oy+(int)(e.getRawY()-sy); try { wm.updateViewLayout(panel,params); } catch(Exception ignored){} return true; }
                return true;
            }
        });
        panel = box;
    }

    private void startLoop() {
        handler.removeCallbacksAndMessages(null);
        loop = new Runnable() {
            @Override public void run() {
                if (!running || !MacroAccessibilityService.isEnabled) return;
                String d = directions[directionIndex++ % directions.length];
                MacroAccessibilityService.performJoystickMove(d, 1300);
                handler.postDelayed(this, 1550);
            }
        };
        handler.post(loop);
    }

    @Override public void onDestroy() {
        running = false; MacroAccessibilityService.setMacroRunning(false);
        handler.removeCallbacksAndMessages(null);
        if (panel != null) try { wm.removeView(panel); } catch (Exception ignored) { }
        super.onDestroy();
    }
}
