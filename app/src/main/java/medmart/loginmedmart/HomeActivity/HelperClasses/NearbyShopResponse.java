package medmart.loginmedmart.HomeActivity.HelperClasses;

public class NearbyShopResponse {

    String shopName;
    Long shopId;
    String distance;
    String price;
    String address;


    public NearbyShopResponse() {
        super();
    }


    public NearbyShopResponse(String shopName, Long shopId, String distance, String price, String address) {

        this.shopName = shopName;
        this.shopId = shopId;
        this.distance = distance;
        this.price = price;
        this.address = address;
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
        this.distance = distance;
    }


    public String getPrice() {
        return price;
    }


    public void setPrice(String price) {
        this.price = price;
    }


    public String getAddress() {
        return address;
    }


    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "NearbyShopResponse{" +
                "shopName='" + shopName + '\'' +
                ", shopId=" + shopId +
                ", distance='" + distance + '\'' +
                ", price='" + price + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
