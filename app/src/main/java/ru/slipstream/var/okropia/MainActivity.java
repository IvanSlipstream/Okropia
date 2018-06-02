package ru.slipstream.var.okropia;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import ru.slipstream.var.okropia.field.City;
import ru.slipstream.var.okropia.field.FieldState;
import ru.slipstream.var.okropia.layout.PinchLayout;
import ru.slipstream.var.okropia.server.OkropiaServer;
import ru.slipstream.var.okropia.views.FieldView;

public class MainActivity extends AppCompatActivity {

    private Messenger mOutMessenger;
    private Messenger mInMessenger;
    private boolean mBound;
    private ClientMessageHandler mHandler;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBound = true;
            mOutMessenger = new Messenger(iBinder);
            mInMessenger = new Messenger(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
            mOutMessenger = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, OkropiaServer.class), mConnection, Context.BIND_AUTO_CREATE);
        FieldView fieldView = findViewById(R.id.fv_main);
        mHandler = new ClientMessageHandler(fieldView);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        FieldView fieldView = findViewById(R.id.fv_main);
//        FieldState fieldState = new FieldState();
//        fieldState.init();
//        fieldView.updateFieldState(fieldState);
        Switch sw = findViewById(R.id.sw_resize);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                PinchLayout layout = findViewById(R.id.pl_main);
                layout.setEnableResize(checked);
            }
        });
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = OkropiaServer.CODE_REQUEST_STATE;
                L.d(getClass(), "attempting to send messagge");
                try {
                    if (mBound) {
                        L.d(getClass(), "sending messagge");
                        message.replyTo = mInMessenger;
                        mOutMessenger.send(message);
                    }
                } catch (RemoteException e) {
                    L.d(getClass(), "failed to send a message"+message.toString());
                }
                handler.postDelayed(this, 40);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private static class ClientMessageHandler extends Handler {

        private FieldView mFieldView;

        public ClientMessageHandler(FieldView fieldView) {
            this.mFieldView = fieldView;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case OkropiaServer.CODE_PROVIDE_STATE:
                    Bundle data = msg.getData();
                    if (data != null) {
                        FieldState fieldState = data.getParcelable(OkropiaServer.KEY_STATE);
                        if (fieldState != null) {
                            mFieldView.updateFieldState(fieldState);
                        }
                    }
            }
            super.handleMessage(msg);
        }
    }
}
