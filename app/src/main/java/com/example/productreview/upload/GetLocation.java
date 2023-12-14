package com.example.productreview.upload;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


import androidx.core.app.ActivityCompat;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class GetLocation {

    private Context context;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private GetLocationUpdate getLocationUpdate;

    public GetLocation(Context context, GetLocationUpdate getLoc) {
        this.context = context;
        this.getLocationUpdate = getLoc;
    }


    public void updateLocation() {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location.hasAccuracy()) {
                    getLocationUpdate.currentLocation(location);
                    locationManager.removeUpdates(locationListener);
                }

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

        if (ActivityCompat.checkSelfPermission
                (context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        List<String> allProviders = locationManager.getAllProviders();
        for (String name : allProviders) {
            locationManager.requestLocationUpdates(name, 1000, 0, locationListener);

        }
    }


    public interface GetLocationUpdate {
        void currentLocation(Location location);
    }
}
