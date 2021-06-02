package medmart.loginmedmart.HomeActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import medmart.loginmedmart.CartActivity.Cart;
import medmart.loginmedmart.CartManagement.CartItem;
import medmart.loginmedmart.CartManagement.CartService;
import medmart.loginmedmart.CommonAdapter.ShopAdapter;
import medmart.loginmedmart.CommonAdapter.ShopCard;
import medmart.loginmedmart.HomeActivity.HelperClasses.CategoryAdapter;
import medmart.loginmedmart.HomeActivity.HelperClasses.CategoryCard;
import medmart.loginmedmart.HomeActivity.HelperClasses.NearbyShopResponse;
import medmart.loginmedmart.MapActivity.PlacesSearch;
import medmart.loginmedmart.ProfileActivity.ProfileActivity;
import medmart.loginmedmart.R;
import medmart.loginmedmart.SearchActivity.Search;
import medmart.loginmedmart.UtilityClasses.RetrofitInstance;
import medmart.loginmedmart.UtilityClasses.RetrofitInterface;
import medmart.loginmedmart.UtilityClasses.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePage extends AppCompatActivity {

    private int LOCATION_PERMISSION_CODE_FIRST = 1;
    private int LOCATION_PERMISSION_CODE_SECOND = 2;
    private int ACTIVITY_CODE = 1;
    private static int REQUEST_CHECK_SETTINGS = 3;
    private boolean dialogBox = true;
    private static boolean mLocationPermission = false;
    private LatLng mDefaultLocation = new LatLng(30.767, 76.7774);
    private String mDefaultLocationName = "Chandigarh";
    private static LatLng mCurrentLocation;
    private static FusedLocationProviderClient mFusedLocationProviderClient;
    TextView currentAddress, itemCount;
    ImageView cartImage;
    ProgressDialog progressDialog;

    RecyclerView categoryRecycler;
    CategoryAdapter categoryAdapter;


    RecyclerView shopRecycler;
    ShopAdapter shopAdapter;

    EditText search;

    @Override
    protected void onStart() {
        super.onStart();
        search.getText().clear();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        GetCartData();
    }

    public void ChangeAddress(View view) {
        Intent intent = new Intent(getApplicationContext(), PlacesSearch.class);
        intent.putExtra("class", "homepage");
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
        itemCount = findViewById(R.id.item_count);
        cartImage = findViewById(R.id.cart_icon);
        SetOnEditorAction();
        AttachHooksAndAdapters();
        progressDialog = new ProgressDialog(this);

        PopulateCataegoryRecycler();
        OnMyLocationAccessListener();
        Intent intent = getIntent();
        GetCartData();

        if (intent.getExtras().containsKey("class") &&
                intent.getStringExtra("class").contentEquals("login")) {

            CheckLocationPermission();
            if (mLocationPermission) {
                LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Utility.CheckGPSStatus(this, dialogBox, ACTIVITY_CODE);
                } else {
                    GetDeviceLocation();
                }
            }
        }
    }

    public void OpenCart(View view) {
        Intent intent = new Intent(this, Cart.class);
        startActivity(intent);
    }

    private void GetCartData() {
//        Toast.makeText(this, "Loading cart", Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        String email = sharedPreferences.getString("email", "No email");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("customerId", email);
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<HashMap<String, Object>> cartCall = retrofitInterface.getUserCart(jwt, params);
        cartCall.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                try {
                    HashMap<String, Object> cart = response.body();
                    String cartId = (String) cart.get("cartId");
                    String shopIdString = (String) cart.get("shopId");
                    long shopId = -9;
                    if (!shopIdString.contentEquals("null"))
                        shopId = Long.parseLong(shopIdString);

                    String totalValueString = (String) cart.get("totalItems");
                    int totalItems = Integer.parseInt(totalValueString);
                    Double totalValue = (Double) cart.get("totalValue");
                    ArrayList<CartItem> items = (ArrayList<CartItem>) cart.get("items");
                    CartService.GetInstance().setCartId(cartId);
                    CartService.GetInstance().setShopId(shopId);
                    CartService.GetInstance().setTotalValue(totalValue);
                    CartService.GetInstance().setTotalItems(totalItems);
                    HashMap<String, CartItem> listofItems = new HashMap<String, CartItem>();

                    for (int i = 0; i < items.size(); i++) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<Map<String, String>>() {
                        }.getType();
                        Map<String, String> productMap = gson.fromJson(gson.toJson(items.get(i)), type);
                        String productId = productMap.get("productId");
                        Double price = Double.parseDouble(productMap.get("price"));
                        int quantity = (int) (Double.parseDouble(productMap.get("quantity")));
                        CartItem cartItem = new CartItem(quantity, price, productId);
                        listofItems.put(productId, cartItem);
                    }

                    CartService.GetInstance().setListOfItems(listofItems);
                    CartService.GetInstance().setCartLoaded(true);
                    itemCount.setText(String.valueOf(CartService.GetInstance().getTotalItems()));
                    itemCount.setVisibility(View.VISIBLE);
                    cartImage.setVisibility(View.VISIBLE);
                    System.out.println(CartService.GetInstance().getListOfItems().size());
                    System.out.println("here not but why");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Conenction Error !! ", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void UseCurrentLocation(View view) {
        CheckLocationPermission();

        if (mLocationPermission) {
            LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Utility.CheckGPSStatus(this, dialogBox, ACTIVITY_CODE);
            } else {
                GetDeviceLocation();
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

        shopRecycler.setHasFixedSize(false);
        shopRecycler.setLayoutManager(linearLayoutManager);


        categoryRecycler.setHasFixedSize(true);
        categoryRecycler.setLayoutManager(linearLayoutManager1);
        categoryAdapter = new CategoryAdapter(this);
        categoryRecycler.setAdapter(categoryAdapter);
    }

    public void OpenProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void GetDeviceLocation() {
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        try {
            if (mLocationPermission) {
//                Toast.makeText(HomePage.this, "Please wait loading nearby shops", Toast.LENGTH_LONG).show();
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
                        } else {
                            progressDialog.dismiss();
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
        Intent intent = getIntent();
        if (!(intent.getExtras().containsKey("class") &&
                intent.getStringExtra("class").contentEquals("login"))) {
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_bar);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
        }

        String address = Utility.GetDataFromCache(getApplicationContext(), "useraddress", mDefaultLocationName);
        currentAddress.setText(address);

        double latitude = Double.parseDouble(Utility.GetDataFromCache(getApplicationContext(), "userlatitude",
                String.valueOf(mDefaultLocation.latitude)));
        double longitude = Double.parseDouble(Utility.GetDataFromCache(getApplicationContext(), "userlongitude",
                String.valueOf(mDefaultLocation.longitude)));
        mCurrentLocation = new LatLng(latitude, longitude);
        String location = String.valueOf(mCurrentLocation.latitude) + ',' + String.valueOf(mCurrentLocation.longitude);
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("location", "\"" + location + "\"");
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        System.out.println(jwt);
        Call<List<NearbyShopResponse>> nearbyShopCalls = retrofitInterface.findNearbyShops(jwt, params);
        nearbyShopCalls.enqueue(new Callback<List<NearbyShopResponse>>() {
            @Override
            public void onResponse(Call<List<NearbyShopResponse>> call, Response<List<NearbyShopResponse>> response) {
                System.out.println("reached here late");
                ArrayList<ShopCard> shopCards = new ArrayList<>();
                List<NearbyShopResponse> nearbyShops = response.body();
                for (int i = 0; i < nearbyShops.size(); i++) {
                    System.out.println(nearbyShops.get(i));
                    DecimalFormat df = new DecimalFormat("0.00");
                    ShopCard shopCard = new ShopCard(R.drawable.biyal_shop__1_,
                            nearbyShops.get(i).getShopName(), nearbyShops.get(i).getDistance(), "", nearbyShops.get(i).getShopId());
                    shopCard.setShopAddress(nearbyShops.get(i).getAddress());
                    shopCards.add(shopCard);
                }

                if (shopAdapter == null) {
                    shopAdapter = new ShopAdapter(getApplicationContext(), null);
                    shopRecycler.setAdapter(shopAdapter);
                }

                NotifyShopRecycler(shopCards);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<NearbyShopResponse>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Connection error !!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void CheckLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mLocationPermission = true;
        } else {
            if (Utility.GetLocationPermission(HomePage.this, LOCATION_PERMISSION_CODE_FIRST,
                    LOCATION_PERMISSION_CODE_SECOND)) {
                mLocationPermission = true;
            }
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

                if (Utility.GetLocationPermission(HomePage.this, LOCATION_PERMISSION_CODE_FIRST,
                        LOCATION_PERMISSION_CODE_SECOND)) {
                    mLocationPermission = true;
                    Utility.CheckGPSStatus(this, dialogBox, ACTIVITY_CODE);
                }
            }
        } else if (requestCode == LOCATION_PERMISSION_CODE_SECOND) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermission = true;
                Utility.CheckGPSStatus(this, dialogBox, ACTIVITY_CODE);
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
        categoryCards.add(new CategoryCard(R.drawable.gel, "GEL"));
        categoryCards.add(new CategoryCard(R.drawable.tablet, "TABLET"));
        categoryCards.add(new CategoryCard(R.drawable.powder, "POWDER"));
        categoryCards.add(new CategoryCard(R.drawable.spray3, "SPRAY"));
        categoryCards.add(new CategoryCard(R.drawable.syrup3, "SYRUP"));

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