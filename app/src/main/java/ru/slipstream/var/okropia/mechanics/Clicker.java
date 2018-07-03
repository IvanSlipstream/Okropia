package ru.slipstream.var.okropia.mechanics;

import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import ru.slipstream.var.okropia.L;
import ru.slipstream.var.okropia.field.Clickable;
import ru.slipstream.var.okropia.field.FieldObject;
import ru.slipstream.var.okropia.field.FieldState;
import ru.slipstream.var.okropia.field.Location;
import ru.slipstream.var.okropia.views.FieldView;

/**
 * Created by Slipstream-DESKTOP on 01.03.2018.
 */

public class Clicker implements View.OnTouchListener {

    private FieldState mFieldState;
    private FieldView mFieldView;

    public enum CommandState {
        IDLE
    }

    public Clicker(FieldView fieldView) {
        this.mFieldView = fieldView;
        this.mFieldState = fieldView.getState();
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

        Location pivot = mFieldView.getPivot();
        float scale = mFieldView.getCurrentScale();

        float locationX = ( touchX / view.getWidth() - 0.5f ) / scale + pivot.x;
        float locationY = ( touchY / view.getHeight() - 0.5f ) / scale + pivot.y;

        Location targetLocation = new Location(locationX, locationY);
        L.d(getClass(), "click location: "+targetLocation.toString());

        // target to run "on select" method
        FieldObject target = null;

        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (mFieldState != null) {
                    SparseArray fieldObjects = mFieldState.getFieldObjects();
                    for (int i = 0;i < fieldObjects.size();i++) {
                        FieldObject fieldObject = (FieldObject) fieldObjects.get(fieldObjects.keyAt(i));
                        if (Location.distance(fieldObject.getLocation(), targetLocation) <= fieldObject.getSize()
                                && fieldObject instanceof Clickable){
                            target = fieldObject;
                            break;
                        }
                    }
                }
        }
        if (target != null) {
            ((Clickable) target).onSelect(this, CommandState.IDLE);
            return true;
        }
        return false;
    }
}
