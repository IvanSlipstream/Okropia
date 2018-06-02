package ru.slipstream.var.okropia.field;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import ru.slipstream.var.okropia.L;
import ru.slipstream.var.okropia.mechanics.Clicker;

/**
 * Created by Slipstream-DESKTOP on 18.02.2018.
 */

public class City extends FieldObject {

    private static final float SPECIFIC_RADIUS = 0.00005f;
    private static final float INTERNAL_RADIUS_MIN = 0.54f;
    private static final float INTERNAL_RADIUS_MAX = 0.82f;
    private static final float EXTERNAL_RADIUS = 1f;

    public static final class AttributeKeys {

        public static final String POPULATION = "population";

        public static final String AFFILIATION = "affiliation";
        public static final String LOYALTY = "loyalty";
        public static final String WEALTH = "wealth";
        public static final String NAME = "name";
    }

    public City(Location location, Bundle attributes) {
        super(location, attributes);
        adjustSize(attributes);
        this.mAngle = 0;
    }

    private void adjustSize(Bundle attributes) {
        this.mSize = SPECIFIC_RADIUS * attributes.getLong(AttributeKeys.POPULATION);
    }

    public City(Parcel parcel) {
        super(parcel);
    }

    @Override
    public void setDefaultAttributes() {
        mAttributes.putLong(AttributeKeys.POPULATION, 1000);
        mAttributes.putBoolean(AttributeKeys.AFFILIATION, true);
        mAttributes.putFloat(AttributeKeys.LOYALTY, 1f);
        mAttributes.putFloat(AttributeKeys.WEALTH, 1f);
        mAttributes.putString(AttributeKeys.NAME, "Город");
    }

    @Override
    public void onSelect(Clicker clicker, Clicker.CommandState state) {
        L.d(getClass(), "City selected");
    }

    @Override
    public void draw(Canvas canvas, float scale, Location pivot) {
        adjustSize(mAttributes);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boolean affiliation = mAttributes.getBoolean(AttributeKeys.AFFILIATION);
        paint.setColor(affiliation ? Color.BLUE : Color.DKGRAY);
        // frame and text color based on affiliation
        PointF center = getScaledCoordinates(canvas, scale, pivot);
        float radius = EXTERNAL_RADIUS * getScaledSize(canvas, scale);
        // name
        // TODO: 18.03.2018 select text direction
        paint.setTextSize(radius * 0.6f);
        canvas.drawText(mAttributes.getString(AttributeKeys.NAME, ""),
                center.x - radius, center.y - radius * 1.6f, paint);
        // frame
        canvas.drawCircle(center.x, center.y, radius, paint);
        // sectors based on loyalty
        float loyalty = mAttributes.getFloat(AttributeKeys.LOYALTY);
        paint.setColor(Color.RED);
        // frame radius based on wealth
        float wealth = mAttributes.getFloat(AttributeKeys.WEALTH);
        radius = (INTERNAL_RADIUS_MAX + wealth * (INTERNAL_RADIUS_MIN - INTERNAL_RADIUS_MAX) ) * getScaledSize(canvas, scale);
        canvas.drawCircle(center.x, center.y, radius, paint);
        paint.setColor(Color.GREEN);
        canvas.drawArc(new RectF(center.x - radius, center.y - radius,
                center.x + radius, center.y + radius),
                -90, loyalty * 360, true, paint);
    }

    public static final Parcelable.Creator<City> CREATOR = new Parcelable.Creator<City>() {

        @Override
        public City createFromParcel(Parcel parcel) {
            return new City(parcel);
        }

        @Override
        public City[] newArray(int i) {
            return new City[i];
        }
    };

}
