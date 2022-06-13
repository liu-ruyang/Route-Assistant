package com.example.perfectnavigationassistant.pojo;

import java.io.Serializable;

//@Data
//@Accessors(chain = true)
//@AllArgsConstructor
//@NoArgsConstructor
public class Place implements Serializable {
    public String name;
    /*经、纬度*/
    public String location;
    public String poiid;
    /*经度*/
    public String longitude;
    /*纬度*/
    public String latitude;

    public Place() {
    }

    public Place(String name, String location, String poiid, String longitude, String latitude) {
        this.name = name;
        this.location = location;
        this.poiid = poiid;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPoiid() {
        return poiid;
    }

    public void setPoiid(String poiid) {
        this.poiid = poiid;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", poiid='" + poiid + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                '}';
    }
}
