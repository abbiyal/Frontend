package medmart.loginmedmart.CommonAdapter;

public class ShopCard {
    int image;
    String shopName;
    String shopDistance;

    public ShopCard(int image, String shopName, String shopDistance) {
        this.image = image;
        this.shopName = shopName;
        this.shopDistance = shopDistance;
    }

    public int getImage() {
        return image;
    }

    public String getShopName() {
        return shopName;
    }

    public String getShopDistance() {
        return shopDistance;
    }
}
