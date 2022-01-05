package kg.geektech.mapapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kg.geektech.mapapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityMainBinding binding;
    private GoogleMap mMap;
    private List<LatLng> markerList = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_frag);
        mapFragment.getMapAsync(this);
        mapFragment.setRetainInstance(true);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getData();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.title(String.valueOf(latLng));
                mMap.addMarker(options);
                markerList.add(latLng);
                binding.polylineBtn.setOnClickListener(view -> {
                    mMap.addPolyline(new PolylineOptions().addAll(markerList));
                });

                binding.clearBtn.setOnClickListener(view -> {
                    mMap.clear();
                    markerList.clear();

                });


            }
        });
        
    }

    private void getData() {
        // Get previous or default camera position
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        double longitude = sharedPref.getFloat("longitude", 0);
        double latitude = sharedPref.getFloat("latitude", 0);
        float zoom = sharedPref.getFloat("zoom", 0);
        LatLng startPosition = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(startPosition)
                .zoom(zoom)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    



    @Override
    protected void onStop() {
        super.onStop();
        CameraPosition cameraPosition =mMap.getCameraPosition();
        double longitude = cameraPosition.target.longitude;
        double latitude = cameraPosition.target.latitude;
        float zoom = cameraPosition.zoom;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("longitude", (float)longitude);
        editor.putFloat("latitude", (float)latitude);
        editor.putFloat("zoom", zoom);
        editor.apply();
    }
}