package medmart.loginmedmart.CartManagement;

import java.text.DecimalFormat;
import java.util.HashMap;

public class CartService {
    private String cartId;
    private long shopId;
    private int totalItems;
    private double totalValue;

    public boolean isCartLoaded() {
        return isCartLoaded;
    }

    public void setCartLoaded(boolean cartLoaded) {
        isCartLoaded = cartLoaded;
    }

    private boolean isCartLoaded;

    @Override
    public String toString() {
        return "Cart{" +
                "cartId='" + cartId + '\'' +
                ", shopId=" + shopId +
                ", totalItems=" + totalItems +
                ", totalValue=" + totalValue +
                ", listOfItems=" + listOfItems +
                '}';
    }

    private HashMap<String, CartItem> listOfItems;

    private static CartService cartServiceInstance;

    public void ClearCart() {
        totalItems = 0;
        totalValue = 0;
        listOfItems.clear();
    }

    public CartService() {
        isCartLoaded = false;
        listOfItems = new HashMap<String, CartItem>();
    }

    public static CartService GetInstance() {
        if (cartServiceInstance == null) {

            cartServiceInstance = new CartService();
        }

        return cartServiceInstance;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        DecimalFormat decimalFormat = new DecimalFormat("##.00");
        this.totalValue = Double.parseDouble(decimalFormat.format(totalValue));
    }

    public HashMap<String, CartItem> getListOfItems() {
        return listOfItems;
    }

    public void setListOfItems(HashMap<String, CartItem> listOfItems) {
        this.listOfItems = listOfItems;
    }

}
