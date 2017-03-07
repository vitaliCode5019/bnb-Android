package com.bicyclebnb.groupridefinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bicyclebnb.groupridefinder.apis.BicycleBnbService;
import com.bicyclebnb.groupridefinder.apis.ServiceGenerator;
import com.bicyclebnb.groupridefinder.apis.ServiceInterceptor;
import com.bicyclebnb.groupridefinder.apis.ShopService;
import com.bicyclebnb.groupridefinder.fragments.AccommodationsFragment;
import com.bicyclebnb.groupridefinder.fragments.BikeShopsFragment;
import com.bicyclebnb.groupridefinder.fragments.GroupRidesFragment;
import com.bicyclebnb.groupridefinder.fragments.RacesFragment;
import com.bicyclebnb.groupridefinder.interfaces.IActivityInteractionListener;
import com.bicyclebnb.groupridefinder.interfaces.IFragmentinteractionListener;
import com.bicyclebnb.groupridefinder.models.AccommodationModel;
import com.bicyclebnb.groupridefinder.models.BikeShopModel;
import com.bicyclebnb.groupridefinder.models.CoordComparableModel;
import com.bicyclebnb.groupridefinder.models.GroupRideModel;
import com.bicyclebnb.groupridefinder.models.RaceModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class MapsActivity extends FragmentActivity implements

        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        IActivityInteractionListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MainActivity";
    private static final String LOCATION_KEY = "LOCATION";
    //region UI
    @BindViews({R.id.tab1, R.id.tab2, R.id.tab3, R.id.tab4})
    List<View> tabs;
    @BindView(R.id.fragment_container)
    View fragmentContainer;
    @BindView(R.id.btnAddNew)
    Button btnAddNew;
    @BindView(R.id.lbl_title)
    TextView lblTitle;

    @OnClick(R.id.imgMyLocation)
    void onMyLocation()
    {
        if(mLocation != null && mMap != null) {
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 12);
            mMap.animateCamera(cu);
        }
    }

    @OnClick(R.id.tab1)
    void onNavigateToRides() {
        setTabItemSelected(0);
    }

    @OnClick(R.id.tab2)
    void onNavigateToShops() {
        setTabItemSelected(1);
    }

    @OnClick(R.id.tab3)
    void onNavigateToAccommodations() {
        setTabItemSelected(2);
    }

    @OnClick(R.id.tab4)
    void onNavigateToRaces() {
        setTabItemSelected(3);
    }

    void setTabItemSelected(int tabIndex) {
        if (mSourceType == tabIndex) return;

        for (int i = 0; i < 4; i++) {
            ImageView tabIcon = (ImageView) tabs.get(i).findViewById(R.id.img_tab);
            TextView tabLabel = (TextView) tabs.get(i).findViewById(R.id.lbl_tab);
            tabIcon.clearColorFilter();
            if (i == tabIndex) {
                tabIcon.getDrawable().setColorFilter(0xFF0077FF, PorterDuff.Mode.SRC_ATOP);
                tabLabel.setTextColor(0xFF0077FF);
            } else {
                tabIcon.getDrawable().setColorFilter(0xFF888888, PorterDuff.Mode.SRC_ATOP);
                tabLabel.setTextColor(0xFF888888);
            }
            tabIcon.invalidate();
        }
        if (tabIndex == 0) {
            loadGroupRideData();
            btnAddNew.setText(R.string.add_ride);
            lblTitle.setText(R.string.add_ride);
        } else if (tabIndex == 1) {
            loadBikeShopData();
            btnAddNew.setText(R.string.add_shop);
            lblTitle.setText(R.string.add_shop);
        } else if (tabIndex == 2) {
            loadAccommodationData();
            btnAddNew.setText(R.string.add_room);
            lblTitle.setText(R.string.add_room);
        } else if (tabIndex == 3) {
            loadRaceData();
            btnAddNew.setText(R.string.add_race);
            lblTitle.setText(R.string.add_race);
        }
    }

    @OnClick(R.id.btnPrevious)
    void onPrevious() {
        if (mDataSource == null || mSourceType == -1) return;
        if (mSelectedModelIndex == 0) return;
        mSelectedModelIndex--;
        setFragmentData(mDataSource.get(mSelectedModelIndex));
        setMapData(mDataSource.get(mSelectedModelIndex));
    }

    @OnClick(R.id.btnNext)
    void onNext() {
        if (mDataSource == null || mSourceType == -1) return;
        if (mSelectedModelIndex == mDataSource.size() - 1) return;
        mSelectedModelIndex++;
        setFragmentData(mDataSource.get(mSelectedModelIndex));
        setMapData(mDataSource.get(mSelectedModelIndex));
    }

    @BindView(R.id.btnShowDetails)
    Button btnShowDetails;

    @OnClick(R.id.btnShowDetails)
    void onShowDetails() {
        if (mDataSource == null || mSourceType == -1) return;
        showDetail(!(fragmentContainer.getVisibility() == View.VISIBLE));
    }
    void showDetail(boolean show)
    {
        mShowDetail = show;
        fragmentContainer.setVisibility(mShowDetail ? View.VISIBLE : View.GONE);
        btnShowDetails.setText(!mShowDetail ? R.string.show_details : R.string.hide_details);
    }

    @OnClick(R.id.btnAddNew)
    void onAddNew() {
        if (mSourceType == 0) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConstants.BNB_ADD_RIDE_URL)));
        } else if (mSourceType == 1) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConstants.GOOGLE_SURVEY_URL)));
        } else if (mSourceType == 2) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConstants.BNB_ADD_PROPERTY_URL)));
        } else if (mSourceType == 3) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConstants.BNB_ADD_RACE_URL)));
        }
    }

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    @OnClick(R.id.btnSearchLocation)
    void onSearchLocation() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }
    //endregion
    private KProgressHUD mHud;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient = null;
    private Location mLocation;
    private boolean mShowDetail = false;
    private int mSelectedModelIndex = -1;
    private int mSourceType = -1;
    private List<CoordComparableModel> mDataSource;
    private List<CoordComparableModel> mGroupRidesDataSource;
    private List<CoordComparableModel> mBikeShopsDataSource;
    private List<CoordComparableModel> mAccommodationsDataSource;
    private List<CoordComparableModel> mRacesDataSource;
    private List<Marker> mMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        updateValuesFromBundle(savedInstanceState);
        initLocation();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setTabItemSelected(0);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                if(mMap != null) {
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12);
                    mMap.animateCamera(cu);
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
            return;
        } else {
            mMap.setMyLocationEnabled(true);
        }

        if(mLocation != null) {
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 12);
            mMap.moveCamera(cu);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Object tag = marker.getTag();
        if(tag instanceof CoordComparableModel) {
            CoordComparableModel model = (CoordComparableModel)tag;
            if(mDataSource != null) {
                int index = mDataSource.indexOf(model);
                if(index >= 0) {
                    mSelectedModelIndex = index;
                    setFragmentData((CoordComparableModel) tag);
                    showDetail(true);
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                if(mMap != null) {
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12);
                    mMap.animateCamera(cu);
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
    //region Progress HUD
    public void showHud() {
        if (mHud == null) {
            mHud = KProgressHUD.create(this);
        }
        mHud.show();
    }

    public void dismissHud() {
        if (mHud != null) {
            mHud.dismiss();
        }
    }
    //endregion

    @Override
    public CoordComparableModel getSelectedModel() {
        if(mSourceType != -1 && mDataSource != null && mSelectedModelIndex != -1) {
            return mDataSource.get(mSelectedModelIndex);
        }
        return null;
    }

    @Override
    public String getProximity(CoordComparableModel model) {
        return model.getProximityString(mLocation);
    }

    //region UPDATE DATA
    void loadGroupRideData() {
        if(mGroupRidesDataSource == null || mGroupRidesDataSource.size() == 0) {
            BicycleBnbService service = ServiceGenerator.createBnbService(BicycleBnbService.class);
            Call<ResponseBody> call = service.listRides();
            new ServiceInterceptor.Builder(this, call).showProgress(true).build().execute(new ServiceInterceptor.ResponseHandler() {
                @Override
                public void onFailure(int errCode) {

                }

                @Override
                public void onSuccess(String result) {
                    mGroupRidesDataSource = GroupRideModel.parseFromHtml(result);
                    updateDataSource(mGroupRidesDataSource, 0);
                }
            });
        } else {
            updateDataSource(mGroupRidesDataSource, 0);
        }
    }

    void loadBikeShopData() {
        if(mBikeShopsDataSource == null || mBikeShopsDataSource.size() == 0) {
            ShopService service = ServiceGenerator.createGoogleSheetService(ShopService.class);
            Call<ResponseBody> call = service.listShops();
            new ServiceInterceptor.Builder(this, call).showProgress(true).build().execute(new ServiceInterceptor.ResponseHandler() {
                @Override
                public void onFailure(int errCode) {

                }

                @Override
                public void onSuccess(String result) {
                    mBikeShopsDataSource = BikeShopModel.parseFromHtml(result);
                    updateDataSource(mBikeShopsDataSource, 1);
                }
            });
        } else {
            updateDataSource(mBikeShopsDataSource, 1);
        }
    }

    void loadAccommodationData() {
        if(mAccommodationsDataSource == null || mAccommodationsDataSource.size() == 0) {
            BicycleBnbService service = ServiceGenerator.createBnbService(BicycleBnbService.class);
            Call<ResponseBody> call = service.listAccommodations();
            new ServiceInterceptor.Builder(this, call).showProgress(true).build().execute(new ServiceInterceptor.ResponseHandler() {
                @Override
                public void onFailure(int errCode) {

                }

                @Override
                public void onSuccess(String result) {
                    mAccommodationsDataSource = AccommodationModel.parseFromHtml(result);
                    updateDataSource(mAccommodationsDataSource, 2);
                }
            });
        } else {
            updateDataSource(mAccommodationsDataSource, 2);
        }
    }

    void loadRaceData() {
        if(mRacesDataSource == null || mRacesDataSource.size() == 0) {
            BicycleBnbService service = ServiceGenerator.createBnbService(BicycleBnbService.class);
            Call<ResponseBody> call = service.listRaces();
            new ServiceInterceptor.Builder(this, call).showProgress(true).build().execute(new ServiceInterceptor.ResponseHandler() {
                @Override
                public void onFailure(int errCode) {

                }

                @Override
                public void onSuccess(String result) {
                    mRacesDataSource = RaceModel.parseFromHtml(result);
                    updateDataSource(mRacesDataSource, 3);
                }
            });
        } else {
            updateDataSource(mRacesDataSource, 3);
        }

    }

    void updateDataSource(List<CoordComparableModel> dataSource, int sourceType) {
        mDataSource = dataSource;
        mSelectedModelIndex = 0;
        mSourceType = sourceType;

        //Sort data source by proximity
        Collections.sort(mDataSource, new Comparator<CoordComparableModel>() {
            @Override
            public int compare(CoordComparableModel o1, CoordComparableModel o2) {
                float distance1 = o1.getProximityInMeter(mLocation);
                float distance2 = o2.getProximityInMeter(mLocation);
                if(distance1 < distance2) return -1;
                else if(distance1 == distance2) return 0;
                else return 1;
            }
        });

        initMapData();
        initFragment();
    }
    //endregion

    private void initMapData()
    {
        mMap.clear();
        mMarkers = new ArrayList<>();
        LatLngBounds.Builder builder =new LatLngBounds.Builder();
        int i = 0;
        for(CoordComparableModel model : mDataSource) {
            LatLng position = model.coordinate;
            if(position == null) continue;

            if(i < 5) {
                builder.include(position);
                i++;
            }


            MarkerOptions option = new MarkerOptions().position(position).title(model.coordTitle()).snippet(model.coordSnippet());

            Marker marker = mMap.addMarker(option);
            marker.setTag(model);
            mMarkers.add(marker);
        }

        if(mLocation != null) {
            //builder.include(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 10);
            mMap.animateCamera(cu);
        } else {

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), 50);
            mMap.animateCamera(cu);
        }
    }

    private void initFragment()
    {
        Fragment fragment = null;
        switch(mSourceType) {
            case 0:
                fragment = GroupRidesFragment.newInstance();
                break;
            case 1:
                fragment = BikeShopsFragment.newInstance();
                break;
            case 2:
                fragment = AccommodationsFragment.newInstance();
                break;
            case 3:
                fragment = RacesFragment.newInstance();
                break;
            default:
                break;
        }
        if(fragment != null) replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment)
    {
        try {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFragmentData(CoordComparableModel model) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if(fragment != null && fragment instanceof IFragmentinteractionListener) {
            ((IFragmentinteractionListener)fragment).setSelectedModel(model);
        }
    }

    private void setMapData(CoordComparableModel model) {
        if(mMarkers == null) return;

        for(Marker marker : mMarkers) {
            if(marker.getTag() == model) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(model.coordinate));
                marker.showInfoWindow();
            }
        }
    }

    //region PERMISSION CHECK
    int PERMISSION_REQUEST_CODE = 99;
    boolean isPermissionDialogOpened = false;
    boolean checkPermissions() {
        String[] permissionsRequired = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        List<String> permissions = new ArrayList<>();

        for (String permission : permissionsRequired) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(permission);
            }
        }

        if (permissions.size() > 0) {
            if(!isPermissionDialogOpened) {
                isPermissionDialogOpened = true;
                ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), PERMISSION_REQUEST_CODE);
            }
            return false;
        } else {
            startLocationUpdates();
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        isPermissionDialogOpened = false;
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length != permissions.length) {
                checkPermissions();
            }
        }
    }
    //endregion


    //region LOCATION PICK
    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
        if(mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            Log.e(TAG, "Connecting google api client");
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Log.e(TAG, "Disconnecting google api client");
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Log.e(TAG, "Starting location update");
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        stopLocationUpdates();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if(mLocation != null) {
            savedInstanceState.putParcelable(LOCATION_KEY, mLocation);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                mLocation = savedInstanceState.getParcelable(LOCATION_KEY);
                Log.e(TAG, "Saved location = " + mLocation.toString());
                updateLocation(mLocation);
            }
        }
    }

    protected void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setFastestInterval(60 * 1000);
        mLocationRequest.setInterval(60 * 1000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
            return;
        }

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(lastLocation != null) {
            updateLocation(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        updateLocation(location);
    }

    private void initLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void updateLocation(Location location) {
        mLocation = location;
        Log.e(TAG, "Location Updated " + mLocation.toString());
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    //endregion
}
