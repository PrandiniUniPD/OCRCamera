package unipd.se18.ocrcamera;

/**
 * Ingredient class which contains the information of the ingredient taken from inci db
 * @author Francesco Pham
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


    Ingredient() {
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

    //SETTERS
    public void setCosingRefNo(String cosingRefNo) {
        this.cosingRefNo = cosingRefNo;
    }

    public void setInciName(String inciName) {
        //remove trailing and leading spaces
        inciName = inciName.trim();

        //ignore whitespaces before and after slash (this increases loading time by 0.7s,
        //we should rewrite all csv instead of doing this here)
        inciName = inciName.replaceAll(" */ *", "/");
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

    @Override
    public int compareTo(String o) {
        return getInciName().compareToIgnoreCase(o);
    }
}
