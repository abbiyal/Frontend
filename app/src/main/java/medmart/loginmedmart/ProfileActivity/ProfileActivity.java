package medmart.loginmedmart.ProfileActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;

import java.util.HashMap;

import medmart.loginmedmart.LoginSignUpActivites.LoginActivity;
import medmart.loginmedmart.R;
import medmart.loginmedmart.UtilityClasses.RetrofitInstance;
import medmart.loginmedmart.UtilityClasses.RetrofitInterface;
import medmart.loginmedmart.UtilityClasses.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    EditText phoneEdit, nameEdit, emailEdit, oldPassword, newPassword, confirmNewPassword;
    Button save;
    private AwesomeValidation nameValidation;
    private AwesomeValidation phoneValidation;
    private Dialog passwordChangeALert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.teal_700));
        setContentView(R.layout.profile_activity);

        nameEdit = findViewById(R.id.name_edittext);
        phoneEdit = findViewById(R.id.phone_edittext);
        emailEdit = findViewById(R.id.email);
        save = findViewById(R.id.save);

        SetUi();
        nameValidation = new AwesomeValidation(ValidationStyle.BASIC);
        nameValidation.addValidation(this, R.id.name_edittext,
                RegexTemplate.NOT_EMPTY, R.string.invalid_name);

        phoneValidation = new AwesomeValidation(ValidationStyle.BASIC);
        phoneValidation.addValidation(this, R.id.phone_edittext,
                "^[5-9][0-9]{9}$", R.string.invalid_phone);

        SetOnclickListeners();
    }

    private void SetOnclickListeners() {
        nameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    save.setVisibility(View.VISIBLE);
                }
            }
        });

        nameEdit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(nameEdit.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        phoneEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    save.setVisibility(View.VISIBLE);
                }
            }
        });

        phoneEdit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(phoneEdit.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save.setVisibility(View.INVISIBLE);

                if (nameEdit.isFocused()) {
                    nameEdit.clearFocus();
                    if (nameValidation.validate()) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(nameEdit.getWindowToken(), 0);
                        editNameofUser(emailEdit.getText().toString(), nameEdit.getText().toString());
                    }
                } else if (phoneEdit.isFocused()) {
                    phoneEdit.clearFocus();
                    if (phoneValidation.validate()) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(phoneEdit.getWindowToken(), 0);
                        editPhoneofUser(emailEdit.getText().toString(), phoneEdit.getText().toString());
                    }
                }
            }
        });
    }

    private void SetUi() {
        nameEdit.setText(Utility.GetDataFromCache(this, "name", "sample"));
        phoneEdit.setText(Utility.GetDataFromCache(this, "phone", "sample"));
        emailEdit.setText(Utility.GetDataFromCache(this, "email", "ABC@example.com"));
    }

    public void ChangePassword(View view) {
        passwordChangeALert = new Dialog(ProfileActivity.this);
        passwordChangeALert.setTitle("Change Password");
        passwordChangeALert.setContentView(R.layout.changepassworddialog);

        oldPassword = passwordChangeALert.findViewById(R.id.old_password);
        newPassword = passwordChangeALert.findViewById(R.id.new_password);
        confirmNewPassword = passwordChangeALert.findViewById(R.id.confirm_new_password);
        passwordChangeALert.show();
    }

    public void ChangePasswordSubmit(View view) {
        //todo call editPasswordofUser function with arguments
        passwordChangeALert.dismiss();
        String oldPasswordString = oldPassword.getEditableText().toString();
        String newPasswordString = newPassword.getEditableText().toString();
        String confirmNewPasswordString = confirmNewPassword.getEditableText().toString();

        if (oldPasswordString.length() > 5 && newPasswordString.contentEquals(confirmNewPasswordString)) {
            editPasswordofUser(emailEdit.getText().toString(), oldPasswordString, newPasswordString);
        } else {
            Toast.makeText(this, "Check credentials", Toast.LENGTH_LONG).show();
        }
    }

    public void ChangePasswordCancel(View view) {
        passwordChangeALert.dismiss();
    }

    public void Back(View view) {
        finish();
    }

    public void Logout(View view) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("jwt");
        editor.remove("roles");
        editor.putString("isLogged", "false");
        editor.apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void editNameofUser(String email, String name) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("name", name);
        ProgressDialog dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");//todo populate jwt from cache
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<HashMap<String, String>> updateCall = retrofitInterface.updateName(jwt, params);
        updateCall.enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                if (response.body().get("response").contentEquals("success")) {
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    Utility.StoreDataInCache(getApplicationContext(), "name", name);
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Connection Error !! ", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void editPhoneofUser(String email, String phone) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", phone);
        params.put("email", email);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND"); //todo Abhishek: populate jwt from cache
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<HashMap<String, String>> updateCall = retrofitInterface.updatePhone(jwt, params);
        updateCall.enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                if (response.body().get("response").contentEquals("success")) {
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Connection Error !! ", Toast.LENGTH_LONG);

            }
        });


    }

    private void editPasswordofUser(String email, String oldpassword, String newpassword) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("newpassword", newpassword);
        params.put("username", email);
        params.put("oldpassword", oldpassword);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<HashMap<String, String>> updateCall = retrofitInterface.updateProfilePassword(jwt, params);
        updateCall.enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                if (response.body().get("response").contentEquals("success")) {
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    Logout(new View(getApplicationContext()));
                } else {
                    Toast.makeText(getApplicationContext(), "Try Again ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Connection Error !! ", Toast.LENGTH_LONG).show();

            }
        });


    }
}