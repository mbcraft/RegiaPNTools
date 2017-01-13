package it.mbcraft.regiapn.tools.utils;

import javafx.scene.control.TextField;

import java.security.InvalidParameterException;
import java.util.Properties;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 14/07/16.
 */
public class PropertiesUtils {

    /**
     * Gets a non-null value from the properties
     *
     * @param pt The properties object
     * @param key The key to get
     * @return The value
     */
    public static String safeGet(Properties pt, String key) {
        if (pt==null) throw new InvalidParameterException("Properties must not be null!");
        if (key==null || key.equals("")) throw new InvalidParameterException("The key can't be null or empty!");
        String value = pt.getProperty(key);
        return value!=null ? value : "";
    }

    /**
     * Saves a non-null value into the given properties.
     * @param key The key to use for storing the value
     * @param value The value to save
     * @param pt The properties in which store the value
     */
    public static void safePut(String key,String value, Properties pt) {
        if (pt==null) throw new InvalidParameterException("Properties must not be null!");
        if (key==null || key.equals("")) throw new InvalidParameterException("The key can't be null or empty!");

        String correctValue = value!=null ? value : "";
        pt.setProperty(key,correctValue);
    }

    /**
     * Saves a value into a JavaFX TextField reding it from a Properties object.
     *
     * @param pt The properties that contains the data
     * @param key The key to retrieve
     * @param tf The TextField in which store the value
     */
    public static void saveIntoField(Properties pt, String key,TextField tf) {
        if (pt==null) throw new InvalidParameterException("Properties must not be null!");
        if (key==null || key.equals("")) throw new InvalidParameterException("The key can't be null or empty!");
        if (tf==null) throw new InvalidParameterException("The TextField can't be null!");
        String value = pt.getProperty(key);
        String correctValue = value!=null ? value : "";
        tf.setText(correctValue);
    }

    /**
     * Fetches a value from a JavaFX TextField and saves it into a Properties object.
     *
     * @param tf The TextField
     * @param key The key use for fetching the data
     * @param pt The Properties object
     */
    public static void saveIntoProperties(TextField tf,String key,Properties pt) {
        if (pt==null) throw new InvalidParameterException("Properties must not be null!");
        if (key==null || key.equals("")) throw new InvalidParameterException("The key can't be null or empty!");
        if (tf==null) throw new InvalidParameterException("The TextField can't be null!");
        String value = tf.getText();
        String correctValue = value!=null ? value : "";
        pt.setProperty(key,correctValue);
    }
}
