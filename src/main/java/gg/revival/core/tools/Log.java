package gg.revival.core.tools;

import java.util.logging.Level;

public class Log {

    public void log(String message) {
        log(Level.INFO, message);
    }

    public void log(Level level, String message) {
        System.out.println("[Revival][" + level.toString() + "] " + message);
    }

}
