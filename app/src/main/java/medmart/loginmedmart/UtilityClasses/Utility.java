package medmart.loginmedmart.UtilityClasses;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import medmart.loginmedmart.HomeActivity.HomePage;
import medmart.loginmedmart.LoginSignUpActivites.Jwt;
import medmart.loginmedmart.LoginSignUpActivites.LoginCredentials;
import medmart.loginmedmart.MapActivity.Maps;
import medmart.loginmedmart.MapActivity.PlacesSearch;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class Utility extends AppCompatActivity {

    private static int REQUEST_CHECK_SETTINGS = 3;

    public static void login(String user, String psswd, Context context) {
//        System.out.println("here");
//        Intent intent = new Intent(context, HomePage.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
//        return;
        LoginCredentials loginCredentials = new LoginCredentials(user, psswd);
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<Jwt> jwtCall = retrofitInterface.getAccessToken(loginCredentials);
        jwtCall.enqueue(new Callback<Jwt>() {
            @Override
            public void onResponse(Call<Jwt> call, Response<Jwt> response) {
                System.out.println(call.request().toString());
                if (response.body().getJwt().contentEquals("null")) {
                    Toast.makeText(context, "Invalid Username or Password", Toast.LENGTH_LONG).show();
                } else
                    parseData(response.body(), context);
            }

            @Override
            public void onFailure(Call<Jwt> call, Throwable t) {
                System.out.println(call.toString());
                System.out.println(t.getMessage());
            }
        });
    }

    private static void parseData(Jwt body, Context context) {
        System.out.println(body);
        Toast.makeText(context, body.getJwt(), Toast.LENGTH_LONG).show();
        StoreDataInCache(context, "roles", body.getRoles());
        StoreDataInCache(context, "jwt", body.getJwt());
        StoreDataInCache(context, "isLogged", "true");
        // todo email, phone and name store in cache

        Intent intent = new Intent(context, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("class","login");
        context.startActivity(intent);
    }

    public static boolean StoreDataInCache(Context context, String key, String value) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("Login_Cookie", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value).apply();
        } catch (Exception exception) {
            return false;
        }

        return true;
    }

    public static String GetDataFromCache(Context context, String key, String defaultString) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultString);
    }

    public static boolean GetLocationPermission(Activity context, int codeFirst, int codeSecond) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(context)
                        .setTitle("Location Permission Denied")
                        .setMessage("Medmart usses this permission to detect your current location and " +
                                "show you nearest Pharmacies. Are you sure you want to deny this permission")
                        .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        codeSecond);
                            }
                        })
                        .setNegativeButton("I'M SURE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, codeFirst);
            }
        }

        return false;
    }

    public static void CheckGPSStatus(Activity context, boolean dialogBox, int activityCode) {
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (dialogBox) {
                new android.app.AlertDialog.Builder(context)
                        .setTitle("Device Location is not enabled")
                        .setMessage("Please enable device location to ensure accurate address and faster delivery")
                        .setPositiveButton("Enable device Loaction", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                TurnOnGps(context, activityCode);
                            }
                        })
                        .setNegativeButton("Enter Location Manually", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(context, PlacesSearch.class);
                                context.startActivity(intent);
                            }
                        })
                        .create().show();
            } else {
                TurnOnGps(context, activityCode);
            }
        } else {
            if (activityCode == 1) {
                ((HomePage)context).GetDeviceLocation();
            }
            else if (activityCode == 2) {
                ((Maps)context).GetDeviceLocation();
            }
        }
    }

    private static void TurnOnGps(Activity activity, int activityCode) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build());

        result.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if (activityCode == 1) {
                    ((HomePage)activity).GetDeviceLocation();
                }
                else if (activityCode == 2) {
                    ((Maps)activity).GetDeviceLocation();
                }
            }
        });

        result.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(activity,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                    }
                }
            }
        });
    }

    public static List<Address> GetCurrentAddressFromLatLng(Context context, @NotNull LatLng mCurrentLocation) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(mCurrentLocation.latitude,
                    mCurrentLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addresses;
    }
}
