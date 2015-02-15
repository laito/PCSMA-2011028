package com.shingeku.root.pcsmaassignment3;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * Created by root on 2/12/2015.
 */
public class Video {

    private String ID;
    private String Name;
    private String Description;
    private String Duration;
    private String Type;
    private String Rating;

    static String[] validAttributes = new String[]{"ID", "Name", "Description", "Duration", "Type", "Rating"};

    public Video(JSONObject video) throws JSONException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for(String key: validAttributes) {
            String attribute = video.getString(key);
            Method m = Video.class.getMethod("set"+attribute, new Class[] { String.class });
            m.invoke(this, attribute);
        }
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        this.Duration = duration;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        this.Rating = rating;
    }

}
