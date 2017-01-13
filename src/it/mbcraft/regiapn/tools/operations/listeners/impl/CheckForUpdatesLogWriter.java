package it.mbcraft.regiapn.tools.operations.listeners.impl;

import it.mbcraft.libraries.net.ftp.IFTPOperationListener;
import it.mbcraft.regiapn.tools.operations.listeners.ICheckForUpdatesOperationListener;
import it.mbcraft.regiapn.tools.ui.strings.Loc;
import javafx.scene.control.TextArea;

import static it.mbcraft.libraries.ui.utils.Exec.ensureInsideJavaFXThread;


/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 07/07/16.
 */
public class CheckForUpdatesLogWriter implements ICheckForUpdatesOperationListener,IFTPOperationListener {

    private final TextArea myLog;

    public CheckForUpdatesLogWriter(TextArea log) {
        myLog = log;
    }

    @Override
    public void loginDone() {
    }

    @Override
    public void fetchingInfo(final String s) {

    }

    @Override
    public void listingFiles(final String s) {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myLog.appendText(Loc.get("ftp.listing_files.message")+" : "+s+"\n");
            }
        });

    }

    @Override
    public void uploadingFile(final String s) {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myLog.appendText(Loc.get("ftp.uploading_file.message")+" : "+s+"\n");
            }
        });
    }

    @Override
    public void fileUploaded(final String s) {

    }

    @Override
    public void uploadingFolder(final String s) {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myLog.appendText(Loc.get("ftp.uploading_folder.message")+" : "+s+"\n");
            }
        });
    }

    @Override
    public void folderUploaded(final String s) {

    }

    @Override
    public void downloadingFile(final String s) {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myLog.appendText(Loc.get("ftp.downloading_file.message")+" : "+s+"\n");
            }
        });
    }

    @Override
    public void fileDownloaded(final String s) {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myLog.appendText(Loc.get("ftp.file_downloaded.message")+ " : " + s + "\n");
            }
        });
    }

    @Override
    public void downloadingFolder(final String s) {

    }

    @Override
    public void folderDownloaded(final String s) {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myLog.appendText(Loc.get("ftp.folder_downloaded.message")+ " : " + s + "\n");
            }
        });
    }

    @Override
    public void folderCreated(final String s) {

    }

    @Override
    public void folderDeleted(final String s) {
        throw new IllegalStateException("Error during check for updates : folderDeleted called!");

    }

    @Override
    public void fileDeleted(final String s) {
        throw new IllegalStateException("Error during check for updates : fileDeleted called!");
    }

    @Override
    public void logoutDone() {
    }

    //phases

    @Override
    public void startingCheckForUpdates() {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myLog.appendText(Loc.get("check_for_updates.start.message")+"\n");
            }
        });
    }

    @Override
    public void checkingForUpdatedScripts() {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myLog.appendText(Loc.get("check_for_updates.scripts.message")+"\n");
            }
        });
    }

    @Override
    public void checkingForSoftwareUpdate() {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myLog.appendText(Loc.get("check_for_updates.software.message")+"\n");
            }
        });
    }

    @Override
    public void checkingForNewPlayers() {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myLog.appendText(Loc.get("check_for_updates.players.message")+"\n");
            }
        });
    }

    @Override
    public void checkForUpdatesFinished() {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myLog.appendText(Loc.get("check_for_updates.end.message")+"\n");
            }
        });
    }


}
