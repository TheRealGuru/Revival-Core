package gg.revival.core.tools;

import java.util.logging.Level;

public class Logger
{

    public static void log(String message)
    {
        log(Level.INFO, message);
    }

    public static void log(Level level, String message)
    {
        System.out.println("[Revival][" + level.toString() + "] " + message);
    }

}
