package com.capacitorjs.plugins.haptics;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import com.capacitorjs.plugins.haptics.arguments.HapticsSelectionType;
import com.capacitorjs.plugins.haptics.arguments.HapticsVibrationType;

public class Haptics {

    private Context context;
    private boolean selectionStarted = false;
    private final Vibrator vibrator;

    Haptics(Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vibratorManager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            this.vibrator = vibratorManager.getDefaultVibrator();
        } else {
            this.vibrator = getDeprecatedVibrator(context);
        }
    }

    @SuppressWarnings("deprecation")
    private Vibrator getDeprecatedVibrator(Context context) {
        return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void vibrate(int duration, String effect) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var vibration_effect = VibrationEffect.DEFAULT_AMPLITUDE;
            if (effect == "Click") {
                vibration_effect = VibrationEffect.EFFECT_CLICK;
            } else if (effect == "DoubleClick") {
                vibration_effect = VibrationEffect.EFFECT_DOUBLE_CLICK;
            } else if (effect == "HeavyClick") {
                vibration_effect = VibrationEffect.EFFECT_HEAVY_CLICK;
            } else if (effect == "Tick") {
                vibration_effect = VibrationEffect.EFFECT_TICK;
            }
            vibrator.vibrate(VibrationEffect.createOneShot(duration, vibration_effect));
        } else {
            vibratePre26(duration);
        }
    }

    @SuppressWarnings({ "deprecation" })
    private void vibratePre26(int duration) {
        vibrator.vibrate(duration);
    }

    @SuppressWarnings({ "deprecation" })
    private void vibratePre26(long[] pattern, int repeat) {
        vibrator.vibrate(pattern, repeat);
    }

    public void selectionStart() {
        this.selectionStarted = true;
    }

    public void selectionChanged() {
        if (this.selectionStarted) {
            performHaptics(new HapticsSelectionType());
        }
    }

    public void selectionEnd() {
        this.selectionStarted = false;
    }

    public void performHaptics(HapticsVibrationType type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(type.getTimings(), type.getAmplitudes(), -1));
        } else {
            vibratePre26(type.getOldSDKPattern(), -1);
        }
    }
}
