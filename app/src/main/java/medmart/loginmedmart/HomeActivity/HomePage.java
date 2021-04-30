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
import android.content.IntentSender;
import android.content.pm.PackageManager;
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

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import medmart.loginmedmart.HomeActivity.HelperClasses.CategoryAdapter;
import medmart.loginmedmart.HomeActivity.HelperClasses.CategoryCard;
import medmart.loginmedmart.HomeActivity.HelperClasses.ShopAdapter;
import medmart.loginmedmart.HomeActivity.HelperClasses.ShopCard;
import medmart.loginmedmart.MapActivity.Maps;
import medmart.loginmedmart.MapActivity.PlacesSearch;
import medmart.loginmedmart.R;
import medmart.loginmedmart.SearchActivity.Search;
import medmart.loginmedmart.UtilityClasses.Utility;

public class HomePage extends AppCompatActivity {

    private int LOCATION_PERMISSION_CODE_FIRST = 1;
    private int LOCATION_PERMISSION_CODE_SECOND = 2;
    private boolean dialogBox = true;
    private boolean mLocationPermission = false;
    private LatLng mDefaultLocation = new LatLng(30.767, 76.7774);
    private String mDefaultLocationName = "Chandigarh";
    private LatLng mCurrentLocation;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
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
        search = findViewById(R.id.search_text);
        SetOnEditorAction();
        TextView currentAddress = findViewById(R.id.current_address);
        currentAddress.setText(Utility.GetDataFromCache(this, "useraddress", mDefaultLocationName));

        categoryRecycler = findViewById(R.id.catagory_recyclerview);
        PopulateCataegoryRecycler();
        Intent intent = getIntent();

        if (intent.getExtras().containsKey("class") && intent.getStringExtra("class").contentEquals("login")) {
            Toast.makeText(this, "here but why", Toast.LENGTH_LONG).show();
            CheckLocationPermission();
        }

        shopRecycler = findViewById(R.id.shop_recyclerview);
        PopulateShopRecycler();
    }

    private void CheckLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermission = true;
            Utility.CheckGPSStatus(this, dialogBox);
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
                Utility.CheckGPSStatus(this, dialogBox);
            } else {
                //
                mLocationPermission = false;
                Utility.GetLocationPermission(HomePage.this, LOCATION_PERMISSION_CODE_FIRST, LOCATION_PERMISSION_CODE_SECOND);
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

    private void PopulateShopRecycler() {
        // todo change it with locations based data
        shopRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        shopRecycler.setLayoutManager(linearLayoutManager);

        ArrayList<ShopCard> shopCards = new ArrayList<>();
        shopCards.add(new ShopCard(R.drawable.biyal_shop__1_, "Biyal Pharmaceuticals", "2.3Km"));
        shopCards.add(new ShopCard(R.drawable.biyal_shop__1_, "Biyal Pharmaceuticals", "2.3Km"));
        shopCards.add(new ShopCard(R.drawable.biyal_shop__1_, "Biyal Pharmaceuticals", "2.3Km"));
        shopCards.add(new ShopCard(R.drawable.biyal_shop__1_, "Biyal Pharmaceuticals", "2.3Km"));
        shopCards.add(new ShopCard(R.drawable.biyal_shop__1_, "Biyal Pharmaceuticals", "2.3Km"));
        shopCards.add(new ShopCard(R.drawable.biyal_shop__1_, "Biyal Pharmaceuticals", "2.3Km"));

        shopAdapter = new ShopAdapter(shopCards);
        shopRecycler.setAdapter(shopAdapter);
    }

    private void PopulateCataegoryRecycler() {
        categoryRecycler.setHasFixedSize(true);
        categoryRecycler.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));

        ArrayList<CategoryCard> categoryCards = new ArrayList<>();
        categoryCards.add(new CategoryCard(R.drawable.app_logo, "Gel"));
        categoryCards.add(new CategoryCard(R.drawable.app_logo, "Gel"));
        categoryCards.add(new CategoryCard(R.drawable.app_logo, "Gel"));
        categoryCards.add(new CategoryCard(R.drawable.app_logo, "Gel"));
        categoryCards.add(new CategoryCard(R.drawable.app_logo, "Gel"));
        categoryAdapter = new CategoryAdapter(categoryCards);
        categoryRecycler.setAdapter(categoryAdapter);
    }


}