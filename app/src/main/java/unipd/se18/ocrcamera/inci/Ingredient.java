package unipd.se18.ocrcamera.inci;

/**
 * Ingredient class which contains the information of the ingredient taken from inci db.
 * @author Francesco Pham
 */
public class Ingredient implements Comparable<String> {

    //PRIVATE PROPERTIES
    private String cosingRefNo;
    private String inciName;
    private String description;
    private String function;

    private String strippedInciName;
    private int startPositionFound;
    private int endPositionFound;

    public Ingredient() {
        this.cosingRefNo = "";
        this.inciName = "";
        this.description = "";
        this.function = "";
        startPositionFound = -1;
        endPositionFound = -1;
    }

    @Override
    public int compareTo(String o) {
        return getInciName().compareToIgnoreCase(o);
    }

    //GETTERS
    public String getCosingRefNo() {
        return cosingRefNo;
    }

    public String getInciName() {
        return inciName;
    }

    public String getDescription() {
        return description;
    }

    public String getFunction() {
        return function;
    }

    public int getStartPositionFound() {
        return startPositionFound;
    }

    public int getEndPositionFound() {
        return endPositionFound;
    }

    String getStrippedInciName() {
        return strippedInciName;
    }

    //SETTERS
    void setCosingRefNo(String cosingRefNo) {
        this.cosingRefNo = cosingRefNo;
    }

    void setInciName(String inciName) {
        this.strippedInciName = stripString(inciName);
        this.inciName = inciName;
    }

    void setDescription(String description) {
        this.description = description;
    }

    void setFunction(String function) {
        this.function = function;
    }

    void setStartPositionFound(int startPositionFound) {
        this.startPositionFound = startPositionFound;
    }

    void setEndPositionFound(int endPositionFound) {
        this.endPositionFound = endPositionFound;
    }

    //this method removes all non alphanumeric characters
    private String stripString(String name){
        return name.replaceAll("[^A-Za-z0-9]", "");
    }
}
