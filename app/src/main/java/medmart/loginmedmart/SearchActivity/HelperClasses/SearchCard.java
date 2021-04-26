package medmart.loginmedmart.SearchActivity.HelperClasses;

public class SearchCard {
    int image;
    String medicineName;
    String medicineCompany;
    String medicineSize;

    public SearchCard(int image, String medicineName, String medicineCompany, String medicineSize) {
        this.image = image;
        this.medicineName = medicineName;
        this.medicineCompany = medicineCompany;
        this.medicineSize = medicineSize;
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
}