package it.mbcraft.regiapn.tools.operations.listeners;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 *
 * Created by Marco Bagnaresi on 30/05/2016.
 */
public interface IDeployProgressListener {

    void startDeploy();

    void startImageWriting();

    void updateImagePercentage(int value);

    void endImageWriting();

    void endPlayerWriting();

    void deployFinished(boolean successful);

    void error(String message);

    boolean hasImageWritingErrors();

    void endSupportScriptsWriting();

    void endBootConfigurationWriting();
}
