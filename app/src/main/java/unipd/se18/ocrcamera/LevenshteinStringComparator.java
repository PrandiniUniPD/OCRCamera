package unipd.se18.ocrcamera;

import info.debatty.java.stringsimilarity.*;

public class LevenshteinStringComparator extends WeightedLevenshtein{

    public LevenshteinStringComparator(){
        super(
            new CharacterSubstitutionInterface() {
                public double cost(char c1, char c2) {

                    // The cost for substituting 't' and 'r' is considered
                    // smaller as these 2 are located next to each other
                    // on a keyboard
                    if (c1 == 't' && c2 == 'r') {
                        return 0.5;
                    }
                    else if (c1 == 'q' && c2 == 'o') {
                        return 0.5;
                    }
                    else if (c1 == 'I' && c2 == 'l') {
                        return 0.5;
                    }

                    // For most cases, the cost of substituting 2 characters
                    // is 1.0
                    return 1.0;
                }
            });
    }

    public double getNormalizedDistance(String str1, String str2){
        int maxLength = Math.max(str1.length(),str2.length());
        return super.distance(str1, str2)/maxLength;
    }

    public double getNormalizedSimilarity(String str1, String str2){
        return 1.0 - getNormalizedDistance(str1, str2);
    }
}
