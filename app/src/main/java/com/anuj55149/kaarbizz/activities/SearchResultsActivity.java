package com.anuj55149.kaarbizz.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anuj55149.kaarbizz.R;
import com.anuj55149.kaarbizz.adapters.OnRecyclerViewItemClickListener;
import com.anuj55149.kaarbizz.adapters.ShowCarsResultRecyclerViewAdapter;
import com.anuj55149.kaarbizz.dao.CarDao;
import com.anuj55149.kaarbizz.models.Car;
import com.anuj55149.kaarbizz.models.Dealer;
import com.anuj55149.kaarbizz.models.searchTypes.SearchTypeCarMake;
import com.anuj55149.kaarbizz.models.searchTypes.SearchTypeMakeModel;
import com.anuj55149.kaarbizz.utilities.Constants;
import com.anuj55149.kaarbizz.utilities.SharedPreference;
import com.anuj55149.kaarbizz.utilities.Utilities;
import com.anuj55149.kaarbizz.volley.RequestCallback;
import com.anuj55149.kaarbizz.volley.Urls;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SearchResultsActivity extends AppCompatActivity implements View.OnClickListener, RequestCallback, GoogleMap.OnMarkerClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final float defaultZoom = 13;
    private static final int MAP_VIEW = 2;
    private static final int LIST_VIEW = 3;
    private static final int FILTER_REQUEST_CODE = 1;
    Marker prevMarker = null;
    float prevRating = 0;
    private Context context;
    private boolean isLocationPermissionGranted = false;
    private GoogleMap map;
    private RecyclerView rvSearchResults;
    private TextView tvSort, tvFilter, tvTitle;
    private ImageView ivBack, ivToggle;
    private RelativeLayout rlDealerInfo;
    private ProgressBar pbSearch;
    private int currentView = LIST_VIEW;
    private SupportMapFragment mapFragment;
    private SharedPreference pref;
    private SearchTypeCarMake searchTypeCarMake;
    private SearchTypeMakeModel searchTypeMakeModel;
    private int SHOW_TYPE;
    private String brandUrl;
    private CarDao carDao;
    private ArrayList<Car> carsList;
    private ArrayList<Marker> markers;
    private ShowCarsResultRecyclerViewAdapter carsResultRecyclerViewAdapter;
    private CameraUpdate cameraUpdate;
    private int rbSortId = R.id.rbNearest;
    StringBuilder carUrl, carUrlWithFilters;
    private int filterArray[] = {-1, -1, -1, -1, -1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        context = this;

        carDao = new CarDao(context, this);
        markers = new ArrayList<>();

        Intent intent = getIntent();
        SHOW_TYPE = intent.getIntExtra(Constants.INTENT_SEARCH_RESULT_ACTIVITY_SHOW_TYPE, 0);
        brandUrl = intent.getStringExtra(Constants.INTENT_SEARCH_RESULT_BRAND_URL);
        if (SHOW_TYPE == Constants.SHOW_CAR_MAKE_NAME) {
            searchTypeCarMake = intent.getParcelableExtra(Constants.INTENT_SEARCH_RESULT_ACTIVITY_SEARCH_TYPE);
        } else if (SHOW_TYPE == Constants.SHOW_MAKE_MODEL) {
            searchTypeMakeModel = intent.getParcelableExtra(Constants.INTENT_SEARCH_RESULT_ACTIVITY_SEARCH_TYPE);
        }

        pref = new SharedPreference(context);
        getPermissions();

        initViews();
        toggleView();

        if (SHOW_TYPE == Constants.SHOW_CAR_MAKE_NAME) {
            tvTitle.setText(searchTypeCarMake.getMakeName());
            StringBuilder url = getBasicSearchUrl();
            url.append("&carMakeName=").append(searchTypeCarMake.getMakeName());
            showProgress();
            carUrl = url;
            carUrlWithFilters = carUrl;
        } else if (SHOW_TYPE == Constants.SHOW_MAKE_MODEL) {
            tvTitle.setText(searchTypeMakeModel.getModelName());
            StringBuilder url = getBasicSearchUrl();
            url.append("&carMakeName=").append(searchTypeMakeModel.getMakeName());
            url.append("&modelName=").append(searchTypeMakeModel.getModelName());
            showProgress();
            carUrl = url;
            carUrlWithFilters = carUrl;
        }

        carDao.getCars(carUrl.toString());
    }

    private StringBuilder getBasicSearchUrl() {
        StringBuilder sb = new StringBuilder("http://");
        sb.append(pref.getServerIPAddress()).append(":5000").append(Urls.CAR_SEARCH);
        Address address = pref.getCurrentAddress();
        sb.append("?lat=").append(address.getLatitude());
        sb.append("&lon=").append(address.getLongitude());
        sb.append("&limit=").append(50);

        return sb;
    }

    private void toggleView() {
        if (currentView == LIST_VIEW) {
            if (prevMarker != null) rlDealerInfo.setVisibility(View.VISIBLE);
            currentView = MAP_VIEW;
            ivToggle.setImageDrawable(getDrawable(R.drawable.ic_list));
            getSupportFragmentManager().beginTransaction().show(mapFragment).commit();
            rvSearchResults.setVisibility(View.GONE);
        } else {
            currentView = LIST_VIEW;
            rlDealerInfo.setVisibility(View.GONE);
            ivToggle.setImageDrawable(getDrawable(R.drawable.ic_map));
            getSupportFragmentManager().beginTransaction().hide(mapFragment).commit();
            rvSearchResults.setVisibility(View.VISIBLE);
        }
    }

    private void showProgress() {
        rvSearchResults.setVisibility(View.GONE);
        pbSearch.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        rvSearchResults.setVisibility(View.VISIBLE);
        pbSearch.setVisibility(View.GONE);
    }

    private void initViews() {
        tvSort = findViewById(R.id.tvSort);
        tvFilter = findViewById(R.id.tvFilter);
        ivBack = findViewById(R.id.ivBackButton);
        ivToggle = findViewById(R.id.ivListMapToggle);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        tvTitle = findViewById(R.id.tvSearchName);
        pbSearch = findViewById(R.id.pbSearchResult);
        rlDealerInfo = findViewById(R.id.rlDealerInfo);
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        carsResultRecyclerViewAdapter = new ShowCarsResultRecyclerViewAdapter(context, brandUrl, new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position, int check) {
                switch (check) {
                    case Constants.EACH_CAR_MAIL_CLICKED:
                        Dealer dealer = carsList.get(position).getDealer();
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{dealer.getEmail()});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Interested in VIN : " + carsList.get(position).getVin());
                        intent.putExtra(Intent.EXTRA_TEXT, "Hi " + dealer.getName() + ", I am interested in buying " + carsList.get(position).getMake() + " - " + carsList.get(position).getModel());
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                        break;
                }
            }
        });

        rvSearchResults.setLayoutManager(new LinearLayoutManager(context));
        rvSearchResults.setAdapter(carsResultRecyclerViewAdapter);
        tvSort.setOnClickListener(this);
        tvFilter.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivToggle.setOnClickListener(this);
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMapToolbarEnabled(false);
                map.getUiSettings().setAllGesturesEnabled(false);
                map.setOnMarkerClickListener(SearchResultsActivity.this);
                Address currentAddress = pref.getCurrentAddress();
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentAddress.getLatitude(), currentAddress.getLongitude()), defaultZoom));
            }

        });
    }

    private void getPermissions() {
        String permission[] = {Constants.PERMISSION_FINE_LOCATION, Constants.PERMISSION_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(context, Constants.PERMISSION_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(context, Constants.PERMISSION_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(SearchResultsActivity.this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(SearchResultsActivity.this, permission, LOCATION_PERMISSION_REQUEST_CODE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivListMapToggle:
                toggleView();
                break;
            case R.id.ivBackButton:
                finish();
                break;
            case R.id.tvSort:
                showSortDialogBox();
                break;
            case R.id.tvFilter:
                Intent intent = new Intent(SearchResultsActivity.this, FilterActivity.class);
                intent.putExtra(Constants.INTENT_FILTER_ARRAY, filterArray);
                startActivityForResult(intent, FILTER_REQUEST_CODE);
                overridePendingTransition(0, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILTER_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    String getUrl = data.getStringExtra(Constants.INTENT_FILTER_URL);
                    filterArray = data.getIntArrayExtra(Constants.INTENT_FILTER_ARRAY);
                    Log.d("searchResultTag", getUrl);
                    String tempUrl = carUrl.toString();
                    tempUrl += getUrl;
                    showProgress();
                    carUrlWithFilters = new StringBuilder(tempUrl);
                    carDao.getCars(tempUrl);
                }
                break;
        }
    }

    private void updateMapMarkers() {
        Set<Dealer> uniqueDealers = new HashSet<>();
        for (Car car : carsList) uniqueDealers.add(car.getDealer());

        Toast.makeText(context, uniqueDealers.size() + " dealers found", Toast.LENGTH_SHORT).show();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(pref.getCurrentAddress().getLatitude(), pref.getCurrentAddress().getLongitude()));
        for (Dealer dealer : uniqueDealers) {
            builder.include(new LatLng(dealer.getLat(), dealer.getLon()));
            float color = (float) (dealer.getRating() * 24f);
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(dealer.getLat(), dealer.getLon()))
                    .icon(BitmapDescriptorFactory.defaultMarker(color)));
            marker.setSnippet(dealer.getName());
            marker.setTag(dealer);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
        map.moveCamera(cu);
        map.getUiSettings().setAllGesturesEnabled(true);
    }

    @Override
    public void onListRequestSuccessful(ArrayList list, int check, boolean status) {
        switch (check) {
            case Constants.DAO_GET_CARS:
                hideProgress();
                if (status) {
                    carsList = list;
                    carsResultRecyclerViewAdapter.updateCarsList(carsList);
                    updateMapMarkers();
                }
                break;
        }
    }

    @Override
    public void onObjectRequestSuccessful(Object object, int check, boolean status) {

    }

    public void showSortDialogBox() {
        View view = getLayoutInflater().inflate(R.layout.dialog_show_sort, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        RadioGroup radioGroup = view.findViewById(R.id.rgSort);

        RadioButton activeBtn = radioGroup.findViewById(rbSortId);
        activeBtn.setChecked(true);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rbSortId = checkedId;
                dialog.dismiss();
                String tempUrl = carUrlWithFilters.toString();
                switch (rbSortId) {
                    case R.id.rbNearest:
                        tempUrl += "&sort=dist";
                        break;
                    case R.id.rbRating:
                        tempUrl += "&sort=avgRating";
                        break;
                    case R.id.rbPriceLH:
                        tempUrl += "&sort=priceLow";
                        break;
                    case R.id.rbPriceHL:
                        tempUrl += "&sort=priceHigh";
                        break;
                }
                showProgress();
                carDao.getCars(tempUrl);
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if (prevMarker != null) {
            prevMarker.setIcon(BitmapDescriptorFactory.defaultMarker(prevRating * 24f));
        }

        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        Dealer dealer = (Dealer) marker.getTag();
        prevMarker = marker;
        prevRating = (float) dealer.getRating();
        rlDealerInfo.setVisibility(View.VISIBLE);
        TextView tvDealerName = rlDealerInfo.findViewById(R.id.tvSingleDealerName);
        tvDealerName.setMaxLines(1);
        tvDealerName.setEllipsize(TextUtils.TruncateAt.END);
        TextView tvDealerRating = rlDealerInfo.findViewById(R.id.tvSingleRatings);
        TextView tvRateCount = rlDealerInfo.findViewById(R.id.ratingCount);
        TextView tvDistance = rlDealerInfo.findViewById(R.id.tvDistance);


        tvDealerName.setText(dealer.getName());
        DecimalFormat df = new DecimalFormat("#.0");
        tvDealerRating.setText(df.format(dealer.getRating()));
        tvRateCount.setText(String.format("(%d)", dealer.getRateCount()));

        GradientDrawable bgShape = (GradientDrawable) tvDealerRating.getBackground();
        bgShape.setColor(Utilities.getColorFromRating(dealer.getRating()));


        String distance = Utilities.format((long) dealer.getDistanceFromCurrentLocation() / 1000);
        tvDistance.setText(String.format("%s km away", distance));

        return false;
    }
}
