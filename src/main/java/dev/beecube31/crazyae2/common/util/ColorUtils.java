package dev.beecube31.crazyae2.common.util;

public class ColorUtils {
    public static boolean isCharHex(Character ch) {
        return Character.isDigit(ch)
                || String.valueOf(ch).equalsIgnoreCase("A")
                || String.valueOf(ch).equalsIgnoreCase("B")
                || String.valueOf(ch).equalsIgnoreCase("C")
                || String.valueOf(ch).equalsIgnoreCase("D")
                || String.valueOf(ch).equalsIgnoreCase("E")
                || String.valueOf(ch).equalsIgnoreCase("F");

    }

    public static int[] getRGBFromHex(String hexString) {
        if (hexString.startsWith("#")) {
            hexString = hexString.substring(1);
        }

        if (hexString.length() != 6) {
            return null;
        }

        try {
            int r = Integer.parseInt(hexString.substring(0, 2), 16);
            int g = Integer.parseInt(hexString.substring(2, 4), 16);
            int b = Integer.parseInt(hexString.substring(4, 6), 16);

            return new int[]{r, g, b};
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String getHexFromRGB(int r, int g, int b) {
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
            return null;
        }

        return String.format("#%02X%02X%02X", r, g, b);
    }
}
