package medmart.loginmedmart.UtilityClasses;

public class ProductCatalogue {
    private String productId;
    private String productName;
    private String companyName;
    private String doseStrength;
    private String size;
    private String type;

    public ProductCatalogue() {
    }

    public ProductCatalogue(String productId, String productName, String companyName, String doseStrength,
                            String size, String type) {
        this.productId = productId;
        this.productName = productName;
        this.companyName = companyName;
        this.doseStrength = doseStrength;
        this.size = size;
        this.type = type;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDoseStrength() {
        return doseStrength;
    }

    public void setDoseStrength(String doseStrength) {
        this.doseStrength = doseStrength;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
