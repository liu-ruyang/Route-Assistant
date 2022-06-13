package com.example.perfectnavigationassistant;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;

public class MyPoiSearcher implements PoiSearch.OnPoiSearchListener {
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        System.out.println("搜索到多条记录");
        ArrayList<PoiItem> pois = poiResult.getPois();
        PoiItem poiItem = pois.get(0);
        LatLonPoint latLonPoint = poiItem.getLatLonPoint();
        System.out.println("name：" + poiItem.getAdName());
        System.out.println("poiid：" + poiItem.getPoiId());
        System.out.println("location：" + poiItem.getLatLonPoint());
        System.out.println("latitude：" + latLonPoint.getLatitude());
        System.out.println("longitude：" + latLonPoint.getLongitude());
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
        System.out.println("搜索到单条记录");
        LatLonPoint latLonPoint = poiItem.getLatLonPoint();
        System.out.println("name：" + poiItem.getAdName());
        System.out.println("poiid：" + poiItem.getPoiId());
        System.out.println("location：" + poiItem.getLatLonPoint());
        System.out.println("latitude：" + latLonPoint.getLatitude());
        System.out.println("longitude：" + latLonPoint.getLongitude());
    }
}
