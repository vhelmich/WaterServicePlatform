package com.ui.waterserviceplatform;

/**
 * Created by vhelmich on 29/11/17.
 */

import android.*;
import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;


public class FragmentLocation extends Fragment {
    private GoogleMap googleMap;
    private PlaceAutocompleteFragment placeAutoComplete;
    private Marker currentMarker;
    private MapFragment mapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placeAutoComplete = new PlaceAutocompleteFragment();
        mapFragment = new MapFragment();
        FragmentManager fm = getActivity().getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_autocomplete, placeAutoComplete);
        ft.replace(R.id.fragment_map, mapFragment);
        ft.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);

        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                addMarkerAndMove(place.getLatLng());
            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });

        AutocompleteFilter countryFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY).setCountry("KE")
                .build();

        placeAutoComplete.setFilter(countryFilter);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                if(checkPermissionGPS()){
                    moveToPosition();
                }
                else{
                    defaultLocation();
                }
                addListenerLongTouch();
            }
        });

        Button backButton = rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity)getActivity()).switchTab(1);
            }
        });

        return rootView;
    }

    private void addMarkerAndMove(LatLng coord) {
        if (currentMarker != null) {
            currentMarker.remove();
        }
        currentMarker = googleMap.addMarker(new MarkerOptions().position(coord).title("Your leak location").snippet(""));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(coord).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void addListenerLongTouch(){
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                try {
                    Geocoder location = new Geocoder(getContext());
                    String country = location.getFromLocation(latLng.latitude,
                            latLng.longitude, 1).get(0).getCountryCode();
                    if(country.equals("KE")){
                        addMarkerAndMove(latLng);
                    }
                    else{
                        Context context = getContext();
                        CharSequence text = "The location must be in Kenya.";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }
                catch(Exception e){

                }

            }
        });
    }

    private boolean checkPermissionGPS(){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                moveToPosition();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                defaultLocation();
            }


        };
        TedPermission.with(getContext())
                .setPermissionListener(permissionlistener)
                .setRationaleTitle("Requesting permission")
                .setRationaleMessage("We can get your position automatically if you allow us to do so.")
                .setDeniedMessage("Permission denied")
                .setDeniedMessage("If you reject permission, you can not use your location.\n" +
                        "Please turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
        return TedPermission.isGranted(getContext(),Manifest.permission.ACCESS_FINE_LOCATION);
    }


    private void changeButtonPosition(){
        View mapView = mapFragment.getView();
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent())
                    .findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){
            @Override
            public boolean onMyLocationButtonClick()
            {
                LocationManager locationManager = (LocationManager)
                        getActivity().getSystemService(Context.LOCATION_SERVICE);
                android.location.LocationListener locationListener = new android.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        addMarkerAndMove(new LatLng(location.getLatitude(), location.getLongitude()));
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
                };
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            5000,
                            10,
                            locationListener);
                }
                catch(SecurityException e){

                }
                return false;
            }
        });
    }

    private void moveToPosition(){
        try {
            googleMap.setMyLocationEnabled(true);
            changeButtonPosition();
        }
        catch(SecurityException e){

        }
    }

    private void defaultLocation(){
        LatLng nairobi = new LatLng(-1.2920408, 36.8256984);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(nairobi).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
