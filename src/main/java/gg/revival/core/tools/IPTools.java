package gg.revival.core.tools;

public class IPTools
{
    /**
     * Returns a int version of an IP address
     * @param ip The IP to be converted
     * @return The converted IP to Integer
     */
    public static int ipStringToInteger(String ip) {
        int value = 0;
        final String[] parts = ip.split ("\\.");

        for (final String part : parts)
            value = (value << 8) + Integer.parseInt (part);

        return value;
    }

    /**
     * Returns a String version of an IP, reverting it to how it is normally viewed
     * @param ip The IP in int form
     * @return The converted IP to String
     */
    public static String ipIntegerToString(int ip) {
        final String[] parts2 = new String[4];

        for (int i = 0; i < 4; i++) {
            parts2[3 - i] = Integer.toString (ip & 0xff);
            ip >>= 8;
        }

        return parts2[0] + '.' + parts2[1] + '.' + parts2[2] + '.' + parts2[3];
    }
}
