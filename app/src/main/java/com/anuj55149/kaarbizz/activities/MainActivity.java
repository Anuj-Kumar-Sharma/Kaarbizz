package com.anuj55149.kaarbizz.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anuj55149.kaarbizz.R;
import com.anuj55149.kaarbizz.utilities.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener, View.OnClickListener {

    private Context context;
    private boolean isLocationPermissionGranted = false;

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private static final int SEARCH_ACTIVITY_REQUEST_CODE = 11;

    private GoogleMap map;
    private FusedLocationProviderClient locationProviderClient;
    private static final float defaultZoom = 13;

    private CardView searchCard, gpsButton;
    private TextView tvSearch;
    private ImageView ivCancel, ivSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isServicesOK()) {
            init();
        }
    }

    private void init() {
        context = this;
        getPermissions();
    }

    private void initViews() {
        searchCard = findViewById(R.id.xTopSearchLayout);
        tvSearch = findViewById(R.id.tvSearch);
        ivCancel = findViewById(R.id.ivCancel1);
        ivSearch = findViewById(R.id.ivSearch);
        gpsButton = findViewById(R.id.cvGpsLocation);

        gpsButton.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
    }


    private void initMap() {
        initViews();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                if (isLocationPermissionGranted) {
                    getDeviceLocation(false);
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    map.setMyLocationEnabled(true);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    map.getUiSettings().setRotateGesturesEnabled(false);
                    map.setOnCameraMoveStartedListener(MainActivity.this);
                    map.setOnCameraIdleListener(MainActivity.this);
                }
            }
        });
    }

    private void getDeviceLocation(final boolean animate) {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(context);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final Task locationTask = locationProviderClient.getLastLocation();
        locationTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()) {
                    Location location = (Location) locationTask.getResult();
                    moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), defaultZoom, animate);
                } else {
                    // Location Not found
                }
            }
        });
    }

    private void moveCamera(LatLng latLng, float zoom, boolean animate) {
        if(animate) map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        else map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void getPermissions() {
        String permission[] = {Constants.PERMISSION_FINE_LOCATION, Constants.PERMISSION_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(context, Constants.PERMISSION_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(context, Constants.PERMISSION_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        isLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                for(int grantResult: grantResults) {
                    if(grantResult != PackageManager.PERMISSION_GRANTED) return;
                }
                isLocationPermissionGranted = true;

                initMap();
        }
    }

    public boolean isServicesOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    public void onCameraMoveStarted(int i) {
        gpsButton.setVisibility(View.GONE);
    }

    @Override
    public void onCameraIdle() {
        gpsButton.setVisibility(View.VISIBLE);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSearch:
            case R.id.ivSearch:
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra(Constants.SEARCH_REULT_BACK_INTENT, tvSearch.getText().toString());
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(this, searchCard, "searchCard");
                startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST_CODE, options.toBundle());
                break;
            case R.id.ivCancel1:
                tvSearch.setText("");
                ivCancel.setVisibility(View.GONE);
                break;
            case R.id.cvGpsLocation:
                gpsButton.setVisibility(View.GONE);
                getDeviceLocation(true);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SEARCH_ACTIVITY_REQUEST_CODE:
                if(resultCode == RESULT_OK && data != null) {
                    String searchText = data.getStringExtra(Constants.SEARCH_REULT_BACK_INTENT);
                    if(searchText != null) {
                        tvSearch.setText(searchText);
                        if(!searchText.isEmpty()) ivCancel.setVisibility(View.VISIBLE);
                        else ivCancel.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }
}
