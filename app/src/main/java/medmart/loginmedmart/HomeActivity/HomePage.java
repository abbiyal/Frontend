package medmart.loginmedmart.HomeActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

import medmart.loginmedmart.HomeActivity.HelperClasses.CategoryAdapter;
import medmart.loginmedmart.HomeActivity.HelperClasses.CategoryCard;
import medmart.loginmedmart.HomeActivity.HelperClasses.ShopAdapter;
import medmart.loginmedmart.HomeActivity.HelperClasses.ShopCard;
import medmart.loginmedmart.MapActivity.PlacesSearch;
import medmart.loginmedmart.R;
import medmart.loginmedmart.SearchActivity.Search;
import medmart.loginmedmart.UtilityClasses.Utility;

public class HomePage extends AppCompatActivity {

    private int LOCATION_PERMISSION_CODE_FIRST = 1;
    private int LOCATION_PERMISSION_CODE_SECOND = 2;
    private int ACTIVITY_CODE = 1;
    private static int REQUEST_CHECK_SETTINGS = 3;
    private boolean dialogBox = true;
    private boolean mLocationPermission = false;
    private LatLng mDefaultLocation = new LatLng(30.767, 76.7774);
    private String mDefaultLocationName = "Chandigarh";
    private LatLng mCurrentLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static HomePage homeInstance;
    TextView currentAddress;

    public static HomePage GetInstance() {
        if (homeInstance == null) {
            homeInstance = new HomePage();
        }

        return homeInstance;
    }

    RecyclerView categoryRecycler;
    CategoryAdapter categoryAdapter;


    RecyclerView shopRecycler;
    ShopAdapter shopAdapter;

    EditText search;
    ImageView searchbutton;

    @Override
    protected void onStart() {
        super.onStart();
        search.getText().clear();
    }

    public void ChangeAddress(View view) {
        Intent intent = new Intent(getApplicationContext(), PlacesSearch.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.teal_700));
        setContentView(R.layout.activity_home_page);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        search = findViewById(R.id.search_text);
        SetOnEditorAction();
        AttachHooksAndAdapters();

        PopulateCataegoryRecycler();
        OnMyLocationAccessListener();

        Intent intent = getIntent();

        if (intent.getExtras().containsKey("class") &&
                intent.getStringExtra("class").contentEquals("login")) {
            CheckLocationPermission();

            if (mLocationPermission) {
                LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Utility.CheckGPSStatus(this, dialogBox, ACTIVITY_CODE);
                } else {
                    // todo if location is already on
                    GetDeviceLocation();
                }
            }
        }
    }

    private void AttachHooksAndAdapters() {
        currentAddress = findViewById(R.id.current_address);
        categoryRecycler = findViewById(R.id.catagory_recyclerview);
        shopRecycler = findViewById(R.id.shop_recyclerview);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);

        shopRecycler.setHasFixedSize(true);
        shopRecycler.setLayoutManager(linearLayoutManager);
        shopAdapter = new ShopAdapter();
        shopRecycler.setAdapter(shopAdapter);

        categoryRecycler.setHasFixedSize(true);
        categoryRecycler.setLayoutManager(linearLayoutManager1);
        categoryAdapter = new CategoryAdapter();
        categoryRecycler.setAdapter(categoryAdapter);
    }

    public void GetDeviceLocation() {
        try {
            if (mLocationPermission) {
                Toast.makeText(this, "Please Wait loading nearby shops", Toast.LENGTH_LONG).show();

                CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
                Task<Location> task = mFusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,
                        cancellationTokenSource.getToken());
                task.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            List<Address> addresses = Utility.GetCurrentAddressFromLatLng(getApplicationContext(),
                                    mCurrentLocation);

                            if (addresses != null) {
                                String address = addresses.get(0).getAddressLine(0);
                                Utility.StoreDataInCache(getApplicationContext(), "usercity", addresses.get(0).getLocality());
                                Utility.StoreDataInCache(getApplicationContext(), "useraddress", address);
                                currentAddress.setText(address);
                            } else {
                                currentAddress.setText("Unknown Location");
                            }

                            Utility.StoreDataInCache(getApplicationContext(), "userlongitude",
                                    String.valueOf(mCurrentLocation.longitude));
                            Utility.StoreDataInCache(getApplicationContext(), "userlatitude",
                                    String.valueOf(mCurrentLocation.latitude));

                            OnMyLocationAccessListener();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Couldn't get Location enter manually", Toast.LENGTH_SHORT).show();
                            cancellationTokenSource.cancel();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void OnMyLocationAccessListener() {
        String address = Utility.GetDataFromCache(getApplicationContext(), "useraddress", mDefaultLocationName);
        currentAddress.setText(address);

        double latitude = Double.parseDouble(Utility.GetDataFromCache(getApplicationContext(), "userlatitude",
                String.valueOf(mDefaultLocation.latitude)));
        double longitude = Double.parseDouble(Utility.GetDataFromCache(getApplicationContext(), "userlatitude",
                String.valueOf(mDefaultLocation.longitude)));
        mCurrentLocation = new LatLng(latitude, longitude);
        // todo backend call get result of shops and notiyfy shop recycler with new ArrayList
        //  and comment below, use default image for now

        ArrayList<ShopCard> shopCards = new ArrayList<>();
        shopCards.add(new ShopCard(R.drawable.biyal_shop__1_, "Biyal Pharmaceuticals", "2.3Km"));
        shopCards.add(new ShopCard(R.drawable.biyal_shop__1_, "Biyal Pharmaceuticals", "2.3Km"));
        shopCards.add(new ShopCard(R.drawable.biyal_shop__1_, "Biyal Pharmaceuticals", "2.3Km"));
        shopCards.add(new ShopCard(R.drawable.biyal_shop__1_, "Biyal Pharmaceuticals", "2.3Km"));
        shopCards.add(new ShopCard(R.drawable.biyal_shop__1_, "Biyal Pharmaceuticals", "2.3Km"));
        shopCards.add(new ShopCard(R.drawable.biyal_shop__1_, "Biyal Pharmaceuticals", "2.3Km"));

        NotifyShopRecycler(shopCards);
    }

    private void CheckLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermission = true;
        } else {
            mLocationPermission = Utility.GetLocationPermission(this, LOCATION_PERMISSION_CODE_FIRST, LOCATION_PERMISSION_CODE_SECOND);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_CODE_FIRST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermission = true;
                Utility.CheckGPSStatus(this, dialogBox, ACTIVITY_CODE);
            } else {
                //
                mLocationPermission = false;
                mLocationPermission = Utility.GetLocationPermission(HomePage.this, LOCATION_PERMISSION_CODE_FIRST, LOCATION_PERMISSION_CODE_SECOND);
            }
        } else if (requestCode == LOCATION_PERMISSION_CODE_SECOND) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // todo after getting permission
                mLocationPermission = true;
            } else {
                //
                mLocationPermission = false;
                new AlertDialog.Builder(this)
                        .setMessage("It looks like you have turned off permissions required for this feature. It can be enabled under Phone" +
                                "Settings > Apps > Medmart > Permissions")
                        .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        })
                        .create().show();
            }
        }
    }

    private void SetOnEditorAction() {
        search.setImeActionLabel("Go", EditorInfo.IME_ACTION_NEXT);
        search.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    CallSearch(v);
                    return true;
                }
                return false;
            }
        });
    }

    public void CallSearch(View view) {
        String query = search.getText().toString();
        Intent intent = new Intent(this, Search.class);
        intent.putExtra("query", query);
        startActivity(intent);
    }

    private void NotifyShopRecycler(ArrayList<ShopCard> shopCards) {
        shopAdapter.SetContent(shopCards);
    }

    private void PopulateCataegoryRecycler() {
        ArrayList<CategoryCard> categoryCards = new ArrayList<>();
        categoryCards.add(new CategoryCard(R.drawable.app_logo, "Gel"));
        categoryCards.add(new CategoryCard(R.drawable.app_logo, "Gel"));
        categoryCards.add(new CategoryCard(R.drawable.app_logo, "Gel"));
        categoryCards.add(new CategoryCard(R.drawable.app_logo, "Gel"));
        categoryCards.add(new CategoryCard(R.drawable.app_logo, "Gel"));

        categoryAdapter.SetContent(categoryCards);
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