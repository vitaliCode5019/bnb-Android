package com.bicyclebnb.groupridefinder.models;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 3/3/17.
 */

public class AccommodationModel extends CoordComparableModel {
    public String imageUrl;
    public String name;
    public String price;
    public String location;
    public String category;
    public String url;

    @Override
    public String coordTitle() {
        return name;
    }

    @Override
    public String coordSnippet() {
        return price;
    }


    public static List<CoordComparableModel> parseFromHtml(String html) {
        List<CoordComparableModel> result = new ArrayList<>();

        Pattern pattern = Pattern.compile("var googlecode_property_vars = (\\{.*\\})");
        Matcher matcher = pattern.matcher(html);
        while(matcher.find()) {
            String json = matcher.group(1);

            try {
                json = java.net.URLDecoder.decode(json, "UTF-8");
                JSONArray markers = new JSONArray(new JSONObject(json).getString("markers"));
                for(int i = 0; i < markers.length(); i++) {
                    JSONArray marker = markers.getJSONArray(i);
                    AccommodationModel model = new AccommodationModel();
                    model.name = marker.getString(0);
                    String lat = marker.getString(1);
                    String lang = marker.getString(2);
                    model.coordinate = new LatLng(Double.parseDouble(lat), Double.parseDouble(lang));
                    model.imageUrl = marker.getString(4);
                    model.price = marker.getString(5);
                    model.url = marker.getString(9);
                    String cat1 = marker.getString(17);
                    String cat2 = marker.getString(18);
                    if(cat2.isEmpty()) model.category = cat1.toUpperCase();
                    else model.category = cat1.toUpperCase() + "/" + cat2.toUpperCase();
                    String loc1 = marker.getString(11);
                    String loc2 = marker.getString(12);
                    if(loc2.isEmpty()) model.location = loc1.toUpperCase();
                    else model.location = loc1.toUpperCase() + "/" + loc2.toUpperCase();
                    model.location = model.location.replace("-", " ");
                    result.add(model);
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return result;
    }
}
