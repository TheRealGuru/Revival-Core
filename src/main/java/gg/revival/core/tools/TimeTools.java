package gg.revival.core.tools;

import org.apache.commons.lang.math.NumberUtils;

public class TimeTools
{

    public int getTime(String string) {
        int time = 0;

        if (string.contains("m")) {
            String timeStr = strip(string);

            if (NumberUtils.isNumber(timeStr)) {
                time = NumberUtils.toInt(timeStr) * 60;
            }

        }

        else if (string.contains("h")) {
            String timeStr = strip(string);

            if (NumberUtils.isNumber(timeStr)) {
                time = NumberUtils.toInt(timeStr) * 3600;
            }

        }

        else if (string.contains("s")) {
            String timeStr = strip(string);

            if (NumberUtils.isNumber(timeStr)) {
                time = NumberUtils.toInt(timeStr);
            }

        }

        else if (string.contains("d")) {
            String timeStr = strip(string);

            if (NumberUtils.isNumber(timeStr)) {
                time = NumberUtils.toInt(timeStr) * 86400;
            }
        }

        else if (string.contains("w")) {
            String timeStr = strip(string);

            if (NumberUtils.isNumber(timeStr)) {
                time = NumberUtils.toInt(timeStr) * 604800;
            }
        }

        else if (string.contains("y")) {
            String timeStr = strip(string);

            if (NumberUtils.isNumber(timeStr)) {
                time = NumberUtils.toInt(timeStr) * 31536000;
            }
        }

        return time;
    }

    private String strip(String src) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);

            if (Character.isDigit(c)) {
                builder.append(c);
            }
        }

        return builder.toString();
    }

}
