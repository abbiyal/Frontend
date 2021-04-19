package medmart.loginmedmart.ForgotPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.BaseBundle;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import medmart.loginmedmart.R;

public class SetNewPassword extends AppCompatActivity {
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    // todo change password in backend

                    // if success then let it go to next activity.. code written nd if not do as required

                    Intent intent = new Intent(getApplicationContext(), ForgetPasswordSuccessMessage.class);
                    startActivity(intent);
                }
            }
        });
    }
}