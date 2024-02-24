package com.abg.mypaint.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.abg.mypaint.R;
import com.abg.mypaint.brush.Brush;
import com.abg.mypaint.brush.BrushType;

public class ShaderTextView extends AppCompatTextView {

    private int radius;
    private int filterId;

    public ShaderTextView(Context context) {
        super(context);
    }

    public ShaderTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ShaderTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public void setFilterId(int filterId) {
        this.filterId = filterId;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    //to be called after setting id and radius
    public void enableMask() {
        this.getPaint().setMaskFilter(idToMaskFilter(filterId, radius));
    }

    private void init(Context context, AttributeSet attrs) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShaderTextView);
        radius = typedArray.getInt(R.styleable.ShaderTextView_mask_radius, 14);

        this.getPaint().setColor(Color.parseColor("#ff00cc"));
        this.getPaint().setMaskFilter(idToMaskFilter(filterId, radius));

        typedArray.recycle();
    }

    private MaskFilter idToMaskFilter(int id, float radius){
        switch (id){
            case BrushType.BRUSH_NEON:
                return Brush.setNeonBrush(radius);
            case BrushType.BRUSH_BLUR:
                return Brush.setBlurBrush(radius);
            case BrushType.BRUSH_INNER:
                return Brush.setInnerBrush(radius);
            case BrushType.BRUSH_EMBOSS:
                return Brush.setEmbossBrush();
            case BrushType.BRUSH_DEBOSS:
                return Brush.setDebossBrush();
            case BrushType.BRUSH_DEFAULT:
                return null;
            default:
                return Brush.setSolidBrush(radius);

        }
    }
}
