package medmart.loginmedmart.MapActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.List;

import medmart.loginmedmart.HomeActivity.HomePage;
import medmart.loginmedmart.ProductResultActivity.ProductResult;
import medmart.loginmedmart.R;
import medmart.loginmedmart.UtilityClasses.Utility;

public class Maps extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;
    private static boolean mLocationPermissionGranted = false;
    private int LOCATION_PERMISSION_CODE_FIRST = 1;
    private int LOCATION_PERMISSION_CODE_SECOND = 2;
    private LatLng mDefaultLocation = new LatLng(30.767, 76.7774);
    private String mDefaultLocationName = "Chandigarh";
    private static LatLng mCurrentLocation;
    private static PlacesClient mPlacesClient;
    private static int REQUEST_CHECK_SETTINGS = 3;
    private boolean dialogBox = false;
    private static FusedLocationProviderClient mFusedLocationProviderClient;
    private final int DEFAULT_ZOOM = 15;
    private int ACTIVITY_CODE = 2;
    TextView currentLocation;
    ProgressDialog progressDialog;

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

                List<Address> addresses = Utility.GetCurrentAddressFromLatLng(getApplicationContext(),
                        mCurrentLocation);

                if (addresses != null) {
                    String address = addresses.get(0).getAddressLine(0);
                    Utility.StoreDataInCache(getApplicationContext(), "usercity", addresses.get(0).getLocality());
                    Utility.StoreDataInCache(getApplicationContext(), "useraddress", address);
                }

                Intent intent = getIntent();

                if (intent.getStringExtra("class").contentEquals("homepage")) {
                    intent.setClass(getApplicationContext(), HomePage.class);
                } else {
                    intent.setClass(getApplicationContext(), ProductResult.class);
                }

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("class", "map");
                startActivity(intent);
            }
        });

        String apiKey = getString(R.string.google_maps_key);
        Places.initialize(getApplicationContext(), apiKey);
        mPlacesClient = Places.createClient(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    public void Back(View view) {
        Intent intent = getIntent();
        intent.setClass(this, PlacesSearch.class);
        startActivity(intent);
        finish();
    }

    public void ChangeLocation(View view) {
        Intent intent = getIntent();
        intent.setClass(this, PlacesSearch.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        marker = mMap.addMarker(new MarkerOptions().position(mCurrentLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, DEFAULT_ZOOM));
        mMap.getUiSettings().setZoomControlsEnabled(true);
//        currentLocation.setText(mDefaultLocationName)
        SetUiWithCurrentLocation();
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
                SetUiWithCurrentLocation();
            }
        });
    }

    private void CheckLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            if (Utility.GetLocationPermission(this, LOCATION_PERMISSION_CODE_FIRST,
                    LOCATION_PERMISSION_CODE_SECOND)) {
                mLocationPermissionGranted = true;
            }
        }
    }

    public void GetDeviceLocation() {
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                Toast.makeText(Maps.this, "Please wait", Toast.LENGTH_LONG).show();

                CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
                Task<Location> task = mFusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,
                        cancellationTokenSource.getToken());
                task.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            progressDialog.dismiss();
                            mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, DEFAULT_ZOOM));
                            marker.setPosition(mCurrentLocation);
                            SetUiWithCurrentLocation();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Couldn't get location Please try again", Toast.LENGTH_SHORT).show();
                            cancellationTokenSource.cancel();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void SetUiWithCurrentLocation() {
        List<Address> addresses = Utility.GetCurrentAddressFromLatLng(this, mCurrentLocation);

        if (addresses != null) {
            String address = addresses.get(0).getAddressLine(0);
            currentLocation.setText(address);
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
                Utility.CheckGPSStatus(this, dialogBox, ACTIVITY_CODE);
            } else {
                //
                mLocationPermissionGranted = false;
                if (Utility.GetLocationPermission(this, LOCATION_PERMISSION_CODE_FIRST,
                        LOCATION_PERMISSION_CODE_SECOND)) {
                    mLocationPermissionGranted = true;
                    Utility.CheckGPSStatus(this, dialogBox, ACTIVITY_CODE);
                }
            }
        } else if (requestCode == LOCATION_PERMISSION_CODE_SECOND) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                Utility.CheckGPSStatus(this, dialogBox, ACTIVITY_CODE);
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
            LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Utility.CheckGPSStatus(this, dialogBox, ACTIVITY_CODE);
            } else {
                GetDeviceLocation();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                GetDeviceLocation();
            }

            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}