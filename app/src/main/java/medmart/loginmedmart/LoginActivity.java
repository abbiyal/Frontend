package medmart.loginmedmart;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import medmart.loginmedmart.ForgotPassword.ForgetPassword;


public class LoginActivity extends AppCompatActivity {

    private static LoginActivity loginActivity;

    public static LoginActivity getInstance(){
        return loginActivity;
    }

    public void GenerateNewPassword(View view){
            Intent intent = new Intent(getApplicationContext(), ForgetPassword.class);
            startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        Button login = (Button)findViewById(R.id.login_button);

        login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //OnCLick Stuff
                EditText username= findViewById(R.id.login_username);
                EditText password=findViewById(R.id.login_password);
                String user=username.getText().toString();
                String psswd=password.getText().toString();
                Utility.login(user,psswd,getApplicationContext());
            }
        });

        Button signUp = (Button)findViewById(R.id.signup_button);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

}
