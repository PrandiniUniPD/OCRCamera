package unipd.se18.ocrcamera.inci;

import info.debatty.java.stringsimilarity.CharacterSubstitutionInterface;
import info.debatty.java.stringsimilarity.WeightedLevenshtein;

/**
 * This class compares strings using weighted levenshtein algorithm
 * For more informations about the library we are using:
 * https://github.com/tdebatty/java-string-similarity
 * @author Francesco Pham
 */
public class LevenshteinStringDistance extends WeightedLevenshtein  {

    /**
     * Constructor defines different substitution cost functions based on characters similarity.
     */
    public LevenshteinStringDistance(){
        super(
            new CharacterSubstitutionInterface() {
                public double cost(char c1, char c2) {

                    // The cost for substituting 't' and 'r' is considered smaller
                    if (c1 == 't' && c2 == 'r') {
                        return 0.5;
                    }
                    else if (c1 == 'q' && c2 == 'o') {
                        return 0.5;
                    }
                    else if (c1 == 'I' && c2 == 'l') {
                        return 0.5;
                    }
                    else if (c1 == 'H' && c2 == 'N') {
                        return 0.5;
                    }

                    // For most cases, the cost of substituting 2 characters
                    // is 1.0
                    return 1.0;
                }
            });
    }

    /**
     * Get the distance between two string normalized into a value from 0.0 to 1.0
     * @param str1 First string
     * @param str2 Second string
     * @return Distance between two string normalized into a value from 0.0 to 1.0
     * @author Francesco Pham
     */
    public double getNormalizedDistance(String str1, String str2){
        int maxLength = Math.max(str1.length(),str2.length());
        return super.distance(str1, str2)/maxLength;
    }

    /**
     * Get the similarity between two string normalized into a value from 0.0 to 1.0
     * @param str1 First string
     * @param str2 Second string
     * @return similarity between two string normalized into a value from 0.0 to 1.0
     * @author Francesco Pham
     */
    public double getNormalizedSimilarity(String str1, String str2){
        return 1.0 - getNormalizedDistance(str1, str2);
    }
}
