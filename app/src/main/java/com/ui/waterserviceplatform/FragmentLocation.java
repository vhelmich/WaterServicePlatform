package com.ui.waterserviceplatform;

/**
 * Created by vhelmich on 29/11/17.
 */

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    public void onResume() {
        super.onResume();
        if(currentMarker==null && googleMap!=null) {
            if (checkPermissionGPS()) {
                moveToPosition();
            } else {
                defaultLocation();
            }
        }
    }

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

        setupNavigation(rootView);

        return rootView;
    }

    /**
     * Add listeners for the navigation bar
     * @param rootView view containing the elements
     */
    private void setupNavigation(View rootView){
        Button backButton = rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity)getActivity()).switchTab(1);
            }
        });

        Button sendButton = rootView.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity)getActivity()).sendData();
            }
        });
    }
    /**
     * Add a market and move to the given position
     * @param coord position to move to
     */
    private void addMarkerAndMove(LatLng coord) {
        if (currentMarker != null) {
            currentMarker.remove();
        }
        ((MainActivity)getActivity()).resetTabColor(2);
        currentMarker = googleMap.addMarker(new MarkerOptions().position(coord)
                .title(getString(R.string.leak_location))
                .snippet("")
        );

        CameraPosition cameraPosition = new CameraPosition.Builder().target(coord).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Add longTouch listener and add marker at the point
     */
    private void addListenerLongTouch(){
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                    if(checkCountry(latLng)){
                        addMarkerAndMove(latLng);
                    }
                    else{
                        Toast.makeText(getContext(),
                                getText(R.string.kenya_loc),
                                Toast.LENGTH_SHORT)
                                .show();
                    }


            }
        });
    }

    /**
     * Check if the application have the permissions to use the GPS
     * @return true if the application can use the GPS
     */
    private boolean checkPermissionGPS(){
        return TedPermission.isGranted(getContext(),Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * Modifies the current position button
     */
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
                        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                        if(checkCountry(ll)) {
                            addMarkerAndMove(ll);
                        }
                        else{
                            Toast.makeText(getContext(),
                                    getText(R.string.kenya_loc),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
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

    /**
     * Move and add marker to current position
     */
    private void moveToPosition(){
        try {
            googleMap.setMyLocationEnabled(true);
            changeButtonPosition();
        }
        catch(SecurityException e){

        }
    }

    /**
     * Default position used if localisation is not activated
     */
    private void defaultLocation(){
        LatLng nairobi = new LatLng(-1.2920408, 36.8256984);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(nairobi).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Return the current marker
     * @return current marker
     */
    public Marker getCurrentMarker(){
        return currentMarker;
    }

    /**
     * Check if the given location is in Kenya
     * @param loc given location
     * @return true if location in Kenya
     */
    private boolean checkCountry(LatLng loc){
        try {
            Geocoder location = new Geocoder(getContext());
            String country = location.getFromLocation(loc.latitude,
                    loc.longitude, 1).get(0).getCountryCode();
            if (country.equals("KE")) {
                return true;
            }
        }
        catch (Exception e){

        }
        return false;
    }
}
