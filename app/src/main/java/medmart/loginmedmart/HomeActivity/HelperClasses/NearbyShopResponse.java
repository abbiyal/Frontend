package medmart.loginmedmart.HomeActivity.HelperClasses;

public class NearbyShopResponse {

    private String shopName;
    private Long shopId;
    private String distance;

    public NearbyShopResponse() {
    }

    public NearbyShopResponse(String shopName, Long shopId, String distance) {
        this.shopName = shopName;
        this.shopId = shopId;
        distance = distance;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        distance = distance;
    }

    @Override
    public String toString() {
        return "NearbyShopResponse{" +
                "shopName='" + shopName + '\'' +
                ", shopId=" + shopId +
                ", Distance=" + distance +
                '}';
    }
}
