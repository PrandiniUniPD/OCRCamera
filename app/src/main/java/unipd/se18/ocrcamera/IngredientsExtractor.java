package unipd.se18.ocrcamera;

import java.util.ArrayList;

interface IngredientsExtractor {
    ArrayList<Ingredient> findListIngredients(String text);
}
