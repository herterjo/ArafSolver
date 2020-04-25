package dataHelper;

import java.math.BigInteger;
import java.util.Random;

public abstract class ColorHelper {
    private static final Random rand = new Random();

    public static byte[] getRandomColor() {
        var bytes = new byte[]{0,0,0};
        int c;
        do {
            rand.nextBytes(bytes);
            c = colorToInt(bytes);
        }while(intToColor(c).length != 3);
        //checking, because there coukld arise a problem
        return bytes;
    }

    public static int colorToInt(byte[] bytes) {
        return new BigInteger(bytes).intValue();
    }

    public static int getRandomIntColor() {
        var colorB = getRandomColor();
        return colorToInt(colorB);
    }

    public static byte[] intToColor(int color) {
        return BigInteger.valueOf(color).toByteArray();
    }
}
