package com.abg.mypaint.ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.abg.mypaint.ui.brush.brushtypes.BlurBrushCommand;
import com.abg.mypaint.ui.brush.brushtypes.DebossBrushCommand;
import com.abg.mypaint.ui.brush.brushtypes.DefaultBrushCommand;
import com.abg.mypaint.ui.brush.brushtypes.EmbossBrushCommand;
import com.abg.mypaint.ui.brush.brushtypes.InnerBrushCommand;
import com.abg.mypaint.ui.brush.brushtypes.SolidBrushCommand;
import com.abg.mypaint.R;
import com.abg.mypaint.ui.brush.BrushOption;
import com.abg.mypaint.ui.brush.BrushType;
import com.abg.mypaint.ui.brush.brushtypes.NeonBrushCommand;
import com.abg.mypaint.ui.command.SaveCommand;
import com.abg.mypaint.ui.command.StrokeWidthCommand;
import com.abg.mypaint.ui.command.UndoCommand;

/**
 * Created by INFIi on 1/21/2017. refactoring by VadHub 😎 on 21/02/2024
 */
public class DrawableFragment extends Fragment implements FingerPaintView.OnUndoEmptyListener, SeekBar.OnSeekBarChangeListener, ColorPicker.ColorPickerListener {

    private ImageButton undo;
    private ColorPicker colorPicker;
    private FingerPaintView fingerPaintView;
    private LinearLayout selectBrushFrame, strokeWidthFrame;
    private FrameLayout undoFrame;
    private TextView strokeWidthStatus;
    private FileHandler fileHandler;
    private BrushOption brushOption;

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
        brushOption.showBrushOptions();
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

    private void enableShader(ShaderTextView shaderTextView, int filterId) {
        shaderTextView.setFilterId(filterId);
        shaderTextView.setRadius(16);
        shaderTextView.enableMask();
    }

    private void bindViews(View layout) {
        selectBrushFrame = layout.findViewById(R.id.brush_option_frame);
        strokeWidthFrame = layout.findViewById(R.id.stroke_width_layout);
        brushOption = new BrushOption(selectBrushFrame, strokeWidthFrame);
        fingerPaintView = layout.findViewById(R.id.finger_paint);
        fingerPaintView.setUndoEmptyListener(this);

        ImageButton save = layout.findViewById(R.id.save);
        save.setOnClickListener(new SaveCommand(fileHandler, fingerPaintView));
        SeekBar strokeSeekbar = layout.findViewById(R.id.stroke_width_seekbar);
        strokeSeekbar.setMax(50);
        strokeSeekbar.setProgress(14);
        strokeSeekbar.setOnSeekBarChangeListener(this);

        undo = layout.findViewById(R.id.undo_btn);
        undo.setOnClickListener(new UndoCommand(fingerPaintView));
        undo.setImageResource(R.drawable.ic_undo);

        ImageButton strokeWidth = layout.findViewById(R.id.show_stroke_bar);
        strokeWidth.setImageResource(R.drawable.ic_gestures);
        strokeWidth.setOnClickListener(new StrokeWidthCommand(strokeWidthFrame));

        ShaderTextView defaultBrush = layout.findViewById(R.id.default_brush);
        defaultBrush.setOnClickListener(new DefaultBrushCommand(brushOption, fingerPaintView));
        enableShader(defaultBrush, BrushType.BRUSH_DEFAULT);

        ShaderTextView solidBrush = layout.findViewById(R.id.solid_brush);
        solidBrush.setOnClickListener(new SolidBrushCommand(brushOption, fingerPaintView));
        enableShader(solidBrush, BrushType.BRUSH_SOLID);

        ShaderTextView neonBrush = layout.findViewById(R.id.neon_brush);
        neonBrush.setOnClickListener(new NeonBrushCommand(brushOption, fingerPaintView));
        enableShader(neonBrush, BrushType.BRUSH_NEON);

        ShaderTextView innerBrush = layout.findViewById(R.id.inner_brush);
        innerBrush.setOnClickListener(new InnerBrushCommand(brushOption, fingerPaintView));
        enableShader(innerBrush, BrushType.BRUSH_INNER);

        ShaderTextView blurBrush = layout.findViewById(R.id.blur_brush);
        blurBrush.setOnClickListener(new BlurBrushCommand(brushOption, fingerPaintView));
        enableShader(blurBrush, BrushType.BRUSH_BLUR);

        ShaderTextView embossBrush = layout.findViewById(R.id.emboss_brush);
        embossBrush.setOnClickListener(new EmbossBrushCommand(brushOption, fingerPaintView));
        enableShader(embossBrush, BrushType.BRUSH_EMBOSS);

        ShaderTextView debossBrush = layout.findViewById(R.id.deboss_brush);
        debossBrush.setOnClickListener(new DebossBrushCommand(brushOption, fingerPaintView));
        enableShader(debossBrush, BrushType.BRUSH_DEBOSS);

        colorPicker = layout.findViewById(R.id.color_picker);
        colorPicker.setColorPickerListener(this);

        strokeWidthStatus = layout.findViewById(R.id.stroke_width_status);
        undoFrame = layout.findViewById(R.id.undo_frame);

    }

}

