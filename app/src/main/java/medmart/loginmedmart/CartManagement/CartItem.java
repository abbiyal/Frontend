package medmart.loginmedmart.CartManagement;

public class CartItem {
    private int quantity;
    private Double price;
    private String productId;

    public CartItem() {
    }

    public CartItem(int quantity, Double price, String productId) {
        this.quantity = quantity;
        this.price = price;
        this.productId = productId;
    }

    public CartItem(int quantity, Double price) {
        this.quantity = quantity;
        this.price = price;
    }

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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "quantity=" + quantity +
                ", price=" + price +
                ", productId='" + productId + '\'' +
                '}';
    }
}
