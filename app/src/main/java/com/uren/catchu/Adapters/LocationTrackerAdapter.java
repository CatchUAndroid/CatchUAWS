package com.uren.catchu.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.ShareItems;

import java.math.BigDecimal;

import static com.uren.catchu.SharePackage.ShareDetailActivity.REQUEST_CODE_ENABLE_LOCATION;

public class LocationTrackerAdapter implements LocationListener {

    private Context mContext;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private Location location;
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
        Log.i("Info", "Location changed lat :" + location.getLatitude());
        Log.i("Info", "Location changed long:" + location.getLongitude());
        CommonUtils.showToast(mContext, "Location changed lat :" + location.getLatitude());

        catchu.model.Location locationModel = new catchu.model.Location();
        locationModel.setLatitude(BigDecimal.valueOf(location.getLatitude()));
        locationModel.setLongitude(BigDecimal.valueOf(location.getLongitude()));
        ShareItems.getInstance().setLocation(locationModel);
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

    public LocationTrackerAdapter(Context context) {
        mContext = context;
        permissionModule = new PermissionModule(mContext);
    }

    public boolean canGetLocation() {

        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled)
            return false;
        else
            return true;
    }

    public Location getLocation() {
        try {
            // First get location from Network Provider
            if (isNetworkEnabled) {

                if (permissionModule.checkAccessFineLocationPermission()) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, (android.location.LocationListener) this);
                }


                if (permissionModule.checkAccessFineLocationPermission()) {
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
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (location == null) {
                    if (permissionModule.checkAccessFineLocationPermission()) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, (android.location.LocationListener) mContext);
                    }

                    if (locationManager != null) {

                        // TODO: 3.09.2018 - getBestProvider kullanilabilir mi bakalim...
                        //Criteria criteria = new Criteria();
                        //location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

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
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.error) + e.getMessage());
        }

        return location;
    }

    /*public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle(mContext.getResources().getString(R.string.gpsSettings));

        alertDialog.setMessage(mContext.getResources().getString(R.string.gpsSettingMessage));
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(mContext.getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });


        alertDialog.show();
    }*/
}
