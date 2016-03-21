package com.apps.ron.weatherornot;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.apps.ron.openweatherlib.WeatherFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, WeatherFragment.OnFragmentInteractionListener {

    private GoogleMap mMap;
    private static final int REQUEST_FINE_LOCATION = 1;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private double lat;
    private double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        // Set location enabled
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        // Get the current location
        Location locate = locationManager.getLastKnownLocation(bestProvider);
        if (locate != null) {
            onLocationChanged(locate);
        } else {
            lat = 40.143736;
            lon = -83.042301;
        }
        // Add a marker and move the camera
        LatLng location = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(location).title("You are here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14.0f));
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
        // Request permissions for getting location
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, REQUEST_FINE_LOCATION);
        }
        Bundle bundle = new Bundle();
        bundle.putDouble("lat", lat);
        bundle.putDouble("lon", lon);
        // Create the weather fragment in the overlay
        getSupportFragmentManager().beginTransaction().add(R.id.overlay, WeatherFragment.newInstance(bundle)).commit();
        // Handle a click on the map and get the coordinates
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                lat = latLng.latitude;
                lon = latLng.longitude;
                View view = findViewById(R.id.overlay);
                if (view.getVisibility() == View.GONE) {
                    Bundle bundle = new Bundle();
                    bundle.putDouble("lat", lat);
                    bundle.putDouble("lon", lon);
                    WeatherFragment frag = new WeatherFragment();
                    frag.setArguments(bundle);
                    // Replace the weather fragment in the overlay
                    getSupportFragmentManager().beginTransaction().replace(R.id.overlay, frag).commit();
                } else {
                    // Hide the fragment
                    view.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
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

    @Override
    // This is a callback function from the fragment
    public void onFragmentInteraction(Uri uri) {
        View view = findViewById(R.id.overlay);
        view.setVisibility(View.VISIBLE);
    }
}
