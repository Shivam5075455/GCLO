package com.terminal.gclo.Fragments.NavigationFragments;

//import static android.os.Build.VERSION_CODES.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import androidx.activity.OnBackPressedCallback;

import com.terminal.gclo.MainActivity;
import com.terminal.gclo.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/* loaded from: classes6.dex */
public class MapFragment extends Fragment{

    private GoogleMap map;
    private static final String TAG = "DEBUG_MA";
    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert supportMapFragment != null;
//        supportMapFragment.getMapAsync(this);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                map=googleMap;
                LatLng latLng = new LatLng(37.7749, -122.4194);
//              Add marker
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Francisco");
                map.addMarker(markerOptions.position(latLng));
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

//                draw circle
                map.addCircle(new CircleOptions().center(latLng).radius(200).strokeColor(R.color.app_theme).fillColor(R.color.black));
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                
//                Ground overlay -> show image
//                map.addGroundOverlay(new GroundOverlayOptions()
//                        .position(latLng,1000f,1000f)
//                        .image(BitmapDescriptorFactory.fromResource(R.drawable.man)));
            }
        });

        // Set up onBackPressed callback
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back press here
                // For example, you can navigate to another fragment or perform other actions
                // If you want to allow the default back button behavior, call isEnabled() before handling it

                // Uncomment the next line if you want to allow the default back button behavior
//                 if (isEnabled()) {
//                     super.handleOnBackPressed();
//                 }
                ((MainActivity) requireActivity()).changeToolbarTitle("Terminal");
                ((MainActivity) requireActivity()).replaceFragments(new TerminalFragment());

            }
        });

        return view;
    }

    public void getLocationData(String latitude,String longitude){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("LocationPreferences", getContext().MODE_PRIVATE);
        latitude = sharedPreferences.getString("latitude", "0");
        longitude = sharedPreferences.getString("longitude", "0");

        Log.d(TAG, "Latitude: " + latitude);
        Log.d(TAG, "Longitude: " + longitude);
    }
/*
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap){
        Log.d(TAG, "onMapReady: started");
        map=googleMap;
        LatLng latLng = new LatLng(37.7749, -122.4194);
//              Add marker
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Francisco");
        map.addMarker(markerOptions.position(latLng));
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

//                draw circle
        map.addCircle(new CircleOptions().center(latLng).radius(200).strokeColor(R.color.app_theme).fillColor(R.color.black));
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

//                Ground overlay -> show image
//                map.addGroundOverlay(new GroundOverlayOptions()
//                        .position(latLng,1000f,1000f)
//                        .image(BitmapDescriptorFactory.fromResource(R.drawable.man)));
    }
*/
   /* public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutContainer, fragment);
        fragmentTransaction.commit();
    }*/
}
