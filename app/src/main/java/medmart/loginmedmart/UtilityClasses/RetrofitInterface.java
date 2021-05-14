package medmart.loginmedmart.UtilityClasses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import medmart.loginmedmart.HomeActivity.HelperClasses.NearbyShopResponse;
import medmart.loginmedmart.LoginSignUpActivites.Jwt;
import medmart.loginmedmart.LoginSignUpActivites.LoginCredentials;
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
    Call<HashMap<String, String>> addUser(@Body HashMap<String, String> user);

    @POST("/api/forgot/sendtoken")
    Call<HashMap<String, String>> sendToken(@Body HashMap<String, String> email);

    @POST("/api/forgot/verifytoken")
    Call<HashMap<String, String>> verifyOtp(@Body HashMap<String, String> otp);

    @POST("/api/forgot/updatepassword")
    Call<HashMap<String, String>> updatePassword(@Body HashMap<String, String> password);

    @GET("/med-inventory/search")
    Call<List<ProductCatalogue>> getSearchResults(@Header("Authorization") String token, @QueryMap Map<String, String> params);

    @POST("/api/profile/updateName")
    Call<HashMap<String, String>> updateName(@Header("Authorization") String token, @Body Map<String, String> params);

    @POST("/api/profile/updatePhone")
    Call<HashMap<String, String>> updatePhone(@Header("Authorization") String token, @Body Map<String, String> params);

    @GET("/med-inventory/search/nearbyShops")
    Call<List<NearbyShopResponse>> findNearbyShops(@Header("Authorization") String token, @QueryMap Map<String, String> params);

    @POST("/med-inventory/search/shopshavingproducts")
    Call<List<NearbyShopResponse>> getShopsHavingProducts(@Header("Authorization") String token, @Body HashMap<String, String> params);

    @GET("/med-inventory/search/category")
    Call<List<ProductCatalogue>> getProductsOfCategory(@Header("Authorization") String token, @QueryMap Map<String, String> params);

    @POST("/med-inventory/search/withincategory")
    Call<List<ProductCatalogue>> getProductsWithinCategory(@Header("Authorization") String token, @Body HashMap<String, String> params);

    @POST("/api/signup/invalid/delete")
    Call<HashMap<String, String>> DeleteUser(@Body HashMap<String, String> params);

    @GET("/med-inventory/search/products_in_shop")
    Call<List<HashMap<String, String>>> findShopProducts(@Header("Authorization") String token, @QueryMap HashMap<String, String> params);

    @POST("/api/profile/updatePassword")
    Call<HashMap<String, String>> updateProfilePassword(@Header("Authorization") String token, @Body Map<String, String> params);

    @GET("/cart-service/carts/getcart")
    Call<HashMap<String,Object>> getUserCart(@Header("Authorization") String token,@QueryMap HashMap<String, String> params);

    @POST("/cart-service/carts/addIteminCart")
    Call<HashMap<String, String>> addItemInCart(@Header("Authorization") String token, @Body Map<String, String> params);

    @GET("/cart-service/carts/emptyCart")
    Call<HashMap<String,String>> emptyCart(@Header("Authorization") String token,@QueryMap HashMap<String, String> params);

    @POST("/cart-service/carts/updateItemQuantity")
    Call<HashMap<String, String>> updateItemInCart(@Header("Authorization") String token, @Body Map<String, String> params);

}
