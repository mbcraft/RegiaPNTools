package it.mbcraft.regiapn.tools.ui.strings;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ResourceBundle;

/**
 * Created by marco on 25/05/16.
 */
public class Loc {

    private static final Logger logger = LogManager.getLogger(Loc.class);

    private static Loc instance = null;
    private final ResourceBundle strings;

    private static final String LABEL_CLOSING = " : ";

    private static Loc getInstance() {
        if (instance==null)
            instance = new Loc();
        return instance;
    }

    private Loc() {
        strings = ResourceBundle.getBundle("strings");
    }

    public static String getLabel(String key) {
        return get(key)+LABEL_CLOSING;
    }

    public static String get(String key) {
        return getInstance().strings.getString(key);
    }

}
