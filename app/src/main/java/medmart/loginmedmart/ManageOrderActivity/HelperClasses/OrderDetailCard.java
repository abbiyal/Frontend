package medmart.loginmedmart.ManageOrderActivity.HelperClasses;

import java.text.DecimalFormat;

public class OrderDetailCard {

    String medicineName;
    String price;
    String quantity;
    String totalValue;
    String productId;

    public OrderDetailCard(String medicineName, String price, String quantity, String productId) {
        this.medicineName = medicineName;
        this.price = price;;
        this.quantity = quantity + " X ";
        this.totalValue = new DecimalFormat("##.00").format(Integer.parseInt(quantity) * Double.parseDouble(price));
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(String totalValue) {
        this.totalValue = totalValue;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String  price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
