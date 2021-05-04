package medmart.loginmedmart.CommonAdapter;

public class SearchCard {
    int image;
    String medicineName;
    String medicineCompany;
    String medicineSize;
    String productId;

    public SearchCard(int image, String medicineName, String medicineCompany, String medicineSize, String productId) {
        this.image = image;
        this.medicineName = medicineName;
        this.medicineCompany = medicineCompany;
        this.medicineSize = medicineSize;
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getImage() {
        return image;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public String getMedicineCompany() {
        return medicineCompany;
    }

    public String getMedicineSize() {
        return medicineSize;
    }

    @Override
    public String toString() {
        return "SearchCard{" +
                "image=" + image +
                ", medicineName='" + medicineName + '\'' +
                ", medicineCompany='" + medicineCompany + '\'' +
                ", medicineSize='" + medicineSize + '\'' +
                ", productId='" + productId + '\'' +
                '}';
    }
}