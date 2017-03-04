package com.bicyclebnb.groupridefinder.models;

import com.google.android.gms.maps.model.LatLng;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 3/3/17.
 */

public class GroupRideModel extends CoordComparableModel{
    public String name;
    public String dayTime;
    public String url;
    public String eventUrl;
    public String location;
    public String lengthOfRide;
    public String discipline;
    public String notes;

    @Override
    public String coordTitle() {
        return name;
    }

    @Override
    public String coordSnippet() {
        return dayTime;
    }

    private static HashMap<Integer, Double[]> extractLatLangFromHtml(String html) {
        HashMap<Integer, Double[]> result = new HashMap<>();
        Pattern pattern = Pattern.compile("position = new google.maps.LatLng\\(([0-9.-]*),([0-9.-]*)\\);[\n\t\r ]*var marker_([0-9]{1,})");
        Matcher matcher = pattern.matcher(html);
        while(matcher.find()) {
            String group1 = matcher.group(1);
            String group2 = matcher.group(2);
            String group3 = matcher.group(3);

            result.put(Integer.parseInt(group3), new Double[] { Double.parseDouble(group1), Double.parseDouble(group2)});
        }
        return result;
    }

    public static List<CoordComparableModel> parseFromHtml(String html) {
        HashMap<Integer, Double[]> latLangs = extractLatLangFromHtml(html);
        List<CoordComparableModel> result = new ArrayList<>();

        Document doc = Jsoup.parse(html);
        Element snTable = doc.select("table[id='shortcode_list']").first();
        Elements nodes = snTable.select("tr");
        for(int i = 1; i < nodes.size(); i++) {
            Element tr = nodes.get(i);
            GroupRideModel model = new GroupRideModel();
            model.name = tr.select("td:nth-child(1)").text().replace("Add Review", "");
            model.eventUrl = tr.select("td:nth-child(1)>a").attr("href");
            model.dayTime = tr.select("td:nth-child(2)").text();
            model.url = tr.select("td:nth-child(3)>a").attr("href");
            model.location = tr.select("td:nth-child(4)").text();
            model.lengthOfRide = tr.select("td:nth-child(6)").text();
            model.discipline = tr.select("td:nth-child(7)").text();
            model.notes = tr.select("td:nth-child(8)").text();

            if(latLangs.containsKey(i - 1)) {
                model.coordinate = new LatLng(latLangs.get(i - 1)[0], latLangs.get(i - 1)[1]);
                result.add(model);
            }
        }
        return result;
    }
}
