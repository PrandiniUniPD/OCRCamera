package unipd.se18.ocrcamera;


/**
 * This class compares strings
 * @author Francesco Pham
 */
public interface StringDistance {
    /**
     * Get the distance between two string normalized into a value from 0.0 to 1.0
     * @param str1 First string
     * @param str2 Second string
     * @return Distance between two string normalized into a value from 0.0 to 1.0
     * @author Francesco Pham
     */
    public double getNormalizedDistance(String str1, String str2);

    /**
     * Get the similarity between two string normalized into a value from 0.0 to 1.0
     * @param str1 First string
     * @param str2 Second string
     * @return similarity between two string normalized into a value from 0.0 to 1.0
     * @author Francesco Pham
     */
    public double getNormalizedSimilarity(String str1, String str2);
}
