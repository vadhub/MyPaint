package com.abg.mypaint.painview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;

import com.abg.mypaint.brush.Brush;
import com.abg.mypaint.brush.BrushType;

import java.util.ArrayList;

public class FingerPaintView extends AppCompatImageView {

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private final ArrayList<PaintData> pathList = new ArrayList<>();
    private int width;
    private int height;
    private float radius = 28;
    private Paint undoPaint;
    private boolean redraw;
    private int lastColor;
    private float lastStrokeWidth;
    private MaskFilter lastMaskFilter;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    public interface OnUndoEmptyListener {
        void undoListEmpty();

        void refillUndo();

        void onTouchDown();

        void onTouchUp();
    }

    private OnUndoEmptyListener undoEmptyListener;

    public void setUndoEmptyListener(OnUndoEmptyListener undoEmptyListener) {
        this.undoEmptyListener = undoEmptyListener;
    }

    public FingerPaintView(Context c) {
        super(c);
        init(c);
    }

    public FingerPaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FingerPaintView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context c) {
        mPaint = new Paint();

        lastColor = 0xffff0000;
        lastStrokeWidth = c.getSharedPreferences("paint", Context.MODE_PRIVATE).getFloat("stroke", 12.0f);
        lastMaskFilter = idToMaskFilter(c.getSharedPreferences("paint", Context.MODE_PRIVATE).getInt("id", BrushType.BRUSH_SOLID), radius = 28);

        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(lastColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(lastStrokeWidth);
        mPaint.setMaskFilter(lastMaskFilter);

        undoPaint = mPaint;
        redraw = false;
        DisplayMetrics displayMetrics = c.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        mBitmap = Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels, Bitmap.Config.ARGB_8888);
        mBitmap.eraseColor(0xFFFFFFFF);
        mCanvas = new Canvas(mBitmap);
        mPath = new Path();
        pathList.add(new PaintData(lastColor, lastStrokeWidth, lastMaskFilter, mPath));
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setBrushColor(int color) {
        mPaint.setColor(color);
        lastColor = color;

    }

    public void setBrushStrokeWidth(float width) {
        if (width > 0.f) lastStrokeWidth = width;
        if (lastStrokeWidth == 0.f) lastStrokeWidth = 12.f;
        mPaint.setStrokeWidth(lastStrokeWidth);
        undoPaint = mPaint;
        getContext().getSharedPreferences("paint", Context.MODE_PRIVATE).edit().putFloat("stroke", lastStrokeWidth).apply();
    }

    public void setBrushType(int id) {
        lastMaskFilter = idToMaskFilter(id, radius);
        mPaint.setMaskFilter(lastMaskFilter);
        undoPaint = mPaint;
        getContext().getSharedPreferences("paint", Context.MODE_PRIVATE).edit().putInt("id", id).apply();
    }

    private MaskFilter idToMaskFilter(int id, float radius) {
        switch (id) {

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
            default:
                return Brush.setSolidBrush(radius);

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            if (!redraw) canvas.drawPath(mPath, mPaint); else redraw();
        } catch (Exception e) {
            Log.e("onDraw", e.toString());
        }
    }

    private void touch_start(float x, float y) {
        if (undoEmptyListener != null) undoEmptyListener.onTouchDown();
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        if (undoEmptyListener != null && pathList.size() == 0) undoEmptyListener.refillUndo();
        if (undoEmptyListener != null) undoEmptyListener.onTouchUp();
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw

        PaintData pd = new PaintData(lastColor, lastStrokeWidth, lastMaskFilter, mPath);
        pathList.add(pd);
        mPath.reset();
    }

    public void onUndo() {
        try {
            redraw = true;
            if (pathList.size() > 0) {
                pathList.remove(pathList.size() - 1);
                onSizeChanged(width, height, width, height);
                if (pathList.isEmpty() && undoEmptyListener != null)
                    undoEmptyListener.undoListEmpty();
                invalidate();
            } else {
                if (undoEmptyListener != null) undoEmptyListener.undoListEmpty();
            }
        } catch (Exception e) {
            Log.e("onUndo", e.getMessage());
        }
    }

    private void redraw() {
        for (PaintData pd : pathList) {
            undoPaint = setPaintAttrs();
            undoPaint.setColor(pd.color);
            undoPaint.setStrokeWidth(pd.strokeWidth);
            if (pd.maskFilter != null) undoPaint.setMaskFilter(pd.maskFilter);
            mCanvas.drawPath(pd.path, undoPaint);
        }
        redraw = false;

    }

    private Paint setPaintAttrs() {
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        return mPaint;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }
        return true;
    }

    private static class PaintData {
        public int color;
        public float strokeWidth;
        public MaskFilter maskFilter;
        public Path path = new Path();

        public PaintData(int clr, float strokeWidth, MaskFilter maskFilter, Path path) {
            this.color = clr;
            this.maskFilter = maskFilter;
            this.strokeWidth = strokeWidth;
            this.path.set(path);

        }
    }
}
