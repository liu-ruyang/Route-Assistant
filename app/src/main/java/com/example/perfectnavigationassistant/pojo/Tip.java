package com.example.perfectnavigationassistant.pojo;

public class Tip {
    private String placeName;
    private String poiId;
    private String districtAndAddress;//所属区域：省+市+区（直辖市为“市+区“）

    public Tip(String placeName, String poiId, String districtAndAddress) {
        this.placeName = placeName;
        this.poiId = poiId;
        this.districtAndAddress = districtAndAddress;
    }

    public Tip() {
    }

    @Override
    public String toString() {
        return "Tip{" +
                "placeName='" + placeName + '\'' +
                ", poiId='" + poiId + '\'' +
                ", districtAndAddress='" + districtAndAddress + '\'' +
                '}';
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }

    public String getDistrictAndAddress() {
        return districtAndAddress;
    }

    public void setDistrictAndAddress(String districtAndAddress) {
        this.districtAndAddress = districtAndAddress;
    }
}
