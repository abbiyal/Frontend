package medmart.loginmedmart.UtilityClasses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import medmart.loginmedmart.HomeActivity.HomePage;
import medmart.loginmedmart.LoginSignUpActivites.Jwt;

import static android.content.Context.MODE_PRIVATE;

public class Utility {

    public static String login(String user,String psswd,Context context){
        System.out.println("here");
        Intent intent=new Intent(context, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return "test";
//        LoginCredentials loginCredentials=new LoginCredentials(user,psswd);
//        RetrofitInterface retrofitInterface= RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
//        Call<Jwt> jwtCall=retrofitInterface.getAccessToken(loginCredentials);
//        jwtCall.enqueue(new Callback<Jwt>() {
//            @Override
//            public void onResponse(Call<Jwt> call, Response<Jwt> response) {
//                System.out.println(call.request().toString());
//                if(response.body().getJwt().contentEquals("null")){
//                    Toast.makeText(context,"Invalid Username or Password",Toast.LENGTH_LONG).show();
//                }
//                else
//                parseData(response.body(),context);
//            }
//
//            @Override
//            public void onFailure(Call<Jwt> call, Throwable t) {
//                System.out.println(call.toString());
//                System.out.println(t.getMessage());
//            }
//        });
//        return "success";

    }
    private static void parseData(Jwt body,Context context) {
        System.out.println(body);
        Toast.makeText(context,body.getJwt(), Toast.LENGTH_LONG).show();
        SharedPreferences sharedPreferences=context.getSharedPreferences("Login_Cookie",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("jwt",body.getJwt());
        editor.putString("roles",body.getRoles());
        editor.apply();
        Intent intent=new Intent(context, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
