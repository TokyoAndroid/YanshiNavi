package com.zw.yanshinavi.event;

/**
 * 第一次定位成功的回调EventBus类
 *
 * @author zhangwei
 * @since 2019-2-22
 */
public class LocationEvent {

    private double lat;
    private double lon;
    private String streetName;

    public LocationEvent(double lat, double lon, String streetName){
        this.lat = lat;
        this.lon = lon;
        this.streetName = streetName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }
}
