package medmart.loginmedmart;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {
    @POST("/api/login")
    Call<Jwt> getAccessToken(@Body LoginCredentials login);

    @POST("/api/signup")
    Call<String> addUser(@Body HashMap<String, String> user);
}
