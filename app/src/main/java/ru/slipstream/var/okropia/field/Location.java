package ru.slipstream.var.okropia.field;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Slipstream-DESKTOP on 18.02.2018.
 */

public class Location implements Parcelable {

    public float x;
    public float y;

    public Location(float x, float y) {
        this.x = x;
        this.y = y;
    }

    protected Location(Parcel in) {
        x = in.readFloat();
        y = in.readFloat();
    }

    /**
     * Translates this location by a given vector
     * @param deltaX x of vector
     * @param deltaY y of vector
     */
    public void translate(float deltaX, float deltaY){
        x += deltaX;
        y += deltaY;
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public static float distance(Location loc1, Location loc2) {
        return (float) Math.pow(Math.pow(loc1.x - loc2.x, 2)+Math.pow(loc1.y - loc2.y, 2), 0.5f);
    }

    @Override
    public String toString() {
        return "Location{" + "x=" + x + ", y=" + y + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(x);
        parcel.writeFloat(y);
    }
}
