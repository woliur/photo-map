package com.rifad.photomap.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rifad.photomap.R;
import com.rifad.photomap.data.constant.AppConstants;
import com.rifad.photomap.model.Photo;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // variable
    private GoogleMap mMap;
    private Photo photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
    }

    private void initVariable() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(AppConstants.KEY_PHOTO)) {
            photo = bundle.getParcelable(AppConstants.KEY_PHOTO);
        }
    }

    private void initView() {
        // set parent view
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker
        LatLng photoLatLng = new LatLng(photo.getLatitude(), photo.getLongitude());
        mMap.addMarker(new MarkerOptions().position(photoLatLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(photoLatLng, 12f));
    }
}
