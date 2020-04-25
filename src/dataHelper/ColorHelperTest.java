package dataHelper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColorHelperTest {

    @Test
    void getRandomColor() {
        var c1 = ColorHelper.getRandomColor();
        var c2 = ColorHelper.getRandomColor();
        assertEquals(3, c1.length);
        assertEquals(3, c2.length);
        for (var i = 0; i < c1.length; i++) {
            assertNotEquals(c1[i], c2[i]);
        }
    }

    @Test
    void colorToIntAndBack() {
        var c = new byte[]{1, 2, 3};
        var i = ColorHelper.colorToInt(c);
        assertArrayEquals(c, ColorHelper.intToColor(i));
    }
}