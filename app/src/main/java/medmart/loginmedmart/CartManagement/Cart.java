package medmart.loginmedmart.CartManagement;

import java.util.HashMap;

public class Cart {
    public class CartItem {
        private int quantity;
        private Double price;

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }
    }

    private String cartId;
    private long shopId;
    private int totalItems;
    private double totalValue;
    private static Cart cartInstance;

    public static Cart GetInstance() {
        if (cartInstance == null) {
            cartInstance = new Cart();
        }

        return cartInstance;
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
        this.totalValue = totalValue;
    }

    public HashMap<String, CartItem> getListOfItems() {
        return listOfItems;
    }

    public void setListOfItems(HashMap<String, CartItem> listOfItems) {
        this.listOfItems = listOfItems;
    }

    private HashMap<String, CartItem> listOfItems;
}
