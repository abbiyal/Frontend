package medmart.loginmedmart;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {
    @POST("/api/login")
    Call<Jwt> getAccessToken(@Body LoginCredentials login);
}
