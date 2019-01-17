package unipd.se18.ocrcamera.performancetester.testers;

/**
 * Exception used to signal an invalid directory that may be empty or no do not have any
 * acceptable test.
 * @author Luca Moroldo
 */
public class TestDirectoryException extends Exception {
    TestDirectoryException(String errorMessage) {
        super(errorMessage);
    }
}