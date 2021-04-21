package medmart.loginmedmart.UtilityClasses;

import java.util.HashMap;

import medmart.loginmedmart.LoginSignUpActivites.Jwt;
import medmart.loginmedmart.LoginSignUpActivites.LoginCredentials;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {
    @POST("/api/login")
    Call<Jwt> getAccessToken(@Body LoginCredentials login);

    @POST("/api/signup")
    Call<HashMap<String,String>> addUser(@Body HashMap<String, String> user);

    @POST("/api/forgot/sendtoken")
    Call<HashMap<String,String>> sendToken(@Body HashMap<String,String> email);

    @POST("/api/forgot/verifytoken")
    Call<HashMap<String,String>> verifyOtp(@Body HashMap<String,String> otp);

    @POST("/api/forgot/updatepassword")
    Call<HashMap<String,String>> updatePassword(@Body HashMap<String,String> password);


}
