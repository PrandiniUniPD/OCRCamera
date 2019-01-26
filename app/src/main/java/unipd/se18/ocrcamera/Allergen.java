package unipd.se18.ocrcamera;

/**
 * Class that contains the info about a certain ingredient that could cause allergic reactions
 */
class Allergen {

    private String[] inciNames;

    private String commonName;

    Allergen() {
        commonName = "";
    }

    //setters

    void setCommonName(String commonNames) {
        this.commonName = commonNames;
    }

    void setInciNames(String[] inciName) {
        this.inciNames = inciName;
    }

    //getters

    public String getCommonName() {
        return commonName;
    }

    public String[] getInciNames() {
        return inciNames;
    }

    @Override
    public String toString() {

        String str = commonName;
        for (String s :inciNames) {
            str = str + ", " + s + ", ";
        }
        return (str);
    }

    public String inciNamesString() {

        String namesString = "";
        boolean first = true;

        for (String name : inciNames) {
            if(first) {       // On the first iteration don't put the comma
                namesString = name;
                first = false;
            }else {
                namesString = namesString + ", " + name;
            }
        }

        return namesString;
    }
    /**   !!HELP FROM STEFANO ROMANELLO!!
     * This method is used to check if two Allergens are the same (used in the "contains" method
     * in AllergensListAdapter
     * @param object to be compared to the Allergen
     * @return isEqual ,true if the objects are found to be equal, false if not
     * @author Stefano Romanello
     */
    @Override
    public boolean equals(Object object){
        boolean isEqual= false;

        if(object != null && object instanceof Allergen){
            isEqual = this.commonName.equals((((Allergen) object).commonName));
        }
        return isEqual;
    }
}
