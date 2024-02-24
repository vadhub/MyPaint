package com.abg.mypaint.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.abg.mypaint.R;
import com.abg.mypaint.Utils;
import com.abg.mypaint.local.Preferences;

public class ColorPicker extends FrameLayout {
    public interface ColorPickerListener {
        void onFinishedColorPicking(int color);
        void onSettingsPressed();
    }

    private ColorPickerListener colorPickerListener;
    private boolean interacting;
    private boolean changingWeight;
    private boolean wasChangingWeight;
    private final OvershootInterpolator interpolator = new OvershootInterpolator(1.02f);

    private static final int[] COLORS = new int[]{
            0xffea2739,
            0xffdb3ad2,
            0xff3051e3,
            0xff49c5ed,
            0xff80c864,
            0xfffcde65,
            0xfffc964d,
            0xff000000,
            0xffffffff
    };

    private static final float[] LOCATIONS = new float[]{
            0.0f,
            0.14f,
            0.24f,
            0.39f,
            0.49f,
            0.62f,
            0.73f,
            0.85f,
            1.0f
    };

    private ImageView settingsButton;

    private final Paint gradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint swatchPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint swatchStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectF = new RectF();

    private float location = 1.0f;
    private float weight = 0.27f;
    private float draggingFactor;
    private boolean dragging;
    private Preferences preferences;

    public ColorPicker(Context context) {
        super(context);
        init(context);
    }

    public ColorPicker(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public ColorPicker(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        init(context);
    }

    void init(Context context) {
        preferences = new Preferences(context);
        setWillNotDraw(false);
        Utils.checkDisplaySize(context, null);
        backgroundPaint.setColor(0xffffffff);
        swatchStrokePaint.setStyle(Paint.Style.STROKE);
        swatchStrokePaint.setStrokeWidth(dp(1));

        settingsButton = new ImageView(context);
        settingsButton.setScaleType(ImageView.ScaleType.CENTER);
        settingsButton.setImageResource(R.drawable.ic_brush);
        addView(settingsButton, new LayoutParams(dp(40), dp(32)));
        settingsButton.setOnClickListener(v -> {
            if (colorPickerListener != null) {
                colorPickerListener.onSettingsPressed();
            }
        });

        location = preferences.getLastColorLocation();
        weight = preferences.getStrokeWidth();
        if (weight != 0.27) weight /= 50.f;
        setLocation(location);

    }

    private int dp(float size) {
        return (int) (size < 0 ? size : Utils.dp(getContext(), size));
    }

    public void setColorPickerListener(ColorPickerListener colorPickerListener) {
        this.colorPickerListener = colorPickerListener;
    }

    public int colorForLocation(float location) {
        if (location <= 0) {
            return COLORS[0];
        } else if (location >= 1) {
            return COLORS[COLORS.length - 1];
        }

        int leftIndex = -1;
        int rightIndex = -1;

        for (int i = 1; i < LOCATIONS.length; i++) {
            float value = LOCATIONS[i];
            if (value > location) {
                leftIndex = i - 1;
                rightIndex = i;
                break;
            }
        }

        float leftLocation = LOCATIONS[leftIndex];
        int leftColor = COLORS[leftIndex];

        float rightLocation = LOCATIONS[rightIndex];
        int rightColor = COLORS[rightIndex];

        float factor = (location - leftLocation) / (rightLocation - leftLocation);
        return interpolateColors(leftColor, rightColor, factor);
    }

    private int interpolateColors(int leftColor, int rightColor, float factor) {
        factor = Math.min(Math.max(factor, 0.0f), 1.0f);

        int r1 = Color.red(leftColor);
        int r2 = Color.red(rightColor);

        int g1 = Color.green(leftColor);
        int g2 = Color.green(rightColor);

        int b1 = Color.blue(leftColor);
        int b2 = Color.blue(rightColor);

        int r = Math.min(255, (int) (r1 + (r2 - r1) * factor));
        int g = Math.min(255, (int) (g1 + (g2 - g1) * factor));
        int b = Math.min(255, (int) (b1 + (b2 - b1) * factor));

        return Color.argb(255, r, g, b);
    }

    public void setLocation(float value) {
        int color = colorForLocation(location = value);
        swatchPaint.setColor(color);

        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);

        if (hsv[0] < 0.001 && hsv[1] < 0.001 && hsv[2] > 0.92f) {
            int c = (int) ((1.0f - (hsv[2] - 0.92f) / 0.08f * 0.22f) * 255);
            swatchStrokePaint.setColor(Color.rgb(c, c, c));
        } else {
            swatchStrokePaint.setColor(color);
        }

        invalidate();
    }

    public void setWeight(float value) {
        weight = value;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            return false;
        }

        float x = event.getX() - rectF.left;
        float y = event.getY() - rectF.top;

        if (!interacting && x < -Utils.dp(getContext(), 10)) {
            return false;
        }

        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            if (interacting && colorPickerListener != null) {
                colorPickerListener.onFinishedColorPicking(colorForLocation(location));
                preferences.saveLastColorLocation(location);
            }
            interacting = false;
            wasChangingWeight = changingWeight;
            changingWeight = false;
            setDragging(false);
        } else if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            if (!interacting) {
                interacting = true;
            }

            float colorLocation = Math.max(0.0f, Math.min(1.0f, y / rectF.height()));
            setLocation(colorLocation);
            setDragging(true);

            if (x < -dp(10)) {
                changingWeight = true;
                float weightLocation = (-x - dp(10)) / dp(190);
                weightLocation = Math.max(0.0f, Math.min(1.0f, weightLocation));
                setWeight(weightLocation);
            }
            return true;
        }
        return false;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;

        int backHeight = getMeasuredHeight() - dp(16) - dp(48);
        gradientPaint.setShader(new LinearGradient(0, dp(16), 0, backHeight + dp(16), COLORS, LOCATIONS, Shader.TileMode.REPEAT));
        int x = width - dp(16) - dp(8);
        int y = dp(16);
        rectF.set(x, y, x + dp(8), y + backHeight);

        settingsButton.layout(width - settingsButton.getMeasuredWidth(), height - dp(32), width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawRoundRect(rectF, dp(6), dp(6), gradientPaint);
        int cx = (int) (rectF.centerX() + draggingFactor * -dp(70));
        int cy = (int) (rectF.top - dp(22) + rectF.height() * location) + dp(22);
        float swatchRadius = (dp(4) + (dp(19) - dp(4))) / 1.5f;
        canvas.drawRect(cx, cy - dp(1), rectF.centerX(), cy + dp(1), swatchStrokePaint);
        canvas.drawCircle(cx, cy, swatchRadius, swatchPaint);
        canvas.drawCircle(cx, cy, swatchRadius - dp(0.5f), swatchStrokePaint);
    }

    private void setDragging(boolean value) {
        if (dragging == value) {
            return;
        }
        dragging = value;
        float target = dragging ? 1.0f : 0.0f;
        Animator a = ObjectAnimator.ofFloat(this, "draggingFactor", draggingFactor, target);
        a.setInterpolator(interpolator);
        int duration = 300;
        if (wasChangingWeight) {
            duration += weight * 75;
        }
        a.setDuration(duration);
        a.start();
    }

    private void setDraggingFactor(float factor) {
        draggingFactor = factor;
        invalidate();
    }
}
