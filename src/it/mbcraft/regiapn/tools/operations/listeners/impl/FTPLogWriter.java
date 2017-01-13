package it.mbcraft.regiapn.tools.operations.listeners.impl;

import it.mbcraft.libraries.net.ftp.IFTPOperationListener;
import it.mbcraft.regiapn.tools.ui.strings.Loc;
import javafx.scene.control.TextArea;

import static it.mbcraft.libraries.ui.utils.Exec.ensureInsideJavaFXThread;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 07/07/16.
 */
public class FTPLogWriter implements IFTPOperationListener {

    private final TextArea myTextArea;

    public FTPLogWriter(TextArea area) {
        myTextArea = area;
    }

    @Override
    public void loginDone() {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myTextArea.appendText(Loc.get("ftp.login_done.message") + "\n");

            }
        });
    }

    @Override
    public void fetchingInfo(final String remotePath) {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myTextArea.appendText(Loc.get("ftp.fetching_info.message") + " : " + remotePath + "\n");

            }
        });
    }

    @Override
    public void listingFiles(final String remoteFolder) {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myTextArea.appendText(Loc.get("ftp.listing_files.message") + " : " + remoteFolder + "\n");

            }
        });
    }

    @Override
    public void uploadingFile(final String remoteFolder) {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myTextArea.appendText(Loc.get("ftp.uploading_file.message") + " : " + remoteFolder + "\n");

            }
        });
    }

    @Override
    public void fileUploaded(final String path) {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myTextArea.appendText(Loc.get("ftp.file_uploaded.message") + " : " + path + "\n");

            }
        });
    }

    @Override
    public void uploadingFolder(final String remoteFolder) {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                myTextArea.appendText(Loc.get("ftp.uploading_folder.message") + " : " + remoteFolder + "\n");

            }
        });
    }

    @Override
    public void folderUploaded(final String remoteFolder) {
        ensureInsideJavaFXThread(new Runnable() {

            @Override
            public void run() {
                myTextArea.appendText(Loc.get("ftp.folder_uploaded.message") + " : " + remoteFolder + "\n");

            }
        });
    }

    @Override
    public void downloadingFile(final String remoteFolder) {
        ensureInsideJavaFXThread(new Runnable() {

            @Override
            public void run() {
                myTextArea.appendText(Loc.get("ftp.downloading_file.message") + " : " + remoteFolder + "\n");

            }
        });
    }

    @Override
    public void fileDownloaded(final String path) {
        ensureInsideJavaFXThread(new Runnable() {

            @Override
            public void run() {
                myTextArea.appendText(Loc.get("ftp.file_downloaded.message") + " : " + path + "\n");
            }
        });
    }

    @Override
    public void downloadingFolder(String s) {

    }

    @Override
    public void folderDownloaded(final String remoteFolder) {
        ensureInsideJavaFXThread(new Runnable() {

            @Override
            public void run() {
                myTextArea.appendText(Loc.get("ftp.folder_downloaded.message") + " : " + remoteFolder + "\n");
            }
        });

    }

    @Override
    public void folderCreated(final String remoteFolder) {
        ensureInsideJavaFXThread(new Runnable() {

            @Override
            public void run() {
                myTextArea.appendText(Loc.get("ftp.folder_created.message") + " : " + remoteFolder + "\n");

            }
        });
    }

    @Override
    public void folderDeleted(final String remoteFolder) {
        ensureInsideJavaFXThread(new Runnable() {

            @Override
            public void run() {
                myTextArea.appendText(Loc.get("ftp.folder_deleted.message") + " : " + remoteFolder + "\n");

            }
        });
    }

    @Override
    public void fileDeleted(final String path) {
        ensureInsideJavaFXThread(new Runnable() {

            @Override
            public void run() {
                myTextArea.appendText(Loc.get("ftp.file_deleted.message") + " : " + path + "\n");
            }
        });
    }

    @Override
    public void logoutDone() {
        ensureInsideJavaFXThread(new Runnable() {

            @Override
            public void run() {
                myTextArea.appendText(Loc.get("ftp.logout_done.message") + "\n");
            }
        });
    }
}