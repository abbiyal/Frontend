package medmart.loginmedmart.CommonAdapter;

public class ShopCard {
    int image;
    String shopName;
    String shopDistance;
    String price;
    Long shopId;
    String shopAddress;

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }
    public ShopCard(int image, String shopName, String shopDistance, String price, Long shopId) {
        this.image = image;
        this.shopName = shopName;
        this.shopDistance = shopDistance;
        this.price = price;
        this.shopId = shopId;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopDistance() {
        return shopDistance;
    }

    public void setShopDistance(String shopDistance) {
        this.shopDistance = shopDistance;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
}
