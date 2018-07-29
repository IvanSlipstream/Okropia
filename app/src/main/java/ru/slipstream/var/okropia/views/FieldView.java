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
import ru.slipstream.var.okropia.field.Location;
import ru.slipstream.var.okropia.mechanics.Clicker;
import ru.slipstream.var.okropia.mechanics.CommandReceiver;

/**
 * Created by Slipstream-DESKTOP on 04.02.2018.
 */

public class FieldView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String KEY_FIELD_STATE = "field_state";

    public static final float MIN_SCALE = 1f;
    public static final float MAX_SCALE = 10f;
    public static final float BORDER_SIZE = 0.1f;

    private DrawThread mThread;
    private FieldState mState;
    private Clicker mClicker;
    private Handler mHandler;
    private Location mPivot = new Location(0.5f, 0.5f);
    private float mCurrentScale = 1f;
    private CommandReceiver mCommandReceiver;

    public FieldView(Context context) {
        super(context);
        if (context instanceof CommandReceiver){
            mCommandReceiver = (CommandReceiver) context;
        } else {
            throw new UnsupportedOperationException("Context must implement "
                    +CommandReceiver.class.getSimpleName());
        }
        getHolder().addCallback(this);
    }

    public FieldView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (context instanceof CommandReceiver){
            mCommandReceiver = (CommandReceiver) context;
        } else {
            throw new UnsupportedOperationException("Context must implement "
                    +CommandReceiver.class.getSimpleName());
        }
        getHolder().addCallback(this); // TODO: 03.06.2018 refactor copy-paste code
    }

    public boolean sendCommand(int commandId, Bundle parameters){
        return mCommandReceiver.sendUserCommand(commandId, parameters);
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

    public void setCurrentScale(float currentScale) {
        this.mCurrentScale = Math.min(MAX_SCALE, Math.max(currentScale, MIN_SCALE));
    }

    public void setPivot(Location pivot) {
        float x = Math.min(1f - BORDER_SIZE, Math.max(pivot.x, BORDER_SIZE));
        float y = Math.min(1f - BORDER_SIZE, Math.max(pivot.y, BORDER_SIZE));
        this.mPivot = new Location(x, y);
    }

    public Location getPivot() {
        return mPivot;
    }

    public float getCurrentScale() {
        return mCurrentScale;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mState = new FieldState();
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
                L.d(Handler.class, "message received");
                Canvas canvas = null;
                try {
                    canvas = mThread.mHolder.lockCanvas();
                    if (canvas != null) {
                        mState.draw(canvas, mCurrentScale, mPivot);
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
