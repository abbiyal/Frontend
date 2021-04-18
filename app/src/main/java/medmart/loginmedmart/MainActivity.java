package medmart.loginmedmart;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        Button login = (Button)findViewById(R.id.login_button);

        login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //OnCLick Stuff
                login();
            }
        });
    }

    protected void login(){
        EditText username= findViewById(R.id.login_username);
        EditText password=findViewById(R.id.login_password);
        String user=username.getText().toString();
        String psswd=password.getText().toString();
        LoginCredentials loginCredentials=new LoginCredentials(user,psswd);
        RetrofitInterface retrofitInterface=RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<Jwt> jwtCall=retrofitInterface.getAccessToken(loginCredentials);
        jwtCall.enqueue(new Callback<Jwt>() {
            @Override
            public void onResponse(Call<Jwt> call, Response<Jwt> response) {
                    System.out.println(call.request().toString());
                    parseData(response.body());
            }

            @Override
            public void onFailure(Call<Jwt> call, Throwable t) {
                    System.out.println(call.toString());
                    System.out.println(t.getMessage());
            }
        });

    }

    private void parseData(Jwt body) {
        System.out.println(body);
        Context context=getApplicationContext();
        Toast.makeText(context,body.getJwt(), Toast.LENGTH_LONG).show();
        SharedPreferences sharedPreferences=getSharedPreferences("Login_Cookie",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("jwt",body.getJwt());
        editor.putString("roles",body.getRoles());
        editor.apply();
    }

}
