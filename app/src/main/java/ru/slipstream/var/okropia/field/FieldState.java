package ru.slipstream.var.okropia.field;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import ru.slipstream.var.okropia.L;

/**
 * Created by Slipstream-DESKTOP on 11.02.2018.
 */

public class FieldState implements Parcelable {

    private SparseArray<Object> mFieldObjects;
    private int mLastId = 0;

    public FieldState() {
    }

    private FieldState(Parcel in) {
        mLastId = in.readInt();
        mFieldObjects = in.readSparseArray(FieldObject.class.getClassLoader());
    }

    public static final Creator<FieldState> CREATOR = new Creator<FieldState>() {
        @Override
        public FieldState createFromParcel(Parcel in) {
            return new FieldState(in);
        }

        @Override
        public FieldState[] newArray(int size) {
            return new FieldState[size];
        }
    };

    public void init() {
        L.d(getClass(), "Initializing field");
        L.d(getClass(), "Adding cities");
        mFieldObjects = new SparseArray<>();
        Bundle bundle = new Bundle();
        bundle.putFloat(City.AttributeKeys.LOYALTY, 0.7f);
        bundle.putLong(City.AttributeKeys.POPULATION, 1900);
        bundle.putBoolean(City.AttributeKeys.AFFILIATION, true);
        bundle.putFloat(City.AttributeKeys.WEALTH, 0.9f);
        addFieldObject(new City(new Location(0.3f, 0.7f), bundle));
        bundle.putBoolean(City.AttributeKeys.AFFILIATION, false);
        bundle.putLong(City.AttributeKeys.POPULATION, 1400);
        bundle.putFloat(City.AttributeKeys.LOYALTY, 0.3f);
        bundle.putFloat(City.AttributeKeys.WEALTH, 0.1f);
        addFieldObject(new City(new Location(0.6f, 0.7f), bundle));
        Clock mainClock = new Clock(true, 1);
        addFieldObject(mainClock);
    }

    public int addFieldObject(FieldObject object){
        mLastId++;
        mFieldObjects.append(mLastId, object);
        return mLastId;
    }

    private FieldObject findFieldObjectById(int id){
        Object object = mFieldObjects.get(id);
        if (object instanceof FieldObject){
            return (FieldObject) object;
        } else {
            return null;
        }
    }

    public void draw(Canvas canvas, float scale, Location pivot) {
        canvas.drawColor(Color.LTGRAY);
        for (int i = 0;i < mFieldObjects.size();i++) {
            FieldObject renderingObject = (FieldObject) mFieldObjects.get(mFieldObjects.keyAt(i));
            if (renderingObject instanceof Renderable) {
                ((Renderable) renderingObject).draw(canvas, scale, pivot);
            }

        }
    }

    public SparseArray getFieldObjects() {
        return mFieldObjects;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mLastId);
        parcel.writeSparseArray(mFieldObjects);
    }
}
