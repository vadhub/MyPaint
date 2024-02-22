package com.abg.mypaint;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.abg.mypaint.brush.BrushType;
import com.abg.mypaint.painview.DrawableOnTouchView;

public class MainActivity extends AppCompatActivity {
    private DrawableOnTouchView drawableOnTouchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout mainFrame = findViewById(R.id.main_frame);
        try {
            drawableOnTouchView = new DrawableOnTouchView(this);
            drawableOnTouchView.setActionListener(new DrawableOnTouchView.OnActionListener() {
                @Override
                public void onCancel() {
                    drawableOnTouchView.setClickable(false);

                }

                @Override
                public void onDone(Bitmap bitmap) {
                    drawableOnTouchView.makeNonClickable(false);
                }

            });

            drawableOnTouchView.setColorChangedListener(new DrawableOnTouchView.OnColorChangedListener() {
                @Override
                public void onColorChanged(int color) {

                }

                @Override
                public void onStrokeWidthChanged(float strokeWidth) {

                }

                @Override
                public void onBrushChanged(int brushType) {

                }

            });
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;

            mainFrame.addView(drawableOnTouchView, params);

            drawableOnTouchView.attachCanvas(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}