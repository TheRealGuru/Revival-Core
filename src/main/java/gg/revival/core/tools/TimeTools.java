package gg.revival.core.tools;

import org.apache.commons.lang.math.NumberUtils;

import java.util.concurrent.TimeUnit;

public class TimeTools {

    /**
     * Returns a formatted uptime based on given long
     * @param uptime
     * @return
     */
    public String formatIntoUptime(long uptime) {
        long days = TimeUnit.MILLISECONDS
                .toDays(uptime);
        uptime -= TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS
                .toHours(uptime);
        uptime -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS
                .toMinutes(uptime);
        uptime -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS
                .toSeconds(uptime);

        if(days != 0)
            return days + " day(s)";

        if(hours != 0)
            return hours + " hour(s)";

        if(minutes != 0)
            return minutes + " minute(s)";

        if(seconds != 0)
            return seconds + " second(s)";

        return "Time not found";
    }

    /**
     * Formats a string in to Hours, minutes and seconds
     * @param duration Time in seconds
     * @return The fancy looking time
     */
    public String formatIntoHHMMSS(int duration) {
        int remainder = duration % 3600;
        int minutes = remainder / 60;
        int seconds = remainder % 60;

        return new StringBuilder().append(minutes).append(":").append(seconds < 10 ? "0" : "").append(seconds).toString();
    }

    /**
     * Returns a fancy looking formatted decimal from a long
     * @param showDecimal Show decimal point?
     * @param duration Duration in date/long format
     * @return The fancy looking time
     */
    public String getFormattedCooldown(boolean showDecimal, long duration) {
        if(showDecimal) {
            double seconds = Math.abs(duration / 1000.0f);
            return String.format("%.1f", seconds);
        }

        else
            return String.valueOf((int)duration / 1000L);
    }

    public int getTime(String string) {
        int time = 0;

        if (string.contains("m")) {
            String timeStr = strip(string);

            if (NumberUtils.isNumber(timeStr))
                time = NumberUtils.toInt(timeStr) * 60;

        }

        else if (string.contains("h")) {
            String timeStr = strip(string);

            if (NumberUtils.isNumber(timeStr))
                time = NumberUtils.toInt(timeStr) * 3600;

        }

        else if (string.contains("s")) {
            String timeStr = strip(string);

            if (NumberUtils.isNumber(timeStr))
                time = NumberUtils.toInt(timeStr);

        }

        else if (string.contains("d")) {
            String timeStr = strip(string);

            if (NumberUtils.isNumber(timeStr))
                time = NumberUtils.toInt(timeStr) * 86400;
        }

        else if (string.contains("w")) {
            String timeStr = strip(string);

            if (NumberUtils.isNumber(timeStr))
                time = NumberUtils.toInt(timeStr) * 604800;
        }

        else if (string.contains("y")) {
            String timeStr = strip(string);

            if (NumberUtils.isNumber(timeStr))
                time = NumberUtils.toInt(timeStr) * 31536000;
        }

        return time;
    }

    private String strip(String src) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);

            if (Character.isDigit(c))
                builder.append(c);
        }

        return builder.toString();
    }

}
