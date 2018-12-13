package unipd.se18.ocrcamera;

/**
 * Ingredient class that contained the informations of the ingredient taken from inci db
 */
public class Ingredient implements Comparable<String> {

    //PRIVATE PROPERTIES
    private String cosingRefNo;

    private String inciName;

    private String innName;

    private String phEurName;

    private String casNo;

    private String ecNo;

    private String description;

    private String restriction;

    private String function;

    private String updateDate;

    private String foundText;

    private double ocrTextSimilarity;


    public Ingredient() {
        this.cosingRefNo = "";
        this.inciName = "";
        this.innName = "";
        this.phEurName = "";
        this.casNo = "";
        this.ecNo = "";
        this.description = "";
        this.restriction = "";
        this.function = "";
        this.updateDate = "";
    }

    //GETTERS
    public String getCosingRefNo() {
        return cosingRefNo;
    }

    public String getInciName() {
        return inciName;
    }

    public String getInnName() {
        return innName;
    }

    public String getPhEurName() {
        return phEurName;
    }

    public String getCasNo() {
        return casNo;
    }

    public String getEcNo() {
        return ecNo;
    }

    public String getDescription() {
        return description;
    }

    public String getRestriction() {
        return restriction;
    }

    public String getFunction() {
        return function;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public String getFoundText() {
        return foundText;
    }

    public double getOcrTextSimilarity() {
        return ocrTextSimilarity;
    }

    //SETTERS
    public void setCosingRefNo(String cosingRefNo) {
        this.cosingRefNo = cosingRefNo;
    }

    public void setInciName(String inciName) {
        this.inciName = inciName;
    }

    public void setInnName(String innName) {
        this.innName = innName;
    }

    public void setPhEurName(String phEurName) {
        this.phEurName = phEurName;
    }

    public void setCasNo(String casNo) {
        this.casNo = casNo;
    }

    public void setEcNo(String ecNo) {
        this.ecNo = ecNo;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRestriction(String restriction) {
        this.restriction = restriction;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public void setFoundText(String foundText) {
        this.foundText = foundText;
    }

    public void setOcrTextSimilarity(double ocrTextSimilarity) {
        this.ocrTextSimilarity = ocrTextSimilarity;
    }

    @Override
    public int compareTo(String o) {
        return getInciName().compareToIgnoreCase(o);
    }
}
