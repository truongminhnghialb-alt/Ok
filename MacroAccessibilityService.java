package com.hivemacro.pro;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

public class MacroAccessibilityService extends AccessibilityService {
    public static volatile boolean isEnabled = false;
    public static volatile boolean macroRunning = false;
    private static volatile MacroAccessibilityService instance;

    public static void setMacroRunning(boolean running) { macroRunning = running; }
    public static boolean performJoystickMove(String direction, long durationMs) {
        MacroAccessibilityService s = instance;
        if (s == null || !isEnabled) return false;
        return s.dispatchJoystickMove(direction, durationMs);
    }

    @Override public void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        isEnabled = true;
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) { }
    @Override public void onInterrupt() { macroRunning = false; }

    private boolean dispatchJoystickMove(String direction, long durationMs) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealMetrics(dm);
        float w = dm.widthPixels, h = dm.heightPixels;
        // Common Roblox mobile joystick area. Screen layouts vary; this is a basic prototype.
        float cx = w * 0.135f, cy = h * 0.775f;
        float dx = 0, dy = 0;
        if ("UP".equals(direction)) dy = -h * 0.055f;
        else if ("DOWN".equals(direction)) dy = h * 0.055f;
        else if ("LEFT".equals(direction)) dx = -w * 0.055f;
        else if ("RIGHT".equals(direction)) dx = w * 0.055f;
        else return false;
        Path path = new Path();
        path.moveTo(cx, cy);
        path.lineTo(cx + dx, cy + dy);
        GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(path, 0, Math.max(400, durationMs));
        GestureDescription gesture = new GestureDescription.Builder().addStroke(stroke).build();
        return dispatchGesture(gesture, null, null);
    }

    @Override public void onDestroy() {
        isEnabled = false;
        macroRunning = false;
        instance = null;
        super.onDestroy();
    }
}
