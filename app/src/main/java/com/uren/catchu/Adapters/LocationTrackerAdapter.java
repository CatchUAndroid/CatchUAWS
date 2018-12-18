package com.uren.catchu.Adapters;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.LocationCallback;

import static android.content.Context.LOCATION_SERVICE;

public class LocationTrackerAdapter implements LocationListener {

    private Context mContext;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private Location location;
    LocationCallback locationCallback;
    private double latitude;
    private double longitude;

    // The minimum distance to change Updates in meters
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000; // 1 sec
    private final String TAG = "LocationTracker";

    protected LocationManager locationManager;
    PermissionModule permissionModule;

    @Override
    public void onLocationChanged(Location location) {
        locationCallback.onLocationChanged(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public LocationTrackerAdapter(Context context, LocationCallback locationCallback) {
        mContext = context;
        permissionModule = new PermissionModule(mContext);
        this.locationCallback = locationCallback;
    }

    public boolean canGetLocation() {

        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled)
                return false;
            else
                return true;
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, LocationTrackerAdapter.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return false;
    }

    public Location getLocation() {
        try {
            setLocationFromNetworkProvider();
            setLocationFromGPSProvider();
            setLocationFromBestProvider();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, LocationTrackerAdapter.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return location;
    }

    public void setLocationFromNetworkProvider() {
        try {
            if (isNetworkEnabled && location == null) {
                if (permissionModule.checkAccessFineLocationPermission()) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, LocationTrackerAdapter.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void setLocationFromGPSProvider() {
        try {
            if (isGPSEnabled && location == null) {
                if (permissionModule.checkAccessFineLocationPermission()) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, LocationTrackerAdapter.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void setLocationFromBestProvider() {
        try {
            if (location == null) {
                String bestProvider;
                locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

                Criteria criteria = new Criteria();
                bestProvider = locationManager.getBestProvider(criteria, false);

                if (permissionModule.checkAccessFineLocationPermission()) {
                    location = locationManager.getLastKnownLocation(bestProvider);
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, LocationTrackerAdapter.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }
}