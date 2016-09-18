package com.andreakim.concertconcierge;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.text.format.DateFormat.getDateFormat;

public class DetailScrollingActivity extends AppCompatActivity {
    int event_id;
    private String weather_temp;
    private String description;
    private CardView mapCard;
    private String parking_url;
    private Button btn_parking;
    String image_url;
    ImageView background;

    String city, lat, lng, popularity, uri, event_name, id, time, date, ageRestriction, zip, venue_name, street, phone, venue_description;
    private TextView txt_name, txt_city, txt_popularity, txt_uri, txt_time, txt_date, txt_ageRestriction, txt_venue, txt_phone, currentTemp, mDescription;
    private  com.google.android.gms.maps.MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);

        image_url = getIntent().getStringExtra("image_url");
        new BackgroundImgAsync().execute();


        currentTemp = (TextView) findViewById(R.id.weather_temp);
        mDescription = (TextView) findViewById(R.id.weather_desc);
        event_id = getIntent().getIntExtra("event_id", 0);
        txt_name = (TextView) findViewById(R.id.concert_txtview_name);
        txt_uri = (TextView) findViewById(R.id.concert_txtUri);
        txt_time = (TextView) findViewById(R.id.concert_txtview_time);
        txt_date = (TextView)findViewById(R.id.concert_txtview_date);
        txt_city=(TextView)findViewById(R.id.concert_txtcity);
        txt_phone=(TextView)findViewById(R.id.txt_phone);
        txt_popularity=(TextView)findViewById(R.id.txt_popularity);
        https://github.com/spoorthipnk/Concert-Concierge.git
        txt_ageRestriction = (TextView) findViewById(R.id.concert_txtAge);
        txt_venue = (TextView) findViewById(R.id.concert_txtview_venue);
        mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        btn_parking = (Button)findViewById(R.id.btn_parking);
        // background = (ImageView)findViewById(R.id.background) ;


        new EventAsync().execute();
        //    new WeatherTask().execute();

        btn_parking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ParkAsync().execute();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    private class BackgroundImgAsync extends AsyncTask<Void,Void,Void> {
        Bitmap background_img;

        @Override
        protected Void doInBackground(Void... voids) {

            OkHttpClient client = new OkHttpClient();

            try {
                Request request = new Request.Builder()
                        .url(image_url)
                        .build();

                Response response = client.newCall(request).execute();

                byte[] image = response.body().bytes();
                background_img = BitmapFactory.decodeByteArray(image,0,image.length);


            }
            catch (Exception e)
            {
                e.printStackTrace();

            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            RelativeLayout rl = (RelativeLayout)findViewById(R.id.event_detail_layout);
            Drawable d = new BitmapDrawable(background_img);
            rl.setBackground(d);
            Random random=new Random();
            ColorFilter cf = new PorterDuffColorFilter(Color.rgb(0, random.nextInt(42),random.nextInt(79)), PorterDuff.Mode.MULTIPLY  );
            // rl.setAlpha((float) 0.6);
            rl.getBackground().setColorFilter(cf);

            // background.setImageBitmap(background_img);
            //   background.setColorFilter(cf);
        }
    }

    private class WeatherTask extends AsyncTask<Void, Void, Void> {
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject weatherJson = JsonParser.getWeather(zip);
            // fetch weather
            // get json
            // put in mWeatherModel
            //mWeatherModel.mDescription = ...
            if (weatherJson != null && weatherJson.length() > 0) {

                try {
                    weather_temp = weatherJson.getJSONObject("main").getString("temp");
                    JSONArray weatherArray = weatherJson.getJSONArray("weather");

                    for (int i = 0; i < weatherArray.length(); i++) {

                        JSONObject innerObject = weatherArray.getJSONObject(i);
                        description = innerObject.getString("description");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute (Void empty){

            currentTemp.setText(weather_temp + "°F");
            mDescription.setText(description);
        }
    }


    private class EventAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String key = getResources().getString(R.string.songkick_api);

            JSONObject event_responseObject = JsonParser.getEventDetails(event_id, key);

            if (event_responseObject != null && event_responseObject.length() > 0) {

                try {
                    JSONObject eventObject = event_responseObject.getJSONObject("resultsPage").getJSONObject("results").getJSONObject("event");

                    city = eventObject.getJSONObject("location").getString("city");
                    lat = eventObject.getJSONObject("location").getString("lat");
                    lng = eventObject.getJSONObject("location").getString("lng");
                    popularity = eventObject.getString("popularity");
                    uri = eventObject.getString("uri");
                    event_name = eventObject.getString("displayName");
                    date = eventObject.getJSONObject("start").getString("date");
                    time = eventObject.getJSONObject("start").getString("time");
                    ageRestriction = eventObject.getString("ageRestriction");
                    JSONObject venue_object = eventObject.getJSONObject("venue");
                    venue_name = venue_object.getString("displayName");
                    street = venue_object.getString("street");
                    phone = venue_object.getString("phone");
                    zip = venue_object.getString("zip");
                    GoogleMapOptions options = new GoogleMapOptions().liteMode(true);

                    //     venue_description =venue_object.getString("venue_description");


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            txt_name.setText(event_name);
            if (ageRestriction == "null") {
                txt_ageRestriction.setText("No age restriction");
            } else {
                txt_ageRestriction.setText(ageRestriction);
            }

            java.text.DateFormat dateFormat = getDateFormat(getApplicationContext());
            if (date!=null)
                txt_date.setText(date);
            txt_time.setText(time);
            txt_popularity.setText(popularity);
            txt_uri.setText(uri);

            txt_uri.setClickable(true);
            txt_uri.setMovementMethod(LinkMovementMethod.getInstance());
            String text = "<a href='" + uri + "'>" + event_name + " </a>";
            txt_uri.setText(Html.fromHtml(text));
            txt_city.setText(city);
            txt_phone.setText(phone);
            String venue = venue_name + "\n" + street + "\n" + zip;
            txt_venue.setText(venue);




        }
    }

    private class ParkAsync extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            String key = getResources().getString(R.string.parkwhiz_key);
            JSONObject parking_responseObj = JsonParser.getParkingDetails(key,Double.valueOf(lat),Double.valueOf(lng));
            try {
                parking_url = parking_responseObj.getString("parkwhiz_url");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.parkwhiz.com/p/chicago-parking/180-n-franklin-st"));
            startActivity(intent);

        }

    }
}
