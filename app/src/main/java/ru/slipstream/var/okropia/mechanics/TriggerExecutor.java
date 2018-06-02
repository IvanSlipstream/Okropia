package ru.slipstream.var.okropia.mechanics;

import android.os.Bundle;
import android.util.SparseArray;

import ru.slipstream.var.okropia.L;
import ru.slipstream.var.okropia.field.City;
import ru.slipstream.var.okropia.field.Clock;
import ru.slipstream.var.okropia.field.FieldState;

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
        trigger = new Trigger() {
            @Override
            public void onExecute(FieldState state) {
                Bundle localParameters = mParameters.get(this.mClockId);
                if (localParameters != null) {
                    float rate = 30f;
                    int i = localParameters.getInt("city_id");
                    City city = (City) state.getFieldObjects().get(i);
                    Bundle bundle = city.getAttributes();
                    long population = bundle.getLong(City.AttributeKeys.POPULATION);
                    population += rate;
                    bundle.putLong(City.AttributeKeys.POPULATION, population);
                    L.d(getClass(), "population: "+population);
                }
            }

            @Override
            public boolean onCheckConditions(FieldState state) {
                return true;
            }
        };
        clock = new Clock(true, 1);
        clockId = state.addFieldObject(clock);
        mParameters.put(clockId, parameters);
        trigger.registerClockEvent(clockId);
        mTriggers.append(lastTriggerId++, trigger);
    }

    public SparseArray<Trigger> getTriggers() {
        return mTriggers;
    }
}
