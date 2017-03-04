package com.bicyclebnb.groupridefinder.models;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 3/3/17.
 */

public class BikeShopModel extends CoordComparableModel {
    public String name;
    public String location;
    public String phone;
    public String url;

    @Override
    public String coordTitle() {
        return name;
    }

    @Override
    public String coordSnippet() {
        return phone;
    }


    public static List<CoordComparableModel> parseFromHtml(String html) {
        List<CoordComparableModel> result = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(html);
            JSONObject feed = json.getJSONObject("feed");
            JSONArray entry = feed.getJSONArray("entry");
            for(int i = 0; i < entry.length(); i++) {
                JSONObject row = entry.getJSONObject(i);
                String approved = row.getJSONObject("gsx$approved").getString("$t");
                if(approved.equals("YES")) {
                    BikeShopModel model = new BikeShopModel();
                    String street = row.getJSONObject("gsx$street").getString("$t");
                    String city = row.getJSONObject("gsx$city").getString("$t");
                    String state = row.getJSONObject("gsx$state").getString("$t");
                    String zip = row.getJSONObject("gsx$zip").getString("$t");
                    model.location = String.format("%s, %s, %s, %s", street, city, state, zip);
                    model.phone = row.getJSONObject("gsx$phone").getString("$t");
                    model.name = row.getJSONObject("gsx$shopname").getString("$t");
                    model.url = row.getJSONObject("gsx$website").getString("$t");

                    String lat = row.getJSONObject("gsx$latitude").getString("$t");
                    String lang = row.getJSONObject("gsx$longitude").getString("$t");
                    model.coordinate = new LatLng(Double.parseDouble(lat), Double.parseDouble(lang));
                    result.add(model);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
