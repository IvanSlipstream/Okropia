package ru.slipstream.var.okropia.field;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import ru.slipstream.var.okropia.L;

/**
 * Created by Slipstream-DESKTOP on 11.02.2018.
 */

public class FieldState implements Parcelable {

    public static final float MIN_SCALE = 1f;
    public static final float MAX_SCALE = 10f;
    public static final float BORDER_SIZE = 0.1f;

    private float mCurrentScale = 1f;
    private float mPivotX = 0.5f;
    private float mPivotY = 0.5f;
    private ArrayList<City> cities;

    public FieldState() {
    }

    protected FieldState(Parcel in) {
        mCurrentScale = in.readFloat();
        mPivotX = in.readFloat();
        mPivotY = in.readFloat();
        cities = in.createTypedArrayList(City.CREATOR);
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
        cities = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putFloat(City.AttributeKeys.LOYALTY, 0.7f);
        bundle.putLong(City.AttributeKeys.POPULATION, 1900);
        bundle.putBoolean(City.AttributeKeys.AFFILIATION, true);
        bundle.putFloat(City.AttributeKeys.WEALTH, 0.9f);
        cities.add(new City(new Location(0.3f, 0.7f), bundle));
        bundle.putBoolean(City.AttributeKeys.AFFILIATION, false);
        bundle.putLong(City.AttributeKeys.POPULATION, 1400);
        bundle.putFloat(City.AttributeKeys.LOYALTY, 0.3f);
        bundle.putFloat(City.AttributeKeys.WEALTH, 0.1f);
        cities.add(new City(new Location(0.6f, 0.7f), bundle));
    }

    public void setCurrentScale(float currentScale) {
        this.mCurrentScale = Math.min(MAX_SCALE, Math.max(currentScale, MIN_SCALE));
    }

    public void setPivot(Location pivot) {
        this.mPivotX = Math.min(1f - BORDER_SIZE, Math.max(pivot.x, BORDER_SIZE));
        this.mPivotY = Math.min(1f - BORDER_SIZE, Math.max(pivot.y, BORDER_SIZE));
    }

    public void draw(Canvas canvas) {
        canvas.drawColor(Color.LTGRAY);
        for (City city :
                cities) {
            city.draw(canvas, mCurrentScale, getPivot());
        }
    }

    public Location getPivot() {
        return new Location(mPivotX, mPivotY);
    }

    public float getCurrentScale(){
        return mCurrentScale;
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeFloat(mCurrentScale);
        parcel.writeFloat(mPivotX);
        parcel.writeFloat(mPivotY);
        parcel.writeTypedList(cities);
    }
}
