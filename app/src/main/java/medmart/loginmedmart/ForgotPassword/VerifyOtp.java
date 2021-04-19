package medmart.loginmedmart.ForgotPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import medmart.loginmedmart.LoginActivity;
import medmart.loginmedmart.R;

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
        setContentView(R.layout.activity_verify_otp);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("email");

        Button verifyCode = findViewById(R.id.verify_code);

        verifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo verification process
            }
        });

    }
}