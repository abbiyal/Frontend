package medmart.loginmedmart.LoginSignUpActivites;


import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import medmart.loginmedmart.ForgotPasswordActivities.ForgetPassword;
import medmart.loginmedmart.HomeActivity.HomePage;
import medmart.loginmedmart.R;
import medmart.loginmedmart.UtilityClasses.Utility;


public class LoginActivity extends AppCompatActivity {

    private AwesomeValidation awesomeValidation;

    private static LoginActivity loginActivity;

    public static LoginActivity getInstance() {
        return loginActivity;
    }

    public void GenerateNewPassword(View view) {
        Intent intent = new Intent(getApplicationContext(), ForgetPassword.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        if (Utility.GetDataFromCache(this, "isLogged", "false").contentEquals("true")) {
            Intent intent = new Intent(this, HomePage.class);
            intent.putExtra("class", "login");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.login_username,
                Patterns.EMAIL_ADDRESS, R.string.invalid_username);
        awesomeValidation.addValidation(this, R.id.login_password,
                ".{5,}", R.string.invalid_password);

        setContentView(R.layout.activity_login);

        Button login = (Button) findViewById(R.id.login_button);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //OnCLick Stuff
                if (awesomeValidation.validate()) {
                    EditText username = findViewById(R.id.login_username);
                    EditText password = findViewById(R.id.login_password);
                    String user = username.getText().toString();
                    String psswd = password.getText().toString();
                    Utility.login(user, psswd, getApplicationContext());
                }
            }
        });

        Button signUp = (Button) findViewById(R.id.signup_button);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

}
