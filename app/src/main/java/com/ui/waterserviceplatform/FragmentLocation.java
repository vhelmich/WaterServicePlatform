package com.ui.waterserviceplatform;

/**
 * Created by vhelmich on 29/11/17.
 */

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


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

                LatLng nairobi = new LatLng(-1.2920408, 36.8256984);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(nairobi).zoom(12).build();
                
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                addListenerLongTouch();
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
}
