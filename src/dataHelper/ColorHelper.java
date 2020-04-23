package dataHelper;

import java.math.BigInteger;
import java.util.Random;

public abstract class ColorHelper {
    private static final Random rand = new Random();

    public static byte[] getRandomColor() {
        var bytes = new byte[3];
        rand.nextBytes(bytes);
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
