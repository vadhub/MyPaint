package com.abg.mypaint.painview;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abg.mypaint.R;
import com.abg.mypaint.brush.BrushType;
import com.abg.mypaint.color.ColorPicker;

/**
 * Created by INFIi on 1/21/2017.
 * interfaces onActionListener, onColorChangedListener
 */
public class DrawableOnTouchView extends FrameLayout implements View.OnClickListener{

    private ImageButton undo, painterIcon, done, cancel;
    private ShaderTextView normalBrush, neonBrush, innerBrush, blurBrush, embossBrush, debossBrush;
    private ColorPicker colorPicker;
    private FingerPaintView fingerPaintView;
    private LinearLayout selectBrushFrame, strokeWidthFrame, drawActionLayout;
    private FrameLayout undoFrame;
    private SeekBar strokeSeekbar;
    private Context context;
    private TextView strokeWidthStatus;
    private FrameLayout mainFrame;
    private ImageView onDoneIv;
    private boolean controlsHidden = false;
    private FrameLayout canvasFrame;
    private OnActionListener actionListener;
    private OnColorChangedListener colorChangedListener;

    public interface OnActionListener {
        void onCancel();
        void onDone(Bitmap bitmap);
    }

    public interface OnColorChangedListener {
        void onColorChanged(int color);
        void onStrokeWidthChanged(float strokeWidth);
        void onBrushChanged(int brushType);
    }

    public void setActionListener(OnActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setColorChangedListener(final OnColorChangedListener colorChangedListener) {
        this.colorChangedListener = colorChangedListener;
        fingerPaintView.setColorPickerChanged(new FingerPaintView.OnColorPickerChanged() {
            @Override
            public void onColorChanged(int color) {
                DrawableOnTouchView.this.colorChangedListener.onColorChanged(color);
            }

            @Override
            public void onStrokeWidthChanged(float strokeWidth) {
                DrawableOnTouchView.this.colorChangedListener.onStrokeWidthChanged(strokeWidth);
            }

            @Override
            public void onBrushChanged(int brushId) {
                DrawableOnTouchView.this.colorChangedListener.onBrushChanged(brushId);
            }

        });
    }

    public DrawableOnTouchView(Context context) {
        super(context);
        init(context);
    }

    public DrawableOnTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawableOnTouchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }

    private void init(Context context) {
        this.context = context;
        try {
            View layout = View.inflate(context, R.layout.drawable_view_layout, null);
            addView(layout);
            bindViews(layout);
            setClickable();
        } catch (Exception e) {
            Log.e("onInit():", e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void attachCanvas(int width, int height) {
        LayoutParams params = new LayoutParams(width, height, Gravity.CENTER);
        canvasFrame.addView(fingerPaintView, params);

    }

    public void flipOnTouch(boolean isVisible) {
        int v = isVisible ? GONE : VISIBLE;
        colorPicker.setVisibility(v);
        selectBrushFrame.setVisibility(GONE);
        strokeWidthFrame.setVisibility(GONE);
        undoFrame.setVisibility(v);

    }

    private void enableShader(ShaderTextView shaderTextView,int filterId){
        shaderTextView.setFilterId(filterId);
        shaderTextView.setRadius(16);
        shaderTextView.enableMask();
    }

    private void bindViews(View layout) {
        undo = layout.findViewById(R.id.undo_btn);
        undo.setImageResource(R.drawable.ic_undo);
        painterIcon = layout.findViewById(R.id.show_stroke_bar);
        painterIcon.setImageResource(R.drawable.ic_gestures);
        painterIcon.setClickable(false);
        painterIcon.setVisibility(GONE);

        normalBrush = layout.findViewById(R.id.normal_brush);
        normalBrush.setOnClickListener(this);
        enableShader(normalBrush, BrushType.BRUSH_SOLID);

        neonBrush = layout.findViewById(R.id.neon_brush);
        neonBrush.setOnClickListener(this);
        enableShader(neonBrush, BrushType.BRUSH_NEON);

        innerBrush = layout.findViewById(R.id.inner_brush);
        innerBrush.setOnClickListener(this);
        enableShader(innerBrush, BrushType.BRUSH_INNER);

        blurBrush = layout.findViewById(R.id.blur_brush);
        blurBrush.setOnClickListener(this);
        enableShader(blurBrush, BrushType.BRUSH_BLUR);

        embossBrush = layout.findViewById(R.id.emboss_brush);
        embossBrush.setOnClickListener(this);
        enableShader(embossBrush, BrushType.BRUSH_EMBOSS);

        debossBrush = layout.findViewById(R.id.deboss_brush);
        debossBrush.setOnClickListener(this);
        enableShader(debossBrush, BrushType.BRUSH_DEBOSS);

        colorPicker = layout.findViewById(R.id.color_picker);
        canvasFrame = layout.findViewById(R.id.canvas_frame);
        fingerPaintView = new FingerPaintView(context);

        float location = context.getSharedPreferences("paint", Activity.MODE_PRIVATE).getFloat("last_color_location", 0.5f);
        fingerPaintView.setBrushColor(colorPicker.colorForLocation(location));
        fingerPaintView.setBrushStrokeWidth(12.f);

        selectBrushFrame = layout.findViewById(R.id.brush_option_frame);
        strokeWidthFrame = layout.findViewById(R.id.stroke_width_layout);

        strokeSeekbar = layout.findViewById(R.id.stroke_width_seekbar);
        strokeSeekbar.setMax(50);
        strokeSeekbar.setProgress(14);

        strokeWidthStatus = layout.findViewById(R.id.stroke_width_status);

        cancel = layout.findViewById(R.id.draw_canceled);
        cancel.setOnClickListener(this);
        done = layout.findViewById(R.id.draw_done);
        done.setOnClickListener(this);

        mainFrame = layout.findViewById(R.id.draw_main_frame);
        drawActionLayout = layout.findViewById(R.id.draw_action_layout);

        onDoneIv = layout.findViewById(R.id.onDone_iv);

        undoFrame = layout.findViewById(R.id.undo_frame);
        hideChecks();
    }

    public void hideChecks() {
        cancel.setVisibility(GONE);
        done.setVisibility(GONE);
    }

    private void setClickable() {
        fingerPaintView.setUndoEmptyListener(new FingerPaintView.OnUndoEmptyListener() {
            @Override
            public void undoListEmpty() {
                undo.setAlpha(0.4f);
            }

            @Override
            public void refillUndo() {
                undo.setAlpha(1.0f);
            }

            @Override
            public void OnUndoStarted() {}

            @Override
            public void OnUndoCompleted() {}

            @Override
            public void onTouchDown() {
                flipOnTouch(true);
            }

            @Override
            public void onTouchUp() {
                if (!controlsHidden)
                    flipOnTouch(false);
            }
        });
        undo.setOnClickListener(v -> fingerPaintView.onUndo());
        painterIcon.setOnClickListener(v -> strokeWidthFrame.setVisibility(strokeWidthFrame.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE));
        strokeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    strokeWidthStatus.setText("Stroke Width:" + progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                fingerPaintView.setBrushStrokeWidth(seekBar.getProgress());
            }
        });
        colorPicker.setColorPickerListener(new ColorPicker.ColorPickerListener() {
            @Override
            public void onBeganColorPicking() {

            }

            @Override
            public void onColorValueChanged(int color) {

            }

            @Override
            public void onFinishedColorPicking(int color) {
                fingerPaintView.setBrushColor(color);
            }

            @Override
            public void onSettingsPressed() {
                showBrushOptions();
            }
        });
    }

    public void hideOnDone() {
        controlsHidden = true;
        onDoneIv.setImageBitmap(fingerPaintView.getmBitmap());
        onDoneIv.setVisibility(VISIBLE);
        colorPicker.setVisibility(GONE);
        selectBrushFrame.setVisibility(GONE);
        undo.setVisibility(GONE);
        strokeWidthFrame.setVisibility(GONE);
        mainFrame.setBackgroundColor(Color.TRANSPARENT);
        drawActionLayout.setVisibility(GONE);
        fingerPaintView.setVisibility(GONE);
        painterIcon.setVisibility(GONE);
    }

    public void makeNonClickable(boolean show) {
        painterIcon.setClickable(true);
        undoFrame.setClickable(true);
        onDoneIv.setClickable(false);
        colorPicker.setClickable(show);
        selectBrushFrame.setClickable(false);
        undo.setClickable(true);
        strokeWidthFrame.setClickable(false);
        drawActionLayout.setClickable(false);
        fingerPaintView.setClickable(show);
    }

    private void showBrushOptions() {
        boolean show = selectBrushFrame.getVisibility() != VISIBLE;
        if (show) {
            selectBrushFrame.setVisibility(VISIBLE);
            selectBrushFrame.setAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_right));
        } else {
            selectBrushFrame.setVisibility(INVISIBLE);
            selectBrushFrame.setAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_right));
        }
        strokeWidthFrame.setVisibility(show ? VISIBLE : GONE);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(normalBrush)) {
            fingerPaintView.setBrushType(BrushType.BRUSH_SOLID);
            showBrushOptions();
        } else if (view.equals(neonBrush)) {
            fingerPaintView.setBrushType(BrushType.BRUSH_NEON);
            showBrushOptions();
        } else if (view.equals(innerBrush)) {
            fingerPaintView.setBrushType(BrushType.BRUSH_INNER);
            showBrushOptions();
        } else if (view.equals(blurBrush)) {
            fingerPaintView.setBrushType(BrushType.BRUSH_BLUR);
            showBrushOptions();
        } else if (view.equals(embossBrush)) {
            fingerPaintView.setBrushType(BrushType.BRUSH_EMBOSS);
            showBrushOptions();
        } else if (view.equals(debossBrush)) {
            fingerPaintView.setBrushType(BrushType.BRUSH_DEBOSS);
            showBrushOptions();
        } else if (view.equals(cancel)) {
            if (actionListener != null) actionListener.onCancel();
        } else if (view.equals(done)) {
            onDoneIv.setImageBitmap(fingerPaintView.getmBitmap());
            fingerPaintView.setVisibility(GONE);
            if (actionListener != null) actionListener.onDone(fingerPaintView.getmBitmap());
            hideOnDone();
        }

    }
}

