package medmart.loginmedmart.ForgotPasswordActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

import medmart.loginmedmart.LoginSignUpActivites.LoginActivity;
import medmart.loginmedmart.R;
import medmart.loginmedmart.UtilityClasses.RetrofitInstance;
import medmart.loginmedmart.UtilityClasses.RetrofitInterface;
import medmart.loginmedmart.UtilityClasses.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtp extends AppCompatActivity {

    private boolean IsOtpVerified = false;
    String parentActivity;
    String userName;
    String password;

    public void ResendOtp(View view) {
        RetrofitInterface retrofitInstance = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        HashMap<String, String> requestObject = new HashMap<>();
        String email = getIntent().getStringExtra("Email");
        requestObject.put("email", email);
        Call<HashMap<String, String>> forgotCall = retrofitInstance.sendToken(requestObject);

        forgotCall.enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                if (response.body().get("response").contentEquals("success")) {
                    Toast.makeText(getApplicationContext(), "OTP SENT !!", Toast.LENGTH_LONG);
                } else if (response.body().get("response").contentEquals("No User Found")) {
                    Toast.makeText(getApplicationContext(), "User Not Found", Toast.LENGTH_LONG);
                } else if (response.body().get("response").contentEquals("Connetion Error !!")) {
                    Toast.makeText(getApplicationContext(), "Connection Error Retry", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                System.out.println("Connection Error !!!");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_verify_otp);

        Intent intent = getIntent();
        userName = intent.getStringExtra("Email");
        parentActivity = intent.getStringExtra("class");
        password = intent.getStringExtra("password");

        Button verifyCode = findViewById(R.id.verify_code);

        verifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
                HashMap<String, String> otp = new HashMap<>();
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
                String email = sharedPreferences.getString("email", "No email");
                Call<HashMap<String, String>> verifyOtpCall = retrofitInterface.verifyOtp(otp);
                String token = ((PinView) findViewById(R.id.otp)).getEditableText().toString();
                otp.put("otp", token);
                verifyOtpCall.enqueue(new Callback<HashMap<String, String>>() {
                    @Override
                    public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                        if (response.body().get("response").contentEquals("Valid")) {
                            IsOtpVerified = true;
                            if (parentActivity.contentEquals("signup")) {
                                Utility.login(userName, password, getApplicationContext());
                                finish();
                            } else {
                                Intent intent = new Intent(getApplicationContext(), SetNewPassword.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("email", userName);
                                getApplicationContext().startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong OTP", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Wrong OTP", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (parentActivity.contentEquals("signup")) {
            if (!IsOtpVerified) {
                System.out.println("here for good");
                RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
                HashMap<String, String> params = new HashMap<>();
                params.put("email", userName);

                Call<HashMap<String, String>> deleteUser = retrofitInterface.DeleteUser(params);

                deleteUser.enqueue(new Callback<HashMap<String, String>>() {
                    @Override
                    public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                        if (response.body().get("response").contentEquals("success")) {
                            System.out.println("ho gya delete");
                        }
                    }

                    @Override
                    public void onFailure(Call<HashMap<String, String>> call, Throwable t) {

                    }
                });
            }
        }
    }


}