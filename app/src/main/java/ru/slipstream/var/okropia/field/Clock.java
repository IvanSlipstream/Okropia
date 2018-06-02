package ru.slipstream.var.okropia.field;

import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Parcel;

import ru.slipstream.var.okropia.mechanics.Clicker;

public class Clock extends FieldObject {

    public static final class AttributeKeys {
        public static final String PERIODIC = "periodic";
        public static final String DURATION = "duration";
        public static final String REMAINING = "remaining";
        public static final String ACTIVE = "active";
    }

    public Clock(boolean periodic, float duration) {
        super(new Location(0, 0), new Bundle());
        mAttributes.putBoolean(AttributeKeys.PERIODIC, periodic);
        mAttributes.putFloat(AttributeKeys.DURATION, duration);
        mAttributes.putFloat(AttributeKeys.REMAINING, duration);
        mAttributes.putBoolean(AttributeKeys.ACTIVE, true);
    }

    @Override
    public void draw(Canvas canvas, float scale, Location p) {
        // this object is invisible
    }

    @Override
    public void setDefaultAttributes() {
        // no default attributes
    }

    @Override
    public void onSelect(Clicker.CommandState commandState) {

    }

    public int checkClock(float decrementalTime){
        if (!mAttributes.getBoolean(AttributeKeys.ACTIVE)){
            return 0;
        }
        float remaining = mAttributes.getFloat(AttributeKeys.REMAINING);
        remaining -= decrementalTime;
        if (mAttributes.getBoolean(AttributeKeys.PERIODIC)) {
            int times = 0;
            while (remaining <= 0) {
                remaining += mAttributes.getFloat(AttributeKeys.DURATION);
                times++;
            }
            // save remaining time
            mAttributes.putFloat(AttributeKeys.REMAINING, remaining);
            return times;
        } else {
            if (remaining <= 0){
                // deactivate after expiration if timer is not periodic
                mAttributes.putBoolean(AttributeKeys.ACTIVE, false);
            }
            // save remaining time
            mAttributes.putFloat(AttributeKeys.REMAINING, remaining);
            return 1;
        }
    }

    protected Clock(Parcel in) {
        super(in);
    }

    public static final Creator<Clock> CREATOR = new Creator<Clock>() {
        @Override
        public Clock createFromParcel(Parcel in) {
            return new Clock(in);
        }

        @Override
        public Clock[] newArray(int size) {
            return new Clock[size];
        }
    };


}
