package medmart.loginmedmart.ForgotPasswordActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

import medmart.loginmedmart.R;
import medmart.loginmedmart.UtilityClasses.RetrofitInstance;
import medmart.loginmedmart.UtilityClasses.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPassword extends AppCompatActivity {
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_forget_passwrod);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.login_username,
                Patterns.EMAIL_ADDRESS, R.string.invalid_username);

        Button next = findViewById(R.id.next_button);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (awesomeValidation.validate()) {
                    RetrofitInterface retrofitInstance = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
                    HashMap<String, String> requestObject = new HashMap<>();
                    String email = ((TextInputLayout) findViewById(R.id.login_username)).getEditText().getText().toString();
                    requestObject.put("email", email);
                    Call<HashMap<String, String>> forgotCall = retrofitInstance.sendToken(requestObject);

                    ProgressDialog progressDialog = new ProgressDialog(ForgetPassword.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_bar);
                    progressDialog.setCancelable(false);
                    progressDialog.getWindow().setBackgroundDrawableResource(
                            android.R.color.transparent
                    );

                    forgotCall.enqueue(new Callback<HashMap<String, String>>() {
                        @Override
                        public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                            progressDialog.dismiss();
                            if (response.body().get("response").contentEquals("success")) {
                                Intent intent = new Intent(getApplicationContext(), VerifyOtp.class);
                                intent.putExtra("Email", ((TextInputLayout) findViewById(R.id.login_username)).getEditText().getText().toString());
                                intent.putExtra("class", "forgetpassword");
                                startActivity(intent);
                            } else if (response.body().get("response").contentEquals("No User Found")) {
                                Toast.makeText(getApplicationContext(), "User Not Found", Toast.LENGTH_LONG);
                            } else if (response.body().get("response").contentEquals("Connetion Error !!")) {
                                Toast.makeText(getApplicationContext(), "Connection Error Retry", Toast.LENGTH_LONG);
                            }
                        }

                        @Override
                        public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                            progressDialog.dismiss();
                            System.out.println("Connection Error !!!");
                        }
                    });
                }
            }
        });

    }
}