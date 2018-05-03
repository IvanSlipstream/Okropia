package ru.slipstream.var.okropia.field;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import ru.slipstream.var.okropia.mechanics.Clicker;

/**
 * Created by Slipstream-DESKTOP on 18.02.2018.
 */

public abstract class FieldObject implements Parcelable {

    protected Location mLocation;
    protected float mSize;
    protected float mAngle;
    protected Bundle mAttributes;

    /**
     * You should define size (mSize) and angle (mAngle)
     * @param location object location
     * @param attributes attributes that override default
     */
    public FieldObject(Location location, Bundle attributes) {
        this.mLocation = location;
        this.mAttributes = new Bundle();
        setDefaultAttributes();
        this.mAttributes.putAll(attributes);
    }

    public abstract void draw(Canvas canvas, float scale, Location p);

    public abstract void setDefaultAttributes();

    public abstract void onSelect(Clicker.CommandState commandState);

    /**
     * Calculate location coordinates at the canvas
     * @param canvas a Canvas {@link FieldObject} is drawn on
     * @param scale current scale of canvas
     * @param pivot field coordinates of pivot point
     * @return location coordinates in px
     */
    protected PointF getScaledCoordinates(Canvas canvas, float scale, Location pivot){
        float canvasX = canvas.getWidth()*(scale*(mLocation.x-pivot.x)+0.5f);
        float canvasY = canvas.getHeight()*(scale*(mLocation.y-pivot.y)+0.5f);
        return new PointF(canvasX, canvasY);
    }

    /**
     * Based on {@link FieldObject} orientation, calculate size the {@link FieldObject} will have at canvas
     * @param canvas a Canvas {@link FieldObject} is drawn on
     * @param scale current scale of canvas
     * @return linear size in px
     */
    protected float getScaledSize(Canvas canvas, float scale){
        return (float) (mSize * scale * Math.sqrt(Math.pow(canvas.getHeight()*Math.sin(mAngle), 2)
                + Math.pow(canvas.getWidth()*Math.cos(mAngle), 2)));
    }

    public void setAttributes(Bundle attributes){
        this.mAttributes = attributes;
    }

    public Location getLocation() {
        return mLocation;
    }

    public Bundle getAttributes() {
        return mAttributes;
    }

    public float getSize() {
        return mSize;
    }

    // Parcelable implementation

    public FieldObject(Parcel in) {
        ClassLoader classLoader = FieldObject.class.getClassLoader();
        this.mLocation = in.readParcelable(classLoader);
        this.mSize = in.readFloat();
        this.mAngle = in.readFloat();
        this.mAttributes = in.readParcelable(classLoader);
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(mLocation, flags);
        parcel.writeFloat(mSize);
        parcel.writeFloat(mAngle);
        parcel.writeParcelable(mAttributes, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
