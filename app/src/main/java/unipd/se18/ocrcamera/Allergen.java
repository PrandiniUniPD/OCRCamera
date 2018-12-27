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

        for (String name : inciNames) {
            namesString = namesString + ", " + name;
        }

        return namesString;
    }
}
