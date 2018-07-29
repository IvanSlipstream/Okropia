package ru.slipstream.var.okropia.mechanics.triggers;

import android.os.Bundle;

import ru.slipstream.var.okropia.L;
import ru.slipstream.var.okropia.field.City;
import ru.slipstream.var.okropia.field.FieldState;
import ru.slipstream.var.okropia.mechanics.Trigger;
import ru.slipstream.var.okropia.mechanics.TriggerExecutor;

public class MainTrigger extends Trigger {

    private TriggerExecutor mExecutor;

    public MainTrigger(TriggerExecutor executor) {
        this.mExecutor = executor;
    }

    @Override
    public void onExecute(FieldState state) {
        Bundle localParameters = mExecutor.getParameters().get(this.mClockId);
        if (localParameters != null) {
            float rate = 30f;
            int i = localParameters.getInt("city_id");
            City city = (City) state.getFieldObjects().get(i);
            Bundle cityAttributes = city.getAttributes();
            long population = cityAttributes.getLong(City.AttributeKeys.POPULATION);
            population += rate;
            cityAttributes.putLong(City.AttributeKeys.POPULATION, population);
        }
    }

    @Override
    public boolean onCheckConditions(FieldState state) {
        return true;
    }
}
