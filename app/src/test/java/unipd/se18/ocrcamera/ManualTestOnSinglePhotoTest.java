package unipd.se18.ocrcamera;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * ManualTestOnSinglePhoto test class
 * @author Giovanni Furlan g2
 */

public class ManualTestOnSinglePhotoTest {

    private String Tag;
    private Method method;

    @Test
    public void findWarningText1() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
    {
        //Getting the permission to use private method findWarning
        method = ManualTestOnSinglePhoto.class.getDeclaredMethod("findWarning", String.class);
        method.setAccessible(true);

        //first test
        String expected = "OCR found no text in this photo";
        String text="";
        int length=10;
        String actual = (String) method.invoke(new ManualTestOnSinglePhoto() ,text, length);
        assertEquals(expected, actual);

        //second test
        expected = "Less then 20 chars found by OCR";
        text="example";
        length=30;
        actual = (String) method.invoke(new ManualTestOnSinglePhoto() ,text, length);
        assertEquals(expected, actual);

        //third test
        expected = "";
        text="example of string";
        length=14;
        actual = (String) method.invoke(new ManualTestOnSinglePhoto() ,text, length);
        assertEquals(expected, actual);
    }

    @Test
    public void compareStringsText() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
    {
        //Getting the permission to use private method compareStrings
        method = ManualTestOnSinglePhoto.class.getDeclaredMethod("compareStrings", String.class, String.class);
        method.setAccessible(true);

        //first test
        String string1 = "example of string";
        String string2="example of string";
        int expected=100;

        //Used int because assertEquals for double is deprecated
        int actual = (int) method.invoke(new ManualTestOnSinglePhoto(),string1,string2);
        assertEquals(expected, actual);

        //second test
        string1="example of string";
        string2="";
        expected=0;

        //Used int because assertEquals for double is deprecated
        actual= (int) method.invoke(new ManualTestOnSinglePhoto(), string1,string2);
        assertEquals(expected, actual);


    }
}
