package medmart.loginmedmart.ForgotPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.chaos.view.PinView;

import java.util.HashMap;

import medmart.loginmedmart.LoginActivity;
import medmart.loginmedmart.R;
import medmart.loginmedmart.RetrofitInstance;
import medmart.loginmedmart.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtp extends AppCompatActivity {

    public void Abort(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void ResendOtp(View view) {
        // todo make call to resend otp function
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_verify_otp);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("Email");

        Button verifyCode = findViewById(R.id.verify_code);

        verifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo verification process
                RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
                HashMap<String,String> otp=new HashMap<>();
                Call<HashMap<String,String>> verifyOtpCall=retrofitInterface.verifyOtp(otp);
                String token=((PinView)findViewById(R.id.otp)).getEditableText().toString();
                otp.put("otp",token);
                verifyOtpCall.enqueue(new Callback<HashMap<String, String>>() {
                    @Override
                    public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                            if(response.body().get("response").contentEquals("Valid")){
                                Intent intent=new Intent(getApplicationContext(),SetNewPassword.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("email",userName);
                                getApplicationContext().startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Wrong OTP",Toast.LENGTH_LONG).show();
                            }
                    }

                    @Override
                    public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"Wrong OTP",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
}