package unipd.se18.ocrcamera.performancetester.testers;

/**
 * Exception used to signal an invalid directory that may be empty or no do not have any
 * acceptable test.
 * @author Luca Moroldo
 */
public class TestDirectoryException extends Exception {
    /**
     * Exception relative to the directory that should be contained the test pics
     * @param errorMessage The error message of the specif error founded
     */
    TestDirectoryException(String errorMessage) {
        super(errorMessage);
    }
}