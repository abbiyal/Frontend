package medmart.loginmedmart.HomeActivity.HelperClasses;

public class CategoryCard {
    int image;
    String name;

    public CategoryCard(int image, String name) {
        this.image = image;
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }
}
