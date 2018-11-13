package unipd.se18.ocrcamera;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that contains all the information from the analysis of a photo
 * @author Leonardo Rossi
 */
public class TestEntry
{
    private String photoName;
    private double confidence;
    private List<String> tags;
    private List<String> ingredients;
    private String notes;

    /**
     * Defines an object of TestEntry type
     * @param photoName The analysed photo's name
     * @param confidence The percentage of recognized words
     * @param tags The photo's tags
     * @param ingredients The photo's ingredients
     * @param notes An additional text with extra information
     */
    TestEntry(String photoName, double confidence, ArrayList<String> tags, ArrayList<String> ingredients, String notes)
    {
        this.photoName = photoName;
        this.confidence = confidence;
        this.tags = tags;
        this.ingredients = ingredients;
        this.notes = notes;
    }

    TestEntry(String photoName, double confidence)
    {
        this(photoName, confidence, new ArrayList<String>(), new ArrayList<String>(), "");
    }

    /**
     * |||||||||||||||||||||||
     * ||   Getter methods  ||
     * |||||||||||||||||||||||
     */

    /**
     * Returns the photo's name
     * @return The photo's name
     */
    public String getPhotoName() { return photoName; }

    /**
     * Returns the percentage of the recognized words
     * @return The percentage of the recognized words
     */
    public double getConfidence() { return confidence; }

    /**
     * Returns the list of the photo's tags
     * @return The list of the photo's tags
     */
    public String[] getTags(){
        return tags.toArray(new String[0]);
        /*
        Object[] arr = tags.toArray();

        String[] stringArr = new String[arr.length];
        for(int i = 0; i < arr.length; i++)
            stringArr[i] = (String) arr[i];

        return stringArr;
        */
    }

    /**
     * Returns the list of photo's ingredients
     * @return The photo's ingredients
     */
    public String[] getIngredients(){ return ingredients.toArray(new String[0]); }

    /**
     * Returns the additional text
     * @return The additional text
     */
    public String getNotes() { return notes; }


    /**
     * |||||||||||||||||||||||
     * ||   Setter methods  ||
     * |||||||||||||||||||||||
     */

    /**
     * Sets the notes value to the specified value
     * @param notes The specified value
     * @modifies notes - It assumes the specified value
     */
    public void setNotes(String notes){ this.notes = notes; }

    /**
     * |||||||||||||||||||||||
     * ||   Public methods  ||
     * |||||||||||||||||||||||
     */

    /**
     * Adds the specified tag to the list of tags
     * @param tag The specified tag
     * @modifies tags - The list's size grows of one unit
     */
    public void addTag(String tag){ tags.add(tag); }

    /**
     * Adds the specified list of tags
     * @param newTags The specified list of tags
     * @modifies tags - The list's size grows of newTags.size units
     */
    public void addTags(List<String> newTags)
    {
        for (int i = 0; i < newTags.size(); i++){ tags.add(newTags.get(i)); }
    }

    /**
     * Adds the specified ingredient to the list of ingredients
     * @param ingredient The specified ingredient
     * @modifies ingredients - The list's size grows of one unit
     */
    public void addIngredient(String ingredient){ ingredients.add(ingredient); }

    /**
     * Adds the specified list of ingredients
     * @param newIngredients The specified list of ingredients
     * @modifies ingredients - The list's size grows of newIngredients.size units
     */
    public void addIngredients(List<String> newIngredients)
    {
        for (int i = 0; i < newIngredients.size(); i++){ ingredients.add(newIngredients.get(i)); }
    }
}
