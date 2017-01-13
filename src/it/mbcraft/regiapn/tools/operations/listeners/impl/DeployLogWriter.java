package it.mbcraft.regiapn.tools.operations.listeners.impl;

import it.mbcraft.regiapn.tools.ui.strings.Loc;
import it.mbcraft.regiapn.tools.operations.listeners.IDeployProgressListener;
import javafx.scene.control.TextArea;

import static it.mbcraft.libraries.ui.utils.Exec.ensureInsideJavaFXThread;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 07/07/16.
 */
public class DeployLogWriter implements IDeployProgressListener {

    private final TextArea myTextArea;
    private boolean imageWritingErrors = false;

    public DeployLogWriter(TextArea myArea) {
        myTextArea = myArea;
    }

    @Override
    public void startDeploy() {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myTextArea.appendText(Loc.get("deploy.started.message")+"\n");
            }
        });

    }

    @Override
    public void startImageWriting() {
        imageWritingErrors = false;
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myTextArea.appendText(Loc.get("deploy.image_write_started.message")+"\n");
            }
        });
    }

    @Override
    public void updateImagePercentage(final int value) {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myTextArea.appendText(Loc.get("deploy.image_write_percentage.message").replace("{p}",value+" %")+"\n");
            }
        });
    }

    @Override
    public void endImageWriting() {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myTextArea.appendText(Loc.get("deploy.image_write_done.message")+"\n");
            }
        });
    }

    @Override
    public void endBootConfigurationWriting() {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myTextArea.appendText(Loc.get("deploy.boot_configuration_written.message")+"\n");
            }
        });
    }

    @Override
    public void endSupportScriptsWriting() {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myTextArea.appendText(Loc.get("deploy.support_scripts_written.message")+"\n");
            }
        });
    }

    @Override
    public void endPlayerWriting() {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myTextArea.appendText(Loc.get("deploy.player_install_done.message")+"\n");
            }
        });
    }

    @Override
    public void deployFinished(final boolean successful) {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                if (successful)
                    myTextArea.appendText(Loc.get("deploy.installation_successful.message")+"\n");
                else
                    myTextArea.appendText(Loc.get("deploy.installation_error.message")+"\n");

            }
        });
    }

    @Override
    public void error(final String message) {
        imageWritingErrors = true;
        ensureInsideJavaFXThread(new Runnable() {

            @Override
            public void run() {
                myTextArea.appendText(Loc.get("deploy.image_write_error.message")+message+"\n");
            }
        });

    }

    @Override
    public boolean hasImageWritingErrors() {
        return imageWritingErrors;
    }

}