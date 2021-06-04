package medmart.loginmedmart.ManageOrderActivity.HelperClasses;

public class PastOrderCard {
    String orderId;
    Double price;
    String shopName;
    String shopAddress;
    String status;
    String dateTime;
    String deliveryAddress;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }



    public PastOrderCard(String orderId, Double price, String shopName, String dateTime, String status,
                         String shopAddress, String deliveryAddress) {
        this.orderId = orderId;
        this.price = price;
        this.shopName = shopName;
        this.dateTime = dateTime;
        this.status = status;
        this.shopAddress = shopAddress;
        this.deliveryAddress = deliveryAddress;
    }

    @Override
    public String toString() {
        return "PastOrderCard{" +
                "orderId='" + orderId + '\'' +
                ", price=" + price +
                ", shopName='" + shopName + '\'' +
                '}';
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
