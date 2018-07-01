package ru.slipstream.var.okropia.mechanics;

import android.os.Bundle;
import android.util.SparseArray;

import ru.slipstream.var.okropia.L;
import ru.slipstream.var.okropia.field.City;
import ru.slipstream.var.okropia.field.Clock;
import ru.slipstream.var.okropia.field.FieldState;
import ru.slipstream.var.okropia.mechanics.triggers.MainTrigger;

public class TriggerExecutor {

    private SparseArray<Trigger> mTriggers = new SparseArray<>();
    private SparseArray<Bundle> mParameters = new SparseArray<>();

    /**
     * This method should run once on {@link ru.slipstream.var.okropia.server.OkropiaServer} start.
     * Initializes all triggers.
     * @param state a state to create triggers for.
     */
    public void initTriggers(FieldState state){
        Trigger trigger;
        Clock clock;
        int clockId;
        Bundle parameters = new Bundle();
        int lastTriggerId = 0;
        // main trigger
        parameters.putInt("city_id", 1);
        trigger = new MainTrigger(this);
        clock = new Clock(true, 1);
        clockId = state.addFieldObject(clock);
        mParameters.put(clockId, parameters);
        trigger.registerClockEvent(clockId);
        mTriggers.append(lastTriggerId++, trigger);
    }

    public SparseArray<Trigger> getTriggers() {
        return mTriggers;
    }

    public SparseArray<Bundle> getParameters() {
        return mParameters;
    }
}
