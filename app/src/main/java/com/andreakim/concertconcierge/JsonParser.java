package com.andreakim.concertconcierge;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by spoorthi on 7/25/16.
 */
public class JsonParser {

    private static Response response;

    public static JSONObject getMetroID(String place,String key) {
        try {
            OkHttpClient client = new OkHttpClient();
            String metro_id_url = "http://api.songkick.com/api/3.0/search/locations.json?query=" + place + "&apikey="+key;
            Request request = new Request.Builder()
                    .url(metro_id_url).build();

            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getConcertsFromApi(int metro_id,String key){
        try {
            OkHttpClient client = new OkHttpClient();
            String Main_Url = "http://api.songkick.com/api/3.0/metro_areas/"+metro_id+"/calendar.json?apikey="+key;
            Request request = new Request.Builder()
                    .url(Main_Url).build();
            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static JSONObject getImage(String artist){
        try{
            OkHttpClient client = new OkHttpClient();

            String url = "https://api.spotify.com/v1/search?q="+artist+"&type=artist";

            Request request = new Request.Builder()
                    .url(url).build();

            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getEventDetails(int event_id,String key){
        try{
            OkHttpClient client = new OkHttpClient();
            String url = "http://api.songkick.com/api/3.0/events/"+event_id+".json?apikey="+key;

            Request request = new Request.Builder()
                    .url(url).build();

            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static JSONObject getWeather(String zip) {


        try{

            OkHttpClient client = new OkHttpClient();
            String url = "http://api.openweathermap.org/data/2.5/weather?zip=" + zip + ",us&units=imperial&APPID=" + "53c85317b6fb4f562de7a0120c3663e0";
            Request request = new Request.Builder()
                    .url(url).build();

            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public static JSONObject getParkingDetails(String key,Double lat,Double lng){
        try{
            OkHttpClient client = new OkHttpClient();
            String url = "http://api.parkwhiz.com/search/?lat="+lat+"&lng="+lng+"&start=1474075548&end=1474086348&key="+key;
            Request request = new Request.Builder().url(url).build();
            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
