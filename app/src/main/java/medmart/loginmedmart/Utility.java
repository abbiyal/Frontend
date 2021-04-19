package medmart.loginmedmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import medmart.loginmedmart.Jwt;
import medmart.loginmedmart.LoginCredentials;
import medmart.loginmedmart.RetrofitInstance;
import medmart.loginmedmart.RetrofitInterface;
import medmart.loginmedmart.Stub;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static androidx.core.content.ContextCompat.startActivity;

public class Utility {

    public static String login(String user,String psswd,Context context){
        System.out.println("here");
        LoginCredentials loginCredentials=new LoginCredentials(user,psswd);
        RetrofitInterface retrofitInterface= RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<Jwt> jwtCall=retrofitInterface.getAccessToken(loginCredentials);
        jwtCall.enqueue(new Callback<Jwt>() {
            @Override
            public void onResponse(Call<Jwt> call, Response<Jwt> response) {
                System.out.println(call.request().toString());
                parseData(response.body(),context);
            }

            @Override
            public void onFailure(Call<Jwt> call, Throwable t) {
                System.out.println(call.toString());
                System.out.println(t.getMessage());
            }
        });
        return "success";

    }
    private static void parseData(Jwt body,Context context) {
        System.out.println(body);
        Toast.makeText(context,body.getJwt(), Toast.LENGTH_LONG).show();
        SharedPreferences sharedPreferences=context.getSharedPreferences("Login_Cookie",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("jwt",body.getJwt());
        editor.putString("roles",body.getRoles());
        editor.apply();
        System.out.println("before intent");
        Intent intent=new Intent(context, Stub.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
