package ru.slipstream.var.okropia.layout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import ru.slipstream.var.okropia.L;
import ru.slipstream.var.okropia.R;
import ru.slipstream.var.okropia.field.Location;
import ru.slipstream.var.okropia.views.FieldView;

/**
 * Created by Slipstream-DESKTOP on 29.01.2018.
 */

public class PinchLayout extends FrameLayout
        implements ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {

    private  static final float MAX_MOVEMENT_PX = 20;

    private float mScale = 1;
    private ScaleGestureDetector mDetector;
    private float mStartX;
    private float mStartY;
    private boolean mEnableResize = false;
    private boolean mMoving = false;

    public PinchLayout(@NonNull Context context) {
        super(context);
        mDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);
    }

    public PinchLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnableResize) {
            mDetector.onTouchEvent(event);
        }
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        float scaleFactor = scaleGestureDetector.getScaleFactor();
        mScale *= scaleFactor;
        FieldView fieldView = findViewById(R.id.fv_main);
        mScale = Math.min(FieldView.MAX_SCALE, Math.max(mScale, FieldView.MIN_SCALE));
        L.d(getClass(), "Setting scale " + mScale + ", thread id: " + Thread.currentThread().getId());
        if (fieldView != null) {
            fieldView.setCurrentScale(mScale);
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        L.d(getClass(), "Scale begin");
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() == 1 && mEnableResize) {

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if (!mMoving) {
                        break;
                    }
                    if (mStartX != 0 && mStartY != 0) {
                        float pxPointerShiftX = mStartX - motionEvent.getX(0);
                        float pxPointerShiftY = mStartY - motionEvent.getY(0);
                        float deltaX = pxPointerShiftX / mScale / view.getWidth();
                        float deltaY = pxPointerShiftY / mScale / view.getHeight();
                        L.d(PinchLayout.class, "Motion: delta X="+deltaX+", delta Y="+deltaY);
                        if (pxPointerShiftX > MAX_MOVEMENT_PX || pxPointerShiftY > MAX_MOVEMENT_PX){
                            break;
                        }
                        FieldView fieldView = findViewById(R.id.fv_main);
                        if (fieldView != null) {
                            Location location = fieldView.getPivot();
                            location.translate(deltaX, deltaY);
                            fieldView.setPivot(location);
                        }
                    }
                case MotionEvent.ACTION_DOWN:
                    mStartX = motionEvent.getX(0);
                    mStartY = motionEvent.getY(0);
                    mMoving = true;
                    break;
                case MotionEvent.ACTION_UP:
                    mStartX = 0;
                    mStartY = 0;
                    mMoving = false;
                    break;
            }
        }
        return false;
    }

    public void setEnableResize(boolean enableResize){
        this.mEnableResize = enableResize;
        this.mStartY = 0;
        this.mStartX = 0;
    }
}
