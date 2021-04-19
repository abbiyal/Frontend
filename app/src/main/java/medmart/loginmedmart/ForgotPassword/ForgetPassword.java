package medmart.loginmedmart.ForgotPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import medmart.loginmedmart.MainActivity;
import medmart.loginmedmart.R;

public class ForgetPassword extends AppCompatActivity {

    private AwesomeValidation awesomeValidation;

    public void GoBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_passwrod);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.login_username,
                Patterns.EMAIL_ADDRESS, R.string.invalid_username);

        Button next = findViewById(R.id.next_button);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (awesomeValidation.validate()) {
                        Intent intent = new Intent(getApplicationContext(), VerifyOtp.class);
                        intent.putExtra("Email", ((TextInputLayout)findViewById(R.id.login_username)).getEditText().getText().toString());
                        startActivity(intent);
                    }
            }
        });

    }
}