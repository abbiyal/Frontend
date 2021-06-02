package medmart.loginmedmart.ForgotPasswordActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

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

public class SetNewPassword extends AppCompatActivity {
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_set_new_password);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation.addValidation(this, R.id.login_password,
                ".{5,}", R.string.invalid_confrim_password);

        awesomeValidation.addValidation(this, R.id.login_password_confirm,
                R.id.login_password, R.string.invalid_confrim_password);

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) {
                    RetrofitInterface retrofitInterface= RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
                    HashMap<String,String> map=new HashMap<>();
                    String email = getIntent().getStringExtra("email");
                    System.out.println(email);
                    map.put("email", email);
                    map.put("password",((TextInputLayout)(findViewById(R.id.login_password))).getEditText().getText().toString());
                    Call<HashMap<String,String>> updatePasswordCall=retrofitInterface.updatePassword(map);
                    ProgressDialog progressDialog = new ProgressDialog(SetNewPassword.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_bar);
                    progressDialog.setCancelable(false);
                    progressDialog.getWindow().setBackgroundDrawableResource(
                            android.R.color.transparent
                    );

                    updatePasswordCall.enqueue(new Callback<HashMap<String, String>>() {
                        @Override
                        public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                            String ans=response.body().get("response");
                            System.out.println(ans);
                            progressDialog.dismiss();
                            if(response.body().get("response").contentEquals("success")){
                                Intent intent = new Intent(getApplicationContext(), ForgetPasswordSuccessMessage.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                        }
                    });

                    // if success then let it go to next activity.. code written nd if not do as required

                }
            }
        });
    }
}