package com.anuj55149.kaarbizz.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anuj55149.kaarbizz.R;
import com.anuj55149.kaarbizz.dao.DealerDao;
import com.anuj55149.kaarbizz.dao.ServerStateDao;
import com.anuj55149.kaarbizz.models.Dealer;
import com.anuj55149.kaarbizz.utilities.Constants;
import com.anuj55149.kaarbizz.utilities.DialogBoxes;
import com.anuj55149.kaarbizz.utilities.SharedPreference;
import com.anuj55149.kaarbizz.volley.RequestCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener, View.OnClickListener, RequestCallback {

    private Context context;
    private boolean isLocationPermissionGranted = false;

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private static final int SEARCH_ACTIVITY_REQUEST_CODE = 11;

    private GoogleMap map;
    private static final float defaultZoom = 13;

    private SharedPreference pref;

    private DrawerLayout drawerLayout;
    private CardView searchCard;
    private FloatingActionButton fab;
    private TextView tvSearch, tvUserName, tvUserEmail;
    private ImageView ivCancel, ivMenu, ivUserImage;
    private NavigationView navigationView;

    private FirebaseAuth auth;
    private RelativeLayout rlLoginHeader, rlUserHeader;

    private DealerDao dealerDao;
    private ServerStateDao serverStateDao;
    private ArrayList<Dealer> nearestDealers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        pref = new SharedPreference(context);

        if (!pref.getIsLaterClicked()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
            return;
        }

        initViews();
        if (isServicesOK()) {
            init();
        }
    }

    private void init() {
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            updateActivityWithUserCredentials();
        }

        getPermissions();
    }

    private void updateActivityWithUserCredentials() {
        rlLoginHeader.setVisibility(View.GONE);
        rlUserHeader.setVisibility(View.VISIBLE);
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            tvUserName.setText(user.getDisplayName());
            tvUserEmail.setText(user.getEmail());
            Glide.with(context)
                    .asBitmap()
                    .apply(RequestOptions.circleCropTransform().placeholder(R.drawable.ic_user))
                    .load(user.getPhotoUrl())
                    .into(ivUserImage);
        }
    }

    private void initViews() {
        searchCard = findViewById(R.id.xTopSearchLayout);
        tvSearch = findViewById(R.id.tvSearch);
        ivCancel = findViewById(R.id.ivCancel1);
        ivMenu = findViewById(R.id.ivMenu);
        fab = findViewById(R.id.fabGpsLocation);
        drawerLayout = findViewById(R.id.mainDrawerLayout);
        navigationView = findViewById(R.id.navigationView);
        View navHeaderLayout = navigationView.getHeaderView(0);
        rlUserHeader = navHeaderLayout.findViewById(R.id.rlUserHeader);
        rlLoginHeader = navHeaderLayout.findViewById(R.id.rlLoginHeader);
        tvUserName = navHeaderLayout.findViewById(R.id.tvUserName);
        tvUserEmail = navHeaderLayout.findViewById(R.id.tvUserEmail);
        ivUserImage = navHeaderLayout.findViewById(R.id.ivUserImage);

        fab.setOnClickListener(this);
        ivMenu.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
        rlLoginHeader.setOnClickListener(this);

        nearestDealers = new ArrayList<>();
        dealerDao = new DealerDao(context, this);
        serverStateDao = new ServerStateDao(context, this);
    }


    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                if (isLocationPermissionGranted) {
                    getDeviceLocation(false, true);
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    map.setMyLocationEnabled(true);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    map.getUiSettings().setRotateGesturesEnabled(false);
                    map.setOnCameraMoveStartedListener(MainActivity.this);
                    map.setOnCameraIdleListener(MainActivity.this);
                    map.getUiSettings().setTiltGesturesEnabled(false);
                    map.getUiSettings().setMapToolbarEnabled(false);

                    LatLng temp = new LatLng(25.9814011, 83.3702517);
                    map.addMarker(new MarkerOptions().position(temp).title("Car Showroom"));
                }
            }
        });
    }

    private void getDeviceLocation(final boolean animate, final boolean getNearestDealers) {
        if (isLocationPermissionGranted) {
            FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            final Task locationTask = locationProviderClient.getLastLocation();
            locationTask.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location location = (Location) locationTask.getResult();

                        if (getNearestDealers) {
                            dealerDao.getNearestDealers(location.getLatitude(), location.getLongitude());
                        }

                        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), defaultZoom, animate);
                    } else {
                        // Location Not found
                    }
                }
            });
        } else {
            getPermissions();
        }
    }

    private void moveCamera(LatLng latLng, float zoom, boolean animate) {
        if (animate) map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        else map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void getPermissions() {
        String permission[] = {Constants.PERMISSION_FINE_LOCATION, Constants.PERMISSION_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(context, Constants.PERMISSION_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(context, Constants.PERMISSION_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) return;
                }
                isLocationPermissionGranted = true;

                initMap();
        }
    }

    public boolean isServicesOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    public void onCameraMoveStarted(int i) {

    }

    @Override
    public void onCameraIdle() {

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSearch:
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra(Constants.SEARCH_RESULT_BACK_INTENT, tvSearch.getText().toString());
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(this, searchCard, "searchCard");
                startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST_CODE, options.toBundle());
                break;
            case R.id.ivCancel1:
                tvSearch.setText("");
                ivCancel.setVisibility(View.GONE);
                break;
            case R.id.fabGpsLocation:
                getDeviceLocation(true, false);
                break;
            case R.id.ivMenu:
                drawerLayout.openDrawer(GravityCompat.START, true);
                break;
            case R.id.rlLoginHeader:
                drawerLayout.closeDrawer(GravityCompat.START, true);
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SEARCH_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    String searchText = data.getStringExtra(Constants.SEARCH_RESULT_BACK_INTENT);
                    if (searchText != null) {
                        tvSearch.setText(searchText);
                        if (!searchText.isEmpty()) ivCancel.setVisibility(View.VISIBLE);
                        else ivCancel.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    @Override
    public void onListRequestSuccessful(ArrayList list, int check, boolean status) {
        switch (check) {
            case Constants.DAO_GET_NEAREST_DEALERS:
                if (status) {
                    nearestDealers = list;
                } else {
                    View view = getLayoutInflater().inflate(R.layout.dialog_change_ip_address, null);
                    DialogBoxes.showChangeIPDialog(view, context, serverStateDao);
                }
                break;
        }
    }

    @Override
    public void onObjectRequestSuccessful(Object object, int check, boolean status) {
        switch (check) {
            case Constants.DAO_SERVER_STATE:
                DialogBoxes.dismissProgressDialog();
                if (status) {
                    Toast.makeText(context, "Connection is successful", Toast.LENGTH_SHORT).show();
                    getDeviceLocation(true, true);
                } else {
                    Toast.makeText(context, "Server is not connected", Toast.LENGTH_SHORT).show();
                    View view = getLayoutInflater().inflate(R.layout.dialog_change_ip_address, null);
                    DialogBoxes.showChangeIPDialog(view, context, serverStateDao);
                }
                break;
        }
    }
}
