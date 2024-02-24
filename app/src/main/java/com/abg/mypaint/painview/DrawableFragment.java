package com.abg.mypaint.painview;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.abg.mypaint.R;
import com.abg.mypaint.brush.BrushType;
import com.abg.mypaint.color.ColorPicker;

/**
 * Created by INFIi on 1/21/2017. refactoring by VadHub 😎 on 21/02/2024
 */
public class DrawableFragment extends Fragment implements View.OnClickListener, FingerPaintView.OnUndoEmptyListener, SeekBar.OnSeekBarChangeListener, ColorPicker.ColorPickerListener {

    private ImageButton undo, painterIcon, save;
    private ShaderTextView solidBrush, neonBrush, innerBrush, blurBrush, embossBrush, debossBrush;
    private ColorPicker colorPicker;
    private FingerPaintView fingerPaintView;
    private LinearLayout selectBrushFrame, strokeWidthFrame;
    private FrameLayout undoFrame;
    private TextView strokeWidthStatus;
    private FileHandler fileHandler;

    public interface FileHandler {
        void onSave(Bitmap bitmap);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fileHandler = (FileHandler) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.drawable_view_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindViews(view);
    }

    public void flipOnTouch(boolean isVisible) {
        int v = isVisible ? GONE : VISIBLE;
        colorPicker.setVisibility(v);
        selectBrushFrame.setVisibility(GONE);
        strokeWidthFrame.setVisibility(GONE);
        undoFrame.setVisibility(v);
    }

    @Override
    public void onFinishedColorPicking(int color) {
        fingerPaintView.setBrushColor(color);
    }

    @Override
    public void onSettingsPressed() {
        showBrushOptions();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            strokeWidthStatus.setText(getString(R.string.stroke_width) + progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        fingerPaintView.setBrushStrokeWidth(seekBar.getProgress());
    }

    @Override
    public void onClick(View view) {
        if (view.equals(solidBrush)) {
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
        } else if (view.equals(save)) {
            fileHandler.onSave(fingerPaintView.getmBitmap());
        } else if (view.equals(undo)) {
            fingerPaintView.onUndo();
        } else if (view.equals(painterIcon)) {
            strokeWidthFrame.setVisibility(strokeWidthFrame.getVisibility() == VISIBLE ? View.INVISIBLE : VISIBLE);
        }

    }

    @Override
    public void undoListEmpty() {
        undo.setAlpha(0.4f);
    }

    @Override
    public void refillUndo() {
        undo.setAlpha(1.0f);
    }

    @Override
    public void onTouchDown() {
        flipOnTouch(true);
    }

    @Override
    public void onTouchUp() {
        flipOnTouch(false);
    }

    private void showBrushOptions() {
        boolean show = selectBrushFrame.getVisibility() != VISIBLE;
        if (show) {
            selectBrushFrame.setVisibility(VISIBLE);
            selectBrushFrame.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right));
        } else {
            selectBrushFrame.setVisibility(GONE);
            selectBrushFrame.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right));
        }
        strokeWidthFrame.setVisibility(show ? VISIBLE : GONE);
    }

    private void enableShader(ShaderTextView shaderTextView,int filterId){
        shaderTextView.setFilterId(filterId);
        shaderTextView.setRadius(16);
        shaderTextView.enableMask();
    }

    private void bindViews(View layout) {
        save = layout.findViewById(R.id.save);
        save.setOnClickListener(this);
        selectBrushFrame = layout.findViewById(R.id.brush_option_frame);
        strokeWidthFrame = layout.findViewById(R.id.stroke_width_layout);
        SeekBar strokeSeekbar = layout.findViewById(R.id.stroke_width_seekbar);
        strokeSeekbar.setMax(50);
        strokeSeekbar.setProgress(14);
        strokeSeekbar.setOnSeekBarChangeListener(this);

        undo = layout.findViewById(R.id.undo_btn);
        undo.setOnClickListener(this);
        undo.setImageResource(R.drawable.ic_undo);
        painterIcon = layout.findViewById(R.id.show_stroke_bar);
        painterIcon.setImageResource(R.drawable.ic_gestures);
        painterIcon.setOnClickListener(this);

        solidBrush = layout.findViewById(R.id.normal_brush);
        solidBrush.setOnClickListener(this);
        enableShader(solidBrush, BrushType.BRUSH_SOLID);

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
        colorPicker.setColorPickerListener(this);

        fingerPaintView = layout.findViewById(R.id.finger_paint);
        fingerPaintView.setUndoEmptyListener(this);

        strokeWidthStatus = layout.findViewById(R.id.stroke_width_status);
        undoFrame = layout.findViewById(R.id.undo_frame);

    }

}

