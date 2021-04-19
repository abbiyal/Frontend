package medmart.loginmedmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {
    private AwesomeValidation awesomeValidation;

    private void AddValidation() {
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        // name
        awesomeValidation.addValidation(this, R.id.Name,
                RegexTemplate.NOT_EMPTY, R.string.invalid_name);
        // userName or Email
        awesomeValidation.addValidation(this, R.id.login_username,
                Patterns.EMAIL_ADDRESS, R.string.invalid_username);
        // phone no
        awesomeValidation.addValidation(this, R.id.phone_number,
                "^[5-9][0-9]{9}$", R.string.invalid_phone);

        // password
        awesomeValidation.addValidation(this, R.id.login_password,
                ".{5,}", R.string.invalid_password);
        // confirm password
        // phone no
        awesomeValidation.addValidation(this, R.id.login_password_confirm,
                R.id.login_password, R.string.invalid_confrim_password);
    }

    private void RegisterUser(HashMap<String,String> jsonObject)  {
        if (awesomeValidation.validate()) {
            // todo after sign up validation
            RetrofitInterface retrofitInterface=RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
            Call<HashMap<String,String>> addusercall=retrofitInterface.addUser(jsonObject);
            addusercall.enqueue(new Callback<HashMap<String,String>>() {
                @Override
                public void onResponse(Call<HashMap<String,String>> call, Response<HashMap<String,String>> response) {
                    if (response.body().get("response").contentEquals("success")) {
                        Utility.login(jsonObject.get("username"), jsonObject.get("password"), getApplicationContext());
                    }
                }
                @Override
                public void onFailure(Call<HashMap<String,String>> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),"Failure to Signup",Toast.LENGTH_LONG);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);
        AddValidation();

        Button signUp = findViewById(R.id.regUser);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=((TextInputLayout)findViewById(R.id.login_username)).getEditText().getText().toString();
                String password=((TextInputLayout)findViewById(R.id.login_password)).getEditText().getText().toString();
                String phone=((TextInputLayout) findViewById(R.id.phone_number)).getEditText().getText().toString();
                String name=((TextInputLayout) findViewById(R.id.Name)).getEditText().getText().toString();
                HashMap<String, String> jsonObject=new HashMap<>();
                jsonObject.put("username",username);
                jsonObject.put("password",password);
                jsonObject.put("phone",phone);
                jsonObject.put("name",name);
                jsonObject.put("role","ROLE_USER");
                RegisterUser(jsonObject);
            }
        });

        Button login = findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}