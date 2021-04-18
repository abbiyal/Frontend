package medmart.loginmedmart;

import com.google.gson.annotations.SerializedName;

public class Jwt {

    @SerializedName("jwt")
    private String jwt;

    @SerializedName("roles")
    private String roles;

    public Jwt(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public Jwt(String jwt, String roles) {
        this.jwt = jwt;
        this.roles = roles;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    @Override
    public String toString() {
        return "Jwt{" +
                "jwt='" + jwt + '\'' +
                ", roles='" + roles + '\'' +
                '}';
    }
}
