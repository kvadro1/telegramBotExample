package configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ConfigReader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);
    private static Properties properties = new Properties();
    private static ConfigReader ourInstance = new ConfigReader();

    private ConfigReader() {
        try {
            properties.load(ClassLoader.getSystemResourceAsStream("config.properties"));
        } catch (Exception e) {
            logger.error("Error get string param from config file", e);
        }
    }

    public static ConfigReader getInstance() {
        return ourInstance;
    }

    //get config in string format
    public String getProp(String inS) {
        String result = null;
        result = properties.getProperty(inS);
        return result;
    }

    //get config in int format
    public int getPropI(String inS) {
        int result = 0;
        try {
            result = Integer.parseInt(properties.getProperty(inS));
        } catch (Exception e) {
            logger.error("Error get int param from config file", e);
        }
        return result;
    }
}
