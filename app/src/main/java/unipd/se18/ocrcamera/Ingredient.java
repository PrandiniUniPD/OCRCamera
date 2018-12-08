package unipd.se18.ocrcamera;

import com.opencsv.bean.CsvBindByPosition;

public class Ingredient {
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

    public Ingredient(String cosingRefNo, String inciName, String innName, String phEurName,
                      String casNo, String ecNo, String description, String restriction,
                      String function, String updateDate) {
        this.cosingRefNo = cosingRefNo;
        this.inciName = inciName;
        this.innName = innName;
        this.phEurName = phEurName;
        this.casNo = casNo;
        this.ecNo = ecNo;
        this.description = description;
        this.restriction = restriction;
        this.function = function;
        this.updateDate = updateDate;
    }

    @CsvBindByPosition(position = 0)
    private String cosingRefNo;

    @CsvBindByPosition(position = 1)
    private String inciName;

    @CsvBindByPosition(position = 2)
    private String innName;

    @CsvBindByPosition(position = 3)
    private String phEurName;

    @CsvBindByPosition(position = 4)
    private String casNo;

    @CsvBindByPosition(position = 5)
    private String ecNo;

    @CsvBindByPosition(position = 6)
    private String description;

    @CsvBindByPosition(position = 7)
    private String restriction;

    @CsvBindByPosition(position = 8)
    private String function;

    @CsvBindByPosition(position = 9)
    private String updateDate;


    private String foundText;
}
