package com.andreakim.concertconcierge;

import android.annotation.TargetApi;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, RecyclerViewClickListener, GoogleApiClient.ConnectionCallbacks {
    private LocationRequest mLocationRequest;
    private RecyclerView recyclerView;
    private ConcertAdapter concertAdapter;
    static private ArrayList<Concert> list_concerts;
    String place;
    String name, date, venue, time, artist, venue_lat, venue_lng, city,image_url;
    int event_id = 0;
    private GoogleApiClient mGoogleApiClient;
    private ProgressBar progressBar;
    Double lat, lng;
    LocationManager locationManager;
    Context context ;
    private int progressStatus =0;
    private android.os.Handler handler = new android.os.Handler();
    android.location.LocationListener locationListener = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(getApplicationContext(), "Location updated", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }


        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };



    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildGoogleApiClient();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria crta = new Criteria();
        crta.setAccuracy(Criteria.ACCURACY_FINE);
        crta.setAltitudeRequired(true);
        crta.setBearingRequired(true);
        crta.setCostAllowed(true);
        crta.setPowerRequirement(Criteria.POWER_LOW);
        String provider = LocationManager.GPS_PROVIDER;
        try {
            long interval = 100;
            float ms = 1;
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {


                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

                } else {

                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            200);
                }
            }


            locationManager.requestLocationUpdates(provider,interval,ms,locationListener );

            Location location = locationManager.getLastKnownLocation(provider);
            if(location!=null) {
                lat = location.getLatitude();
                lng = location.getLongitude();
                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                updateProgressBar();
                launchDataAsyc(lat, lng);
            }


        }
        catch (SecurityException e){
            e.printStackTrace();
        }

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place_searched) {
                // TODO: Get info about the selected place.
                Log.i("Hey", "Place: " + place_searched.getName());

                LatLng latLng = place_searched.getLatLng();
                Double lat = latLng.latitude;
                Double lng = latLng.longitude;
                launchDataAsyc(lat, lng);
                updateProgressBar();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("Hey", "An error occurred: " + status);
            }
        });
    }

    public void updateProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(10);
        progressBar.setProgress(0);
        progressStatus=0;
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            if(progressStatus == 10){
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        //Just to display the progress slowly
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void launchDataAsyc(Double lat, Double lng) {
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0)
                place = addresses.get(0).getLocality();
            String toastMsg = String.format("Place: %s", addresses.get(0).getLocality());
            Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
            list_concerts = new ArrayList<Concert>();
            // place = txt_search.getText().toString();
            new DataAsync().execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("error", "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected (Bundle connectionHint){
        //    super.
        try {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        list_concerts.get(position);
        Intent intent = new Intent(MainActivity.this,ConcertDetailActivity.class);
        intent.putExtra("event_id",event_id);
        startActivity(intent);
    }


    private class DataAsync extends AsyncTask<Void, Integer, Void> {
        int metro_id = 0;

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }


        @Override
        protected Void doInBackground(Void... params) {
            String key = getResources().getString(R.string.songkick_api);

            JSONObject jsonObject = JsonParser.getMetroID(place,key);
            if (jsonObject != null) {
                if (jsonObject.length() > 0) {
                    try {
                        JSONArray jsonArray = jsonObject.getJSONObject("resultsPage").getJSONObject("results").getJSONArray("location");
                        metro_id = jsonArray.getJSONObject(0).getJSONObject("metroArea").getInt("id");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            JSONObject concert_jsonObject = JsonParser.getConcertsFromApi(metro_id,key);
            if (concert_jsonObject != null) {
                if (concert_jsonObject.length() > 0) {
                    try {
                        JSONArray jsonArray = concert_jsonObject.getJSONObject("resultsPage").getJSONObject("results").getJSONArray("event");
                        int length = jsonArray.length();
                        if (length > 0) {
                            for (int i = 0; i < length; i++) {
                                JSONObject innerObject = jsonArray.getJSONObject(i);
                                name = innerObject.getString("displayName");
                                date = innerObject.getJSONObject("start").getString("date");
                                time = innerObject.getJSONObject("start").getString("time");
                                venue = innerObject.getJSONObject("venue").getString("displayName");
                                venue_lat = innerObject.getJSONObject("venue").getString("lat");
                                venue_lng = innerObject.getJSONObject("venue").getString("lng");
                                city = innerObject.getJSONObject("location").getString("city");
                                event_id = innerObject.getInt("id");
                                context = getApplicationContext();

                                JSONArray jsonArray_forArtist = innerObject.getJSONArray("performance");
                                JSONObject innerObject_artist = jsonArray_forArtist.getJSONObject(0);
                                artist = innerObject_artist.getJSONObject("artist").getString("displayName");

                                Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.concerttwo);
                                JSONObject images_JsonObject = JsonParser.getImage(artist);
                                if (images_JsonObject != null) {
                                    JSONArray items_JsonArray = images_JsonObject.getJSONObject("artists").getJSONArray("items");
                                    if (items_JsonArray != null) {
                                        JSONObject image_inner_object = items_JsonArray.getJSONObject(0);
                                        JSONArray image_array = image_inner_object.getJSONArray("images");
                                        JSONObject image_obj = image_array.getJSONObject(1);
                                         image_url = image_obj.getString("url");

                                    }
                                }



                                Concert concert = new Concert(name, venue, city, time, image_url, event_id, context);
                                list_concerts.add(concert);
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            //  Log.e("Hey",name);
            recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
            recyclerView.setElevation((float) 1.00);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);
            concertAdapter = new ConcertAdapter(list_concerts, new RecyclerViewClickListener() {
                @Override
                public void recyclerViewListClicked(View v, int position) {
                    list_concerts.get(position);
                    Intent intent = new Intent(MainActivity.this,ConcertDetailActivity.class);
                    int eventid = list_concerts.get(position).getId();
                    intent.putExtra("event_id",eventid);
                    startActivity(intent);
                }
            });
            recyclerView.setAdapter(concertAdapter);

        }

    }

}
