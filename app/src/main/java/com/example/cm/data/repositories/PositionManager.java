package com.example.cm.data.repositories;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public class PositionManager {

    private static PositionManager instance;
    private final LocationManager locationManager;
    private final LocationListener locationListener;
    private PositionListener positionListener;

    private PositionManager(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = getLocationListener();
    }

    public static PositionManager getInstance(Context context) {
        if (instance == null) {
            instance = new PositionManager(context);
        }
        return instance;
    }

    private LocationListener getLocationListener() {
        return new LocationListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (positionListener == null) {
                    return;
                }

                LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
                positionListener.onPositionChanged(position);
                locationManager.removeUpdates(locationListener);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                LocationListener.super.onProviderEnabled(provider);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                LocationListener.super.onProviderDisabled(provider);
            }
        };
    }

    @SuppressLint("MissingPermission")
    public void requestCurrentLocation(PositionListener listener) {
        positionListener = listener;

        // Both variations are called since sometimes GPS Provider is available but does not return a result
        // TODO: Check callback methods of listener if GPS provider fails and only then call with network provider
        if (hasNetworkProvider()) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
        }
        if (hasGPSProvider()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        }
    }

    public boolean hasGPSProvider() {
        return locationManager.getProvider(LocationManager.GPS_PROVIDER) != null;
    }

    public boolean hasNetworkProvider() {
        return locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null;
    }

    public interface PositionListener {
        void onPositionChanged(LatLng position);
    }
}
