package it.mbcraft.regiapn.tools.operations.listeners;

import it.mbcraft.libraries.net.ftp.IFTPOperationListener;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 07/07/16.
 */
public interface ICheckForUpdatesOperationListener extends IFTPOperationListener {

    void startingCheckForUpdates();

    void checkingForUpdatedScripts();

    void checkingForNewPlayers();

    void checkingForSoftwareUpdate();

    void checkForUpdatesFinished();
}
