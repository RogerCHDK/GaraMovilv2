package com.example.pruebafirebase;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class gps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private  int permiso=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        /*
        if (ContextCompat.checkSelfPermission(gps.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(gps.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                mMap.setMyLocationEnabled(true);
            } else {

                ActivityCompat.requestPermissions(gps.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        permiso);
            }
        }else{
            mMap.setMyLocationEnabled(true);
        }

         */
        // Add a marker in Sydney and move the camera
        LatLng prueba = new LatLng(19.278912, -99.628864);
        mMap.addMarker(new MarkerOptions().position(prueba).title("Steren").snippet("Contamos con garantías virtuales").icon(BitmapDescriptorFactory.fromResource(R.drawable.icono1)));
        LatLng prueba2 = new LatLng(19.249526, -99.600256);
        mMap.addMarker(new MarkerOptions().position(prueba2).title("ùwú").snippet("Contamos con garantías virtuales").icon(BitmapDescriptorFactory.fromResource(R.drawable.icono1)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(prueba,12));
    }
}
