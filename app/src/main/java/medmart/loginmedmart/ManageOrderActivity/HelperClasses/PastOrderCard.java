package medmart.loginmedmart.ManageOrderActivity.HelperClasses;

public class PastOrderCard {
    String orderId;
    Double price;
    String shopName;
    String status;
    String dateTime;

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



    public PastOrderCard(String orderId, Double price, String shopName, String dateTime, String status) {
        this.orderId = orderId;
        this.price = price;
        this.shopName = shopName;
        this.dateTime = dateTime;
        this.status = status;
    }

    @Override
    public String toString() {
        return "PastOrderCard{" +
                "orderId='" + orderId + '\'' +
                ", price=" + price +
                ", shopName='" + shopName + '\'' +
                '}';
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
