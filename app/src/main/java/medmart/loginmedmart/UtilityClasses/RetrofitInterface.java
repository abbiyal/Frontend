package medmart.loginmedmart.UtilityClasses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import medmart.loginmedmart.LoginSignUpActivites.Jwt;
import medmart.loginmedmart.LoginSignUpActivites.LoginCredentials;
import medmart.loginmedmart.SearchActivity.HelperClasses.SearchCard;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

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

    @GET("/med-inventory/search")
    Call<List<ProductCatalogue>> getSearchResults(@Header("Authorization") String token, @QueryMap Map<String,String> params);

    @GET("/api/updateName")
    Call<HashMap<String,String>> updateName(@Header("Authorization") String token, @QueryMap Map<String,String> params);

    @GET("/api/updatePhone")
    Call<HashMap<String,String>> updatePhone(@Header("Authorization") String token, @QueryMap Map<String,String> params);


}
