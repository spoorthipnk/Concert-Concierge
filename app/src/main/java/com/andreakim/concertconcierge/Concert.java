package com.andreakim.concertconcierge;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by andreakim on 7/25/16.
 */
public class Concert {
    String name;
    String venue;
    String date;
    String time;
    String image_url;
    double dist;

    public double getDist() {
        return dist;
    }

    public void setDist(Float dist) {
        this.dist = dist;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    Context context;
    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    int id;






    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getCity() {
        return city;
    }










    public void setCity(String city) {
        this.city = city;
    }

    String city;

    public Concert(String name, String venue, String city, String time,String image_url ,int id,Context context, double dist) {


        this.name = name;
        this.venue = venue;
        this.city = city;
        this.time = time;
        this.image_url = image_url;
        this.context=context;
        this.id = id;
        this.dist=dist;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}