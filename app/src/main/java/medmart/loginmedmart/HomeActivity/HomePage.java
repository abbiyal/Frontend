package medmart.loginmedmart.HomeActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import medmart.loginmedmart.HomeActivity.HelperClasses.CategoryAdapter;
import medmart.loginmedmart.HomeActivity.HelperClasses.CategoryCard;
import medmart.loginmedmart.HomeActivity.HelperClasses.ShopAdapter;
import medmart.loginmedmart.HomeActivity.HelperClasses.ShopCard;
import medmart.loginmedmart.R;
import medmart.loginmedmart.SearchActivity.Search;
import medmart.loginmedmart.UtilityClasses.Utility;

public class HomePage<shopRecycler> extends AppCompatActivity {

    private int LOCATION_PERMISSION_CODE_FIRST = 1;
    private int LOCATION_PERMISSION_CODE_SECOND = 2;
    private boolean mLocationPermission = false;

    LatLng mDefaultLocation = new LatLng(30.767, 76.7774);
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
        categoryRecycler = findViewById(R.id.catagory_recyclerview);
        PopulateCataegoryRecycler();
        if (CheckGPSStatus()) {
            // todo read and send to backend then populate view
        }

        new AlertDialog.Builder(this)
                .setTitle("Device Location is not enabled")
                .setMessage("Please enable device location to ensure accurate address and faster delivery")
                .setPositiveButton("Enable device Loaction", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mLocationPermission = Utility.GetLocationPermission(HomePage.this, LOCATION_PERMISSION_CODE_FIRST);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Enter Location Manually", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Todo Enter manually location
                    }
                })
                .create().show();

        shopRecycler = findViewById(R.id.shop_recyclerview);
        PopulateShopRecycler();
    }

    private boolean CheckGPSStatus() {
        LocationManager locationManager = (LocationManager)this.getSystemService(this.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_CODE_FIRST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // todo after getting permission
                mLocationPermission = true;
            } else {
                //
                mLocationPermission = false;
                Utility.GetLocationPermission(HomePage.this, LOCATION_PERMISSION_CODE_SECOND);
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