package it.mbcraft.regiapn.tools.operations.listeners.impl;

import it.mbcraft.regiapn.tools.operations.listeners.IConfigOperationListener;
import it.mbcraft.regiapn.tools.ui.strings.Loc;
import javafx.scene.control.TextArea;

import static it.mbcraft.libraries.ui.utils.Exec.ensureInsideJavaFXThread;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 07/07/16.
 */
public class ConfigLogWriter implements IConfigOperationListener {

    private final TextArea myArea;

    public ConfigLogWriter(TextArea area) {
        myArea = area;
    }

    @Override
    public void configLoaded() {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myArea.appendText(Loc.get("misc.config_loaded.message") + "\n");

            }
        });
    }

    @Override
    public void configEditable() {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myArea.appendText(Loc.get("misc.config_editable.message") + "\n");
            }

        });
    }



    @Override
    public void configSaved() {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myArea.appendText(Loc.get("misc.config_saved.message") + "\n");
            }

        });
    }

}