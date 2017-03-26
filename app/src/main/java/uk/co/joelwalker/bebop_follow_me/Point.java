package uk.co.joelwalker.bebop_follow_me;

import java.io.Serializable;

/**
 * Created by Joel on 24/03/2017.
 */

public class Point implements Serializable {

    private String name, info;

    private double lng, lat;

    private float accuracy;

    private boolean poi;

    public Point(String name, String info, double lng, double lat, float accuracy, boolean poi){
        this. name = name;
        this.info = info;
        this.lng = lng;
        this.lat = lat;
        this.accuracy = accuracy;
        this.poi = poi;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public boolean isPoi() {
        return poi;
    }
}
