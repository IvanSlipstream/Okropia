package ru.slipstream.var.okropia.mechanics;

import android.os.Bundle;

import ru.slipstream.var.okropia.L;
import ru.slipstream.var.okropia.field.Clock;
import ru.slipstream.var.okropia.field.FieldState;

public abstract class Trigger {

    int mClockId;

    public abstract void onExecute(FieldState state);

    public abstract boolean onCheckConditions(FieldState state);

    public void registerClockEvent(int clockId){
        mClockId = clockId;
    }

    public void run(FieldState state, float time){
        try {
            Clock clock = (Clock) state.getFieldObjects().get(mClockId);
            int times = clock.checkClock(time);
            if (times > 0){
                L.d(Trigger.class, "running trigger with clock id: "+mClockId);
                for (int i=0;i<times;i++){
                    if (onCheckConditions(state)){
                        onExecute(state);
                    }
                }
            }
        } catch (Exception e) {
            L.d(getClass(), "Exception when running trigger: \n"+e.getLocalizedMessage());
        }
    }

}
