package medmart.loginmedmart.UtilityClasses;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import medmart.loginmedmart.HomeActivity.HomePage;
import medmart.loginmedmart.LoginSignUpActivites.Jwt;
import medmart.loginmedmart.LoginSignUpActivites.LoginCredentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class Utility {

    public static void login(String user, String psswd, Context context) {
//        System.out.println("here");
//        Intent intent = new Intent(context, HomePage.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
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
        Intent intent = new Intent(context, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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

    public static void GetLocationPermission(Context context, int code) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // todo permission alrdy granted
            Toast.makeText(context, "alrdy granted", Toast.LENGTH_LONG).show();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // todo if alrdy denied once (show dialog if says no end app)
                new AlertDialog.Builder(context)
                        .setTitle("Location Permission Denied")
                        .setMessage("Medmart usses this permission to detect your current location and " +
                                "show you nearest Pharmacies. Are you sure you want to deny this permission")
                        .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        code);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("I'M SURE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // todo handle denial
                            }
                        })
                        .create().show();
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, code);
            }
        }
    }
}
