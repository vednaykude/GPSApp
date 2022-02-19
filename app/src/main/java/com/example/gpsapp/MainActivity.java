package com.example.gpsapp;

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.lang.Math.pow;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements LocationListener {

    TextView editLocation;
    double getLongitude;
    double getLatitude;
    LocationManager locationManager;
    float distance;
    ArrayList<String> locationArrayList = new ArrayList<String>();
    ArrayList<String> locationArrayList2 = new ArrayList<String>();
    //use these arraylists to store the time and location, compare time, use that index for the location arraylist
    ArrayList<Double> times = new ArrayList<Double>();
    ArrayList<Double> times2 = new ArrayList<Double>();
    Double favTime = 0.0;
    String favAddress = "None";
    Location getL;
    DecimalFormat d = new DecimalFormat("###.#####");
    DecimalFormat d2 = new DecimalFormat("###.##");
    private long time = SystemClock.elapsedRealtime();
    int i = 0;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editLocation = findViewById(R.id.textView2);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 201);
            return;
        }
        else
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 5, this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(getL == null)
            getL = location;
            i++;
            editLocation.setText("");
            String lng;
            String lt;
            getLongitude = location.getLongitude();
            getLatitude = location.getLatitude();
            double getLongitude1 = abs(getLongitude);
            double getLatitude1 = abs(getLatitude);
            if (location.getLatitude() < 0)
                lt = "S";
            else
                lt = "N";
            if (location.getLongitude() < 0)
                lng = "W";
            else
                lng = "E";
            Toast.makeText(getBaseContext(), "Location Updated!", Toast.LENGTH_SHORT).show();
            String longitude = "Longitude: " + d.format(getLongitude1) + "° " + lng;
            String latitude = "Latitude: " + d.format(getLatitude1) + "° " + lt;
            String address = "";
            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> a;
            try {
                a = geocoder.getFromLocation(getLatitude, getLongitude, 1);
                if (a.size() > 0) {
                    address = a.get(0).getAddressLine(0);
                    Log.d("TAG123", address);
                }
                locationArrayList.add(address);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("TAG123", e.toString());
            }
                String pre = " feet";
                double dtemp = (location.distanceTo(getL) * 3.28084);
                Log.d("TAG12", String.valueOf(dtemp));
                distance += dtemp;
                double elapsedTime = (SystemClock.elapsedRealtime() - time)/1000.0;
                if(i == 1)
                    elapsedTime = 0;
                times.add(elapsedTime);
                for(int i = 0; i < times.size(); i++){
                    //favTime = 0.0;
                    //Double too = 0.0;
                    if (i != 0) {
                        /*for(int j = 0; j < locationArrayList.size(); j++) {
                            if(j != 0) {
                                if (locationArrayList.get(j-1).equals(favAddress)) {
                                    too += times.get(j) - times.get(j - 1);
                                }
                            }
                        }*/
                        Double temp = times.get(i) - times.get(i-1);
                        if (temp > favTime) {
                            favTime = temp;
                            favAddress = locationArrayList.get(i-1);
                            //too = 0.0;
                        }
                    }
                }
            String s = latitude + "\n" + longitude + "\nAddress: " + address + "\nDistance travelled: " + d2.format(distance) + pre + "\nTime elapsed: " + elapsedTime + " seconds" + "\nFavorite Location: " + favAddress + "\nTime Spent: " + d2.format(favTime) + " seconds";
            editLocation.setText(s);
            getL = location;
    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("TAG", String.valueOf(requestCode));
        switch (requestCode) {
            case 201: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 5, this);
                }
            }
        }

    }

}