package unipd.se18.ocrcamera;

import android.graphics.Bitmap;

/**
 * Class that contains a single test instance
 * @author Francesco Pham
 */
public class TestInstance {
    private String ingredients;
    private Bitmap picture;

    public TestInstance(String ingredients, Bitmap picture) {
        this.ingredients = ingredients;
        this.picture = picture;
    }

    public String getIngredients() {
        return ingredients;
    }

    public Bitmap getPicture() {
        return picture;
    }

}
