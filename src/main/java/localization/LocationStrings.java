package localization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

//Class (singleton for read string from localization files)
public class LocationStrings {
    private static Logger logger = LoggerFactory.getLogger(LocationStrings.class);
    private static Properties rusMessages = new Properties();
    private static Properties engMessages = new Properties();

    private static LocationStrings ourInstance = new LocationStrings();

    private LocationStrings() {
        try {
            rusMessages.load(ClassLoader.getSystemResourceAsStream("location_ru.properties"));
            engMessages.load(ClassLoader.getSystemResourceAsStream("location_en.properties"));
        } catch (Exception e) {
            logger.error("Error read resources files: ", e);
        }
    }

    public static LocationStrings getInstance() {
        return ourInstance;
    }

    //get property by name and location
    public String getS(String propName, Locals loc) {
        String result = null;
        try {
            if (loc == Locals.EN)
                return engMessages.getProperty(propName);
            if (loc == Locals.RU)
                return rusMessages.getProperty(propName);
        } catch (Exception e) {
            logger.error("Error read properties sting: ", e);
        }
        return engMessages.getProperty(propName);
    }
}
