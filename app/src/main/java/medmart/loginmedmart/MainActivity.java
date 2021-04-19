package medmart.loginmedmart;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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

    private static MainActivity mainActivity;

    public static MainActivity getInstance(){
        return mainActivity;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.mainactivity);

        Button login = (Button)findViewById(R.id.login_button);

        login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //OnCLick Stuff
                EditText username= findViewById(R.id.login_username);
                EditText password=findViewById(R.id.login_password);
                String user=username.getText().toString();
                String psswd=password.getText().toString();
                login(user,psswd);
            }
        });

        Button signUp = (Button)findViewById(R.id.signup_button);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
    }
    public String login(String user,String psswd){
        System.out.println("here");
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
        return "success";

    }
    protected void parseData(Jwt body) {
        System.out.println(body);
        Context context=getApplicationContext();
        Toast.makeText(context,body.getJwt(), Toast.LENGTH_LONG).show();
        SharedPreferences sharedPreferences=getSharedPreferences("Login_Cookie",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("jwt",body.getJwt());
        editor.putString("roles",body.getRoles());
        editor.apply();
        System.out.println("before intent");
        Intent intent=new Intent(this,Stub.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            System.out.println(login(data.getStringExtra("username"),data.getStringExtra("password")));
        }
    }
}
