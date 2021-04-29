package medmart.loginmedmart.ProfileActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import medmart.loginmedmart.R;
import medmart.loginmedmart.UtilityClasses.RetrofitInstance;
import medmart.loginmedmart.UtilityClasses.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        TextView logout=findViewById(R.id.logout);
        ImageView editName=findViewById(R.id.edit_name);
        ImageView editPhone=findViewById(R.id.edit_phone);

        editPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo Pankaj: Start new Fragment to get new Phone No
                String Phone="";
                editPhoneofUser(Phone);
            }
        });
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Todo Pankaj: Start new Fragment to get new Name
                String email=""; // Todo populate email from profile page
                editNameofUser(email);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {
        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("Login_Cookie",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.remove("jwt");
        editor.remove("roles");
        editor.putBoolean("isLogged",false);
    }
    private void editNameofUser(String email){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("email", email);
        String jwt="Bearer " ;//+ getCacheData(jwt); //todo populate jwt from cache
        RetrofitInterface retrofitInterface= RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<HashMap<String,String>> updateCall = retrofitInterface.updateName(jwt,params);
        updateCall.enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                if(response.body().get("response").contentEquals("success")){
                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Connection Error !! ",Toast.LENGTH_LONG);

            }
        });
    }

    private void editPhoneofUser(String phone){

        HashMap<String,String> params = new HashMap<String,String>();
        params.put("phone", phone);
        String jwt="Bearer " ;//+ getCacheData(jwt); //todo Abhishek: populate jwt from cache
        RetrofitInterface retrofitInterface= RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<HashMap<String,String>> updateCall = retrofitInterface.updatePhone(jwt,params);
        updateCall.enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                if(response.body().get("response").contentEquals("success")){
                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Connection Error !! ",Toast.LENGTH_LONG);

            }
        });


    }

}