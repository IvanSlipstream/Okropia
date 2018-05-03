package ru.slipstream.var.okropia.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import ru.slipstream.var.okropia.L;
import ru.slipstream.var.okropia.field.FieldState;
import ru.slipstream.var.okropia.mechanics.Clicker;

/**
 * Created by Slipstream-DESKTOP on 04.02.2018.
 */

public class FieldView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String KEY_FIELD_STATE = "field_state";

    private DrawThread mThread;
    private FieldState mState;
    private Clicker mClicker;
    private Handler mHandler;

    public FieldView(Context context) {
        super(context);
        init();
    }

    public FieldView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // set handler for responses from drawing thread
        getHolder().addCallback(this);
        // attaching and initializing FieldState
        mState = new FieldState();
        mState.init();
        // handle user clicks
        mClicker = new Clicker(mState);
        setOnTouchListener(mClicker);
    }

    public FieldState getState() {
        return mState;
    }

    public void updateFieldState(FieldState fieldState) {
        this.mState = fieldState;
        if (mHandler != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(KEY_FIELD_STATE, fieldState);
            Message message = Message.obtain();
            message.setData(bundle);
            mHandler.dispatchMessage(message);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mThread = new DrawThread(surfaceHolder);
        mThread.setRunning(true);
        mThread.start();
        mHandler = new Handler(mThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (!mThread.mRunning) {
                    mThread.getLooper().quit();
                }
                mState = message.getData().getParcelable(KEY_FIELD_STATE);
                L.d(getClass(), "message received");
                Canvas canvas = null;
                try {
                    canvas = mThread.mHolder.lockCanvas();
                    if (canvas != null) {
                        mState.draw(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        mThread.mHolder.unlockCanvasAndPost(canvas);
                    }
                }
                return false;
            }
        });
        updateFieldState(mState);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        mThread.setRunning(false);
        while (retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class DrawThread extends HandlerThread {

        boolean mRunning = false;
        SurfaceHolder mHolder;

        DrawThread(SurfaceHolder holder) {
            super("drawer");
            this.mHolder = holder;
        }

        void setRunning(boolean running) {
            this.mRunning = running;
        }

//        @Override
//        public void run() {
//            while (mRunning) {
//                Canvas canvas = null;
//                try {
//                    canvas = mHolder.lockCanvas();
//                    if (canvas != null) {
//                        mState.draw(canvas);
//                    }
//                } finally {
//                    if (canvas != null) {
//                        mHolder.unlockCanvasAndPost(canvas);
//                    }
//                }
//            }
//        }
    }
}
