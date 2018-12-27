package unipd.se18.ocrcamera;

/**
 * Class that contains the info about a certain ingredient that could cause allergic reactions
 */
class Allergen {

    private String[] inciNames;

    private String commonName;

    private boolean selected;  //keeps track if this allergen was selected by the user

    //standard constructor, sets default empty parameters
    Allergen() {
        commonName = "";
        selected = false;
    }

    //constructor that sets name and selection parameters
    Allergen(String name, Boolean sel){
        commonName= name;
        selected= sel;
    }


    //setters methods for allergen variables

    void setCommonName(String commonNames) {
        this.commonName = commonNames;
    }

    void setInciNames(String[] inciName) {
        this.inciNames = inciName;
    }

    void setSelection(boolean selection) {
        this.selected = selection;
    }

    //getters methods for

    public String getCommonName() {
        return commonName;
    }

    public String[] getInciNames() {
        return inciNames;
    }

    public boolean isSelected() { return selected; }

    @Override
    public String toString() {

        String str = commonName;
        for (String s :inciNames) {
            str = str + ", " + s + ", " + Boolean.toString(selected);
        }
        return (str);
    }
}
