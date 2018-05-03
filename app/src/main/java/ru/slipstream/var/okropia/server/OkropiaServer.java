package ru.slipstream.var.okropia.server;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import ru.slipstream.var.okropia.L;
import ru.slipstream.var.okropia.field.City;
import ru.slipstream.var.okropia.field.FieldState;

public class OkropiaServer extends Service {

    public static final int CODE_USER_COMMAND = 1;
    public static final int CODE_REQUEST_STATE = 2;
    public static final int CODE_PROVIDE_STATE = 3;

    public static final String KEY_STATE = "state";

    private FieldStateUpdatingThread mThread;
    private long mLastUpdateTime;
    private FieldState mState;

    private final Object lock = new Object();

    public OkropiaServer() {
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mThread != null) {
            mThread.setRunning(false);
        }
    }

    private void timelyUpdateFieldState(long nanoseconds){
        synchronized (lock) {
            float rate = 0.0003f;
            City city = mState.getCities().get(0);
            Bundle bundle = city.getAttributes();
            long population = bundle.getLong(City.AttributeKeys.POPULATION);
            population += rate * nanoseconds;
            bundle.putLong(City.AttributeKeys.POPULATION, population);
        }
    }

    private void sendUserCommand() {

    }


    @Override
    public void onCreate() {
        super.onCreate();
        L.d(getClass(), "run on create");
        mState = new FieldState();
        mState.init();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                while (mThread.mRunning){
                    long nanoseconds = 0;
                    long currentTime = System.nanoTime();
                    if (mLastUpdateTime != 0) {
                        nanoseconds = currentTime - mLastUpdateTime;
                    }
                    mLastUpdateTime = currentTime;
                    timelyUpdateFieldState(nanoseconds);
                }
            }
        };
        mThread = performOnBackgroundThread(r);
        mThread.setRunning(true);
    }

    class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CODE_USER_COMMAND:
                    L.d(getClass(), "command from user");
                    break;
                case CODE_REQUEST_STATE:
                    L.d(getClass(), "requested state");
                    Messenger messenger = msg.replyTo;
                    try {
                        if (messenger != null) {
                            Message replyMessage = Message.obtain();
                            replyMessage.what = CODE_PROVIDE_STATE;
                            Bundle data = new Bundle(ClassLoader.getSystemClassLoader());
                            data.putParcelable(KEY_STATE, mState);
                            replyMessage.setData(data);
                            messenger.send(replyMessage);
                        }
                    } catch (RemoteException e) {
                        L.d(OkropiaServer.class, "failed to reply requested state");
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    static class FieldStateUpdatingThread extends Thread {

        boolean mRunning = true;

        public void setRunning(boolean running){
            this.mRunning = running;
        }

    };

    public static FieldStateUpdatingThread performOnBackgroundThread(final Runnable runnable) {
        final FieldStateUpdatingThread t = new FieldStateUpdatingThread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

}
