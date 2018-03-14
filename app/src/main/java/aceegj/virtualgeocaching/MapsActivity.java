package aceegj.virtualgeocaching;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static float ZOOM_LEVEL = 20f;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private FloatingActionButton mFloatingActionButton;
    private LatLng mLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mFloatingActionButton = findViewById(R.id.fab_add);
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
        mMap.setMaxZoomPreference(ZOOM_LEVEL);
        mMap.setMinZoomPreference(ZOOM_LEVEL);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Disables all gestures
        mMap.getUiSettings().setAllGesturesEnabled(false);

        while (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    4
            );
        }
        mMap.setMyLocationEnabled(true);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeocacheData.getGeocacheData().messagesMap.put(mLatLng, new ArrayList<GeocacheData.GeocacheMessage>());
                mFloatingActionButton.setVisibility(View.INVISIBLE);
                updateMarkers();
                Intent intent = new Intent(MapsActivity.this, MessageActivity.class);
                intent.putExtra("LatLng", mLatLng);
                startActivity(intent);
            }
        });

        mLocationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(5000);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastKnownLocation = locationResult.getLastLocation();
                if (mLastKnownLocation != null) {
                    mLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, ZOOM_LEVEL));
                }  else {
                    Log.d("tag", "Current location is null. Using defaults.");
                    mLatLng = new LatLng(34.068921, -118.4473698);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, ZOOM_LEVEL));
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
                boolean set = true;
                for (LatLng oldLatLng : GeocacheData.getGeocacheData().messagesMap.keySet()) {
                    if (GeocacheData.distance(mLatLng, oldLatLng) < 15f) {
                        set = false;
                        break;
                    }
                }
                mFloatingActionButton.setVisibility(set ? View.VISIBLE : View.INVISIBLE);
            }
        };
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null);

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(34.068921, -118.4473698);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").snippet("Yolo: Lolo fweoajofjaweojweoa oijfweaj we \n FEWjoijowefjao fweoowai"));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(MapsActivity.this, MessageActivity.class);
                intent.putExtra("LatLng", marker.getPosition());
                startActivity(intent);
                return true;
            }
        });
        updateMarkers();
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void updateMarkers() {
        mMap.clear();
        for (LatLng oldLatLng : GeocacheData.getGeocacheData().messagesMap.keySet()) {
            mMap.addMarker(new MarkerOptions().position(oldLatLng));
        }
    }
}
