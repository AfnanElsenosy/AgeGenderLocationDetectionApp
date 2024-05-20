package com.example.myfaceapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSTracker implements LocationListener {

    private Context context;
    private LocationManager locationManager;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;
    private Location location;
    private double latitude;
    private double longitude;
    private String cityName;
    private String countryName;

    public GPSTracker(Context context) {
        this.context = context;
        getLocation();
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // Get GPS and network status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No location provider is enabled
                Log.e("GPSTracker", "No location provider is enabled");
            } else {
                this.canGetLocation = true;

                // Check permissions
                if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED &&
                        context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    Log.e("GPSTracker", "Location permissions not granted");
                }

                // If permissions are granted
                if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    if (isGPSEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                getCityAndCountryName();
                            }
                        }
                    }

                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 10, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                getCityAndCountryName();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCityAndCountryName() {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                cityName = addresses.get(0).getLocality();
                countryName = addresses.get(0).getCountryName();
                Log.d("GPSTracker", "City: " + cityName + ", Country: " + countryName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    @Override
    public void onLocationChanged(Location location) {
        // Not needed for now, but required by LocationListener
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Not needed for now, but required by LocationListener
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Not needed for now, but required by LocationListener
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Not needed for now, but required by LocationListener
    }

    public boolean isGPSEnabled() {
        return isGPSEnabled;
    }
}
