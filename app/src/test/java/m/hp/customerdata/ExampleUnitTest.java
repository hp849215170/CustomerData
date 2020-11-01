package m.hp.customerdata;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void textRegex() {
        String str = "Z1123AT89AVC777";

        Pattern p = Pattern.compile("^[A-Z][0-9]");
        Matcher m = p.matcher(str);
        boolean b = m.find();
        System.out.println(b);
    }
}