package ru.slipstream.var.okropia.mechanics;

import android.view.MotionEvent;
import android.view.View;

import ru.slipstream.var.okropia.L;
import ru.slipstream.var.okropia.field.City;
import ru.slipstream.var.okropia.field.FieldObject;
import ru.slipstream.var.okropia.field.FieldState;
import ru.slipstream.var.okropia.field.Location;

/**
 * Created by Slipstream-DESKTOP on 01.03.2018.
 */

public class Clicker implements View.OnTouchListener {

    private FieldState mFieldState;

    public enum CommandState {
        IDLE_TO_SELECT
    }

    public Clicker(FieldState state) {
        this.mFieldState = state;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        L.d(getClass(), "Touch detected, pointers: "+motionEvent.getPointerCount());
        if (motionEvent.getPointerCount() > 1){
            return false;
        }

        // calculating touch coordinates in field units

        float touchX = motionEvent.getX(0) - view.getX();
        float touchY = motionEvent.getY(0) - view.getY();

        Location pivot = mFieldState.getPivot();
        float scale = mFieldState.getCurrentScale();

        float locationX = ( touchX / view.getWidth() - 0.5f ) / scale + pivot.x;
        float locationY = ( touchY / view.getHeight() - 0.5f ) / scale + pivot.y;

        Location targetLocation = new Location(locationX, locationY);
        L.d(getClass(), "click location: "+targetLocation.toString());

        // target to run "on select" method
        FieldObject target = null;

        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (mFieldState != null) {
                    for (City city :
                            mFieldState.getCities()) {
                        if (Location.distance(city.getLocation(), targetLocation) <= city.getSize()){
                            target = city;
                            break;
                        }
                    }
                }
        }
        if (target != null) {
            target.onSelect(CommandState.IDLE_TO_SELECT);
            return true;
        }
        return false;
    }
}
