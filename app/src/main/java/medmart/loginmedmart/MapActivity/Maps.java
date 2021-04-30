package medmart.loginmedmart.MapActivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.common.base.Stopwatch;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import medmart.loginmedmart.HomeActivity.HomePage;
import medmart.loginmedmart.R;
import medmart.loginmedmart.UtilityClasses.Utility;

public class Maps extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;
    private boolean mLocationPermissionGranted = false;
    private int LOCATION_PERMISSION_CODE_FIRST = 1;
    private int LOCATION_PERMISSION_CODE_SECOND = 2;
    private LatLng mDefaultLocation = new LatLng(30.767, 76.7774);
    private String mDefaultLocationName = "Chandigarh";
    private LatLng mCurrentLocation;
    private PlacesClient mPlacesClient;
    private boolean dialogBox = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final int DEFAULT_ZOOM = 15;
    TextView currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.teal_700));
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        currentLocation = findViewById(R.id.currentlocation);
        Intent intent = getIntent();

        double userLongitude = intent.getDoubleExtra("userlongitude", mDefaultLocation.longitude);
        double userLatitude = intent.getDoubleExtra("userlatitude", mDefaultLocation.latitude);
        mCurrentLocation = new LatLng(userLatitude, userLongitude);
        Button confirmLocation = findViewById(R.id.confirm_button);
        confirmLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.StoreDataInCache(getApplicationContext(), "userlongitude",
                        String.valueOf(mCurrentLocation.longitude));
                Utility.StoreDataInCache(getApplicationContext(), "userlatitude",
                        String.valueOf(mCurrentLocation.latitude));
                Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(mCurrentLocation.latitude,
                            mCurrentLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses != null) {
                    String address = addresses.get(0).getAddressLine(0);
                    Utility.StoreDataInCache(getApplicationContext(), "usercity", addresses.get(0).getLocality());
                    Utility.StoreDataInCache(getApplicationContext(), "useraddress", address);
                }

                Intent intent1 = new Intent(getApplicationContext(), HomePage.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.putExtra("class", "map");
                startActivity(intent1);
                finish();
            }
        });

        String apiKey = getString(R.string.google_maps_key);
        Places.initialize(getApplicationContext(), apiKey);
        mPlacesClient = Places.createClient(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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

    public void Back(View view) {
        Intent intent = new Intent(this, PlacesSearch.class);
        startActivity(intent);
        finish();
    }

    public void ChangeLocation(View view) {
        Intent intent = new Intent(getApplicationContext(), PlacesSearch.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        marker = mMap.addMarker(new MarkerOptions().position(mCurrentLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, DEFAULT_ZOOM));
//        mMap.getUiSettings().setZoomControlsEnabled(true)
//        currentLocation.setText(mDefaultLocationName)
        SetUiWithLastLocation();
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                LatLng center = mMap.getCameraPosition().target;
                marker.setPosition(center);
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                mCurrentLocation = mMap.getCameraPosition().target;
                SetUiWithLastLocation();
            }
        });
    }

    private void CheckLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            Utility.CheckGPSStatus(this, dialogBox);
        } else {
            mLocationPermissionGranted = Utility.GetLocationPermission(this, LOCATION_PERMISSION_CODE_FIRST, LOCATION_PERMISSION_CODE_SECOND);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void GetDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mFusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, DEFAULT_ZOOM));
                                    marker.setPosition(mCurrentLocation);
                                    SetUiWithLastLocation();
                                    // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                } else {
                                    // todo hndle if location is null
                                }
                            }
                        });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void SetUiWithLastLocation() {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(mCurrentLocation.latitude,
                    mCurrentLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null) {
            String address = addresses.get(0).getAddressLine(0);
            currentLocation.setText(address);
            Utility.StoreDataInCache(getApplicationContext(), "usercity", addresses.get(0).getLocality());
            Utility.StoreDataInCache(getApplicationContext(), "useraddress", address);
        } else {
            currentLocation.setText("Unknown Location");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_CODE_FIRST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                Utility.CheckGPSStatus(this, dialogBox);
            } else {
                //
                mLocationPermissionGranted = false;
                Utility.GetLocationPermission(this, LOCATION_PERMISSION_CODE_FIRST, LOCATION_PERMISSION_CODE_SECOND);
            }
        } else if (requestCode == LOCATION_PERMISSION_CODE_SECOND) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // todo after getting permission
                mLocationPermissionGranted = true;
            } else {
                //
                mLocationPermissionGranted = false;
                new AlertDialog.Builder(this)
                        .setMessage("It looks like you have turned off permissions required for this feature. It can be enabled under Phone" +
                                "Settings > Apps > Medmart > Permissions")
                        .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void PickCurrentPlace(View view) throws InterruptedException {
        System.out.println("here in current");
        if (mMap == null) {
            return;
        }

        CheckLocationPermission();

        if (mLocationPermissionGranted) {
            GetDeviceLocation();
        } else {
            Toast.makeText(this, "Please grant permission first", Toast.LENGTH_LONG).show();
        }
    }
}