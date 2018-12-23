package unipd.se18.ocrcamera;

/**
 * Class that contains the info about a certain ingredient that could cause allergic reactions
 */
class Allergen {

    private String[] inciNames;

    private String commonName;

    private boolean selected;  //keeps track if this allergen was selected by the user

    Allergen() {
        commonName = "";
        selected = false;
    }

    //setters

    void setCommonName(String commonNames) {
        this.commonName = commonNames;
    }

    void setInciNames(String[] inciName) {
        this.inciNames = inciName;
    }

    void setSelection(boolean selection) { this.selected = selection; }

    //getters

    public String getCommonName() {
        return commonName;
    }

    public String[] getInciNames() {
        return inciNames;
    }

    public boolean getSelection() { return selected; }

    @Override
    public String toString() {

        String str = commonName;
        for (String s :inciNames) {
            str = str + ", " + s + ", " + Boolean.toString(selected);
        }
        return (str);
    }
}
