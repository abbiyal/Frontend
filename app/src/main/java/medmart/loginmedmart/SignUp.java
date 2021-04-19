package medmart.loginmedmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaCodec;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;

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

    private void RegisterUser()  {
        if (awesomeValidation.validate()) {
            // todo after sign up validation
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
                RegisterUser();
            }
        });

        Button login = findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}