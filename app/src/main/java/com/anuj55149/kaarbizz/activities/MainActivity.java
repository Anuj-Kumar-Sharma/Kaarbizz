package com.anuj55149.kaarbizz.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anuj55149.kaarbizz.R;
import com.anuj55149.kaarbizz.adapters.MainRecyclerViewAdapter;
import com.anuj55149.kaarbizz.adapters.OnRecyclerViewItemClickListener;
import com.anuj55149.kaarbizz.dao.DealerDao;
import com.anuj55149.kaarbizz.dao.ServerStateDao;
import com.anuj55149.kaarbizz.models.Dealer;
import com.anuj55149.kaarbizz.utilities.Constants;
import com.anuj55149.kaarbizz.utilities.DialogBoxes;
import com.anuj55149.kaarbizz.utilities.SharedPreference;
import com.anuj55149.kaarbizz.utilities.Utilities;
import com.anuj55149.kaarbizz.volley.RequestCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener, View.OnClickListener, RequestCallback, OnRecyclerViewItemClickListener {


    private Context context;
    private boolean isLocationPermissionGranted = false;

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private static final int SEARCH_ACTIVITY_REQUEST_CODE = 11;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    private static final float defaultZoom = 13;

    private SharedPreference pref;

    private DrawerLayout drawerLayout;
    private CardView searchCard;
    private TextView tvSearch, tvUserName, tvUserEmail, tvChangeLocation, tvYourLocation;
    private ImageView ivCancel, ivMenu, ivUserImage, ivGps;
    private NavigationView navigationView;
    private RecyclerView rvMain;

    private FirebaseAuth auth;
    private RelativeLayout rlLoginHeader, rlUserHeader;

    private DealerDao dealerDao;
    private ServerStateDao serverStateDao;
    private ArrayList<Dealer> nearestDealers;
    private MainRecyclerViewAdapter mainRecyclerViewAdapter;
    private ProgressDialog progressDialog;

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

        showProgressDialog(context, "Connecting...");
        serverStateDao.getServerState();
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
        drawerLayout = findViewById(R.id.mainDrawerLayout);
        navigationView = findViewById(R.id.navigationView);
        View navHeaderLayout = navigationView.getHeaderView(0);
        rlUserHeader = navHeaderLayout.findViewById(R.id.rlUserHeader);
        rlLoginHeader = navHeaderLayout.findViewById(R.id.rlLoginHeader);
        tvUserName = navHeaderLayout.findViewById(R.id.tvUserName);
        tvChangeLocation = findViewById(R.id.tvChangeLocation);
        tvUserEmail = navHeaderLayout.findViewById(R.id.tvUserEmail);
        ivUserImage = navHeaderLayout.findViewById(R.id.ivUserImage);
        rvMain = findViewById(R.id.rvMain);
        tvYourLocation = findViewById(R.id.tvYourLocation);
        ivGps = findViewById(R.id.ivGps);


        ivMenu.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
        rlLoginHeader.setOnClickListener(this);
        tvChangeLocation.setOnClickListener(this);
        ivGps.setOnClickListener(this);

        nearestDealers = new ArrayList<>();
        mainRecyclerViewAdapter = new MainRecyclerViewAdapter(context, this);
        rvMain.setLayoutManager(new LinearLayoutManager(context));
        rvMain.setAdapter(mainRecyclerViewAdapter);
        dealerDao = new DealerDao(context, this);
        serverStateDao = new ServerStateDao(context, this);
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
                        Address address = Utilities.getAddressFromLatLng(context, location.getLatitude(), location.getLongitude());
                        pref.setCurrentLocationName("");
                        if (address != null) pref.setCurrentAddress(address);
                        else
                            Toast.makeText(context, "Address could not be found", Toast.LENGTH_SHORT).show();
                        updateYourLocation();

                        if (getNearestDealers) {
                            dealerDao.getNearestDealers(location.getLatitude(), location.getLongitude());
                        }
                    } else {
                        // Location Not found
                    }
                }
            });
        } else {
            getPermissions();
        }
    }

    private void updateYourLocation() {
        Address address = pref.getCurrentAddress();
        String text = pref.getLocationName();
        if (text == null || text.isEmpty()) text = address.getSubLocality();
        if (text == null || text.isEmpty()) text = address.getLocality();
        if (text == null || text.isEmpty()) text = address.getAdminArea();
        tvYourLocation.setText(text);
    }

    private void getPermissions() {
        String permission[] = {Constants.PERMISSION_FINE_LOCATION, Constants.PERMISSION_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(context, Constants.PERMISSION_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(context, Constants.PERMISSION_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionGranted = true;
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
            case R.id.ivMenu:
                drawerLayout.openDrawer(GravityCompat.START, true);
                break;
            case R.id.rlLoginHeader:
                drawerLayout.closeDrawer(GravityCompat.START, true);
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                break;
            case R.id.tvChangeLocation:
                tvChangeLocation.setClickable(false);
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
                    startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
                } catch (GooglePlayServicesRepairableException e) {
                    // Indicates that Google Play Services is either not installed or not up to date. Prompt
                    // the user to correct the issue.
                    GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                            0 /* requestCode */).show();
                } catch (GooglePlayServicesNotAvailableException e) {
                    // Indicates that Google Play Services is not available and the problem is not easily
                    // resolvable.
                    String message = "Google Play Services is not available: " +
                            GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
                    Log.e(TAG, message);
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ivGps:
                ivGps.setVisibility(View.GONE);
                getDeviceLocation(false, false);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_AUTOCOMPLETE:
                tvChangeLocation.setClickable(true);
                if (resultCode == RESULT_OK) {
                    // Get the user's selected place from the Intent.
                    ivGps.setVisibility(View.VISIBLE);
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    pref.setCurrentLocationName(place.getName().toString());
                    Address address = Utilities.getAddressFromLatLng(context, place.getLatLng().latitude, place.getLatLng().longitude);
                    if (address != null) pref.setCurrentAddress(address);
                    else
                        Toast.makeText(context, "Address could not be found", Toast.LENGTH_SHORT).show();
                    updateYourLocation();
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    Log.e(TAG, "Error: Status = " + status.toString());
                }
        }
    }

    @Override
    public void onListRequestSuccessful(ArrayList list, int check, boolean status) {
        switch (check) {
            case Constants.DAO_GET_NEAREST_DEALERS:
                if (status) {
                    nearestDealers = list;
                    mainRecyclerViewAdapter.updateNearestDealersData(nearestDealers);
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
                dismissProgressDialog();
                if (status) {
                    getDeviceLocation(true, true);
                } else {
                    Toast.makeText(context, "Server is not connected", Toast.LENGTH_SHORT).show();
                    View view = getLayoutInflater().inflate(R.layout.dialog_change_ip_address, null);
                    DialogBoxes.showChangeIPDialog(view, context, serverStateDao);
                }
                break;
        }
    }

    @Override
    public void onClick(View view, int position, int check) {

    }

    public void showProgressDialog(Context context, String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.setMessage(message);
        if (!((Activity) context).isFinishing()) {
            progressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


}
