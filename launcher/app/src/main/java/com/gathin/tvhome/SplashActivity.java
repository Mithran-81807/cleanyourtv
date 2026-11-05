package com.gathin.tvhome;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Black screen with a credit line, shown on the first Home after boot and after every wake-up. */
public class SplashActivity extends Activity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable closeLater = this::close;
    private FrameLayout root;
    private boolean closing;

    private int dp(float v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    private Typeface font(String asset, String fallback) {
        try {
            return Typeface.createFromAsset(getAssets(), "fonts/" + asset);
        } catch (Exception e) {
            return Typeface.create(fallback, Typeface.NORMAL);
        }
    }

    private TextView line(String text, int color, float sp, Typeface face, float spacing) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextColor(color);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        t.setTypeface(face);
        t.setLetterSpacing(spacing);
        t.setGravity(Gravity.CENTER);
        t.setSingleLine(true);
        t.setMaxLines(1);
        t.setEllipsize(null);
        t.setPadding(dp(24), dp(6), dp(24), dp(6));
        return t;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);
        root.setClipChildren(false);

        Typeface script = font("script.ttf", "sans-serif-thin");
        Typeface title = font("title.ttf", "serif");

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER_HORIZONTAL);
        box.setClipChildren(false);

        LinearLayout.LayoutParams full = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView line1 = line("Crafted by", 0xE6FFFFFF, 52, script, 0f);
        box.addView(line1, full);

        TextView line2 = line("MITHRAN", Color.WHITE, 56, title, 0.18f);
        line2.setShadowLayer(dp(16), 0, 0, 0x88FFFFFF);
        box.addView(line2, full);

        View divider = new View(this);
        divider.setBackgroundColor(0x66FFFFFF);
        LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(dp(90), Math.max(1, dp(1)));
        dlp.gravity = Gravity.CENTER_HORIZONTAL;
        dlp.topMargin = dp(20);
        dlp.bottomMargin = dp(14);
        box.addView(divider, dlp);

        TextView line3 = line("WELCOME HOME", 0x99FFFFFF, 18, title, 0.3f);
        box.addView(line3, full);

        root.addView(box, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        setContentView(root);

        reveal(line1, 0);
        reveal(line2, 450);
        reveal(divider, 900);
        reveal(line3, 1100);
        handler.postDelayed(closeLater, 3800);
    }

    private void reveal(View v, long delay) {
        v.setAlpha(0f);
        v.setTranslationY(dp(10));
        v.animate().alpha(1f).translationY(0f).setStartDelay(delay).setDuration(700).start();
    }

    private void close() {
        if (closing) return;
        closing = true;
        root.animate().alpha(0f).setDuration(500).withEndAction(this::finish).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        close();
        return true;
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(closeLater);
        super.onDestroy();
    }
}
