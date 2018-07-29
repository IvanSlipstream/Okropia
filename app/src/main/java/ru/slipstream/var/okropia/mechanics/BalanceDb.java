package ru.slipstream.var.okropia.mechanics;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import ru.slipstream.var.okropia.L;
import ru.slipstream.var.okropia.R;
import ru.slipstream.var.okropia.field.City;
import ru.slipstream.var.okropia.field.FieldObject;
import ru.slipstream.var.okropia.field.Location;

public class BalanceDb {

    private static final String ID_TAG = "id";
    private static final String X_TAG = "x";
    private static final String Y_TAG = "y";
    private static final String CITY_TAG = "city";
    private static final String[] OBJECT_TAGS = new String[]{CITY_TAG};
    private static final int[] OBJECT_RESOURCES = new int[]{R.xml.cities};

    private Bundle mLoadedValues;
    private SparseArray<FieldObject> mLoadedObjects;

    public void loadFieldObjects(@NonNull Context context){
        mLoadedObjects = new SparseArray<>();
        for (int i=0;i<OBJECT_TAGS.length;i++){
            String objectTag = OBJECT_TAGS[i];
            int resourceId = OBJECT_RESOURCES[i];
            XmlPullParser parser = context.getResources().getXml(resourceId);
            try {
                int id = 0;
                float x = 0;
                float y = 0;
                Bundle attributes = new Bundle();
                while (parser.getEventType() != XmlPullParser.END_DOCUMENT){
                    if (parser.getEventType() == XmlPullParser.START_TAG &&
                            parser.getName().equals("city")){
                        for (int j=0;j<parser.getAttributeCount();j++){
                            String attributeName = parser.getAttributeName(j);
                            String attributeValue = parser.getAttributeValue(j);
                            switch (attributeName) {
                                case ID_TAG:
                                    id = Integer.parseInt(attributeValue);
                                    break;
                                case X_TAG:
                                    x = Float.parseFloat(attributeValue);
                                    break;
                                case Y_TAG:
                                    y = Float.parseFloat(attributeValue);
                                    break;
                            }
                        }
                        while (parser.getEventType() != XmlPullParser.END_TAG
                                || !parser.getName().equals(objectTag)){
                            parser.next();
                            String attributeType = "";
                            String attributeName = "";
                            if (parser.getEventType() == XmlPullParser.START_TAG &&
                                    parser.getName().equals("attribute")){
                                attributeType = parser.getAttributeValue(null, "type");
                                attributeName = parser.getAttributeValue(null, "name");
                                parser.next();
                                if (parser.getEventType() == XmlPullParser.TEXT){
                                    switch (attributeType){
                                        case "long":
                                            attributes.putLong(attributeName, Long.parseLong(parser.getText()));
                                            break;
                                        case "float":
                                            attributes.putFloat(attributeName, Float.parseFloat(parser.getText()));
                                            break;
                                        case "int":
                                            attributes.putInt(attributeName, Integer.parseInt(parser.getText()));
                                            break;
                                        case "string":
                                        default:
                                            attributes.putString(attributeName, parser.getText());
                                            break;
                                    }
                                }
                            }
                        }
                        City city = new City(new Location(x, y), attributes);
                        mLoadedObjects.put(id, city);
                    }
                    parser.next();
                }
            } catch (XmlPullParserException|IOException|NumberFormatException e) {
                L.d(BalanceDb.class, "Unable to load objects: "+e.getMessage());
            }
        }
    }

    public SparseArray<FieldObject> getFieldObjects(){
        return mLoadedObjects;
    }

    /**
     * A factory to create {@link FieldObject} according to tag name
     * @param location to pass to the constructor
     * @param attributes to pass to the constructor
     * @param tagName a name identifying result type, e.g. 'city'
     * @return FieldObject of specified type
     */
    public static FieldObject newFieldObject(Location location, Bundle attributes, String tagName) {
        switch (tagName){
            case CITY_TAG:
                return new City(location, attributes);
            default:
                return new FieldObject(location, attributes) {
                    @Override
                    public void setDefaultAttributes() {

                    }
                };
        }
    }

}
