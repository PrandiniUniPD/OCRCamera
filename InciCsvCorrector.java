import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class for the correction of the csv file containing the INCI ingredients found at
 * "http://ec.europa.eu/growth/tools-databases/cosing/pdf/COSING_Ingredients-Fragrance%20Inventory_v2.csv".
 * There are reoccurring formatting errors in the downloaded file that make it unusable (line endings inside the ingredient description),
 * this class fixes those errors by writing in a new file the fixed text, the paths to the source and end files will be given by the user.
 * The INCI database is updated quite often so this class can be used every time there is an update to get a usable file.
 *
 * This is the structure that every line in the file should have:
 * COSING Ref No,"INCI name","INN name","Ph. Eur. Name",CAS No,EC No,"Chem/IUPAC Name / Description","Restriction","Function",Update Date
 * @author Nicolò Cervo (g3)
 */
public class InciCsvCorrector {

    private static final int DATE_LENGTH = 10;  // dd/mm/yyyy
    private static final int MINIMUM_VALID_LENGTH = 15; // of a line
    private static final int SOURCE_FILE = 0; // of a line
    private static final int END_FILE = 1; // of a line

    public static void main(String[] args)
    {
        Path[] paths = getFilesPaths();
        File source = paths[SOURCE_FILE].toFile();
        File corrected = paths[END_FILE].toFile();
        boolean needsCorrection = false;
        ArrayList<Integer> invLines = null;

        try {
            invLines = findInvalidLines(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!(invLines.isEmpty())) {   // if there are invalid lines print their number
            needsCorrection = true;
            System.out.print("invalid lines : ");
            while(!invLines.isEmpty()){
                System.out.print(invLines.remove(0) + " ");
            }
            System.out.println();
        }
        if(needsCorrection){
            try {
                correctInterruptedLines(source, corrected);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("the file has been corrected");
        }else{
            System.out.println("the file does not need correction");
        }
    }

    /**
     * finds the invalid lines in the source file and prints them
     * @param source source file
     * @return an ArrayList of integers with the line numbers in the source file
     * @throws IOException when the file does not exist
     */
    private static ArrayList<Integer> findInvalidLines(File source) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(source));
        String line;
        int lineNum=0;
        ArrayList<Integer> invalidLines = new ArrayList<>();

        while (((line = br.readLine()) != null) ) {
            lineNum++;
            if(line.length()>MINIMUM_VALID_LENGTH) {
                boolean currentLineHasValidStart = (isNumeric(line.substring(0, 5)) && (line.charAt(5) == ','));        //valid start of line (12345,)
                boolean currentLineHasValidEnd = isDate(line.substring(line.length() - DATE_LENGTH));              //valid end of line (dd/mm/yyyy)
                if (!(currentLineHasValidStart && currentLineHasValidEnd)) {               //adds to list the invalid lines
                    invalidLines.add(lineNum);
                }
            }else{
                invalidLines.add(lineNum);
            }
        }
        return invalidLines;
    }

    /**
     * eliminates line endings ("\r\n") that break the ingredient entries,
     * writing the reassembled lines in the result file.
     * @param source source file
     * @param corrected result file
     */
    private static void correctInterruptedLines(File source,File corrected) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(source));
        BufferedWriter bw = new BufferedWriter(new FileWriter(corrected));
        String line;
        boolean currentLineHasValidStart;
        boolean currentLineHasValidEnd;
        boolean lineCompleted = true;

        while((line = br.readLine())!=null){
            if(line.length()>MINIMUM_VALID_LENGTH) {

                currentLineHasValidStart = (isNumeric(line.substring(0, 5)) && (line.charAt(5) == ','));//valid start of line (12345,)
                currentLineHasValidEnd = isDate(line.substring(line.length() - DATE_LENGTH)); //valid end of line (dd/mm/yyyy)

                if (currentLineHasValidStart && currentLineHasValidEnd) {               //line is complete
                    bw.write(line + "\r\n");          //write and go to the next
                } else if (currentLineHasValidStart) {         //line has a valid start but is incomplete
                    bw.write(line);                 //write without "\r\n"
                    lineCompleted = false;
                } else if (isDate(line.substring(line.length() - DATE_LENGTH)) && !lineCompleted) {   //valid end of line (ends with a date) and last line is incomplete (does not end with a date)
                    bw.write(line + "\r\n");                                                 //write and end line
                    lineCompleted = true;
                } else if (!lineCompleted) {          //last line incomplete
                    bw.write(line);                 //write without ending line
                }
            }
        }
        bw.close();
    }

    /**
     * gets the file paths from console input
     * @return Path[2] containing the paths of the source file [0] and the new corrected file [1]
     */
    private static Path[] getFilesPaths(){

        //array that will contain the two paths
        Path[] paths = new Path[2];
        Scanner sc = new Scanner(System.in);
        boolean correctPath;

        do {
            correctPath = true;
            System.out.print("Source file path (C:\\Users\\...) :");
            try {
                //convert the user input to Path
                paths[SOURCE_FILE] = Paths.get(URI.create(sc.nextLine()));
            } catch (IllegalArgumentException e) {
                // if the input is not a valid path ask for it again until it is valid.
                System.out.print("invalid path");
                correctPath = false;
            }
        }while(!correctPath);

        do {
            correctPath = true;
            System.out.print("Source file path (C:\\Users\\...) :");
            try {
                //convert the user input to Path
                paths[SOURCE_FILE] = Paths.get(URI.create(sc.nextLine()));
            } catch (IllegalArgumentException e) {
                // if the input is not a valid path ask for it again until it is valid.
                System.out.print("Result file path :");
                correctPath = false;
            }
        }while(!correctPath);

        return paths;
    }

    /**
     *
     * @param str String to be checked
     * @return boolean true if str is a string of only digits, false otherwise
     */
    private static boolean isNumeric(String str)
    {
        for (char c : str.toCharArray())
        {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    /**
     * rudimental check to see if the String date is indeed a date (by checking if it is composed of digits and "/")
     * @param date String to be checked
     * @return true if date is a string of digits and "/"
     */
    private static boolean isDate(String date)   // not very precise but enough for this class
    {
        for (char c : date.toCharArray())
        {
            if (!(Character.isDigit(c) || c=='/')) return false;
        }
        return true;
    }
}
