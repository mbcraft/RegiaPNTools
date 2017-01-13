package it.mbcraft.regiapn.tools.operations;

import it.mbcraft.libraries.command.io.CleanupUnusedJarLibrariesCommand;
import it.mbcraft.libraries.net.ftp.EasyFtp;
import it.mbcraft.libraries.ui.dialogs.DialogFactory;
import it.mbcraft.regiapn.tools.config.ConfigHelper;
import it.mbcraft.regiapn.tools.config.ConfigKeys;
import it.mbcraft.regiapn.tools.config.EnvKeys;
import it.mbcraft.regiapn.tools.ui.strings.Loc;
import it.mbcraft.regiapn.tools.operations.listeners.ICheckForUpdatesOperationListener;
import it.mbcraft.regiapn.tools.utils.PropertiesUtils;
import it.sauronsoftware.ftp4j.FTPFile;
import java.io.File;
import java.util.Properties;

import static it.mbcraft.libraries.ui.utils.Exec.ensureInsideJavaFXThread;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 07/07/16.
 */
public class UpdatesHelper {

    private static boolean executeFtpDownload(File localFolder, String remoteFolder, ICheckForUpdatesOperationListener listener, Properties globalPt, FtpCredentialsHelper ftpCredentialsHelper) {

        boolean allOk = true;

        EasyFtp ftp = new EasyFtp(PropertiesUtils.safeGet(globalPt,ConfigKeys.SERVER_HOST), ftpCredentialsHelper.getFtpUsername(), ftpCredentialsHelper.getFtpPassword());
        ftp.setFTPOperationListener(listener);
        allOk &= ftp.login();

        //REMOTE PATH FOR UPDATES IS IN DOWNLOAD FOLDER!!!
        Properties env = ConfigHelper.readEnvConfigs();
        String remotePath = PropertiesUtils.safeGet(env,EnvKeys.REMOTE_DOWNLOAD_ROOT_PATH) + remoteFolder;
        FTPFile remoteFtpFolder = ftp.getFileInfo(remotePath, false);

        allOk &= remoteFtpFolder != null;

        if (allOk) {
            allOk &= FTPHelper.ftpDownloadWithTips(ftp, remotePath, remoteFtpFolder, localFolder);
        }
        allOk &= ftp.logout();

        return allOk;
    }

    private static void executeFtpScriptDownload(ICheckForUpdatesOperationListener listener, Properties globalPt, FtpCredentialsHelper ftpCredentialsHelper) {

        File dataFolder = getLocalDataFolder();
        File localFolder = new File(dataFolder,"files/script/");
        if (!localFolder.exists()) {
            throw new IllegalStateException("Local folder "+localFolder.getPath()+" must exist!");
        }
        String remoteFolder = "software/MBCRAFT/RegiaPN_Tools/data/files/script/";
        listener.checkingForUpdatedScripts();
        executeFtpDownload(localFolder, remoteFolder, listener, globalPt, ftpCredentialsHelper);
    }

    private static void executeFtpPlayerDownload(ICheckForUpdatesOperationListener listener, Properties globalPt, FtpCredentialsHelper ftpCredentialsHelper) {

        File dataFolder = getLocalDataFolder();
        File localFolder = new File(dataFolder,"files/player/");
        String remoteFolder = "software/MBCRAFT/RegiaPN_Tools/data/files/player/";
        listener.checkingForNewPlayers();
        executeFtpDownload(localFolder, remoteFolder, listener, globalPt, ftpCredentialsHelper);
    }

    private static void executeFtpSoftwareUpdateDownload(ICheckForUpdatesOperationListener listener, Properties globalPt, FtpCredentialsHelper ftpCredentialsHelper) {

        File distFolder = getLocalDistFolder();
        String remoteFolder = "software/MBCRAFT/RegiaPN_Tools/dist/";
        listener.checkingForSoftwareUpdate();
        executeFtpDownload(distFolder, remoteFolder, listener, globalPt, ftpCredentialsHelper);
    }

    private static void executePostInstallCleanup() {

        File distFolder = getLocalDistFolder();

        File libFolder = new File(distFolder,"lib");
        File jarPath = new File(distFolder,"RBTools.jar");

        CleanupUnusedJarLibrariesCommand cleanUp = new CleanupUnusedJarLibrariesCommand(jarPath,libFolder,true);
        cleanUp.execute();
    }

    private static File getLocalDistFolder() {
        Properties env = ConfigHelper.readEnvConfigs();
        return new File(PropertiesUtils.safeGet(env,EnvKeys.LOCAL_DIST_DOWNLOAD_PATH));
    }

    private static File getLocalDataFolder() {
        Properties env = ConfigHelper.readEnvConfigs();
        return new File(PropertiesUtils.safeGet(env, EnvKeys.DATA_ROOT_PATH));
    }

    public static void doCheckForUpdates(final ICheckForUpdatesOperationListener listener, final Properties globalPt, final FtpCredentialsHelper ftpCredentialsHelper) {

        ftpCredentialsHelper.readCredentials();

        Thread th = new Thread(new Runnable() {

            @Override
            public void run() {
                EasyFtp ftp = new EasyFtp(PropertiesUtils.safeGet(globalPt,ConfigKeys.SERVER_HOST), ftpCredentialsHelper.getFtpUsername(), ftpCredentialsHelper.getFtpPassword());
                boolean result = true;
                result &= ftp.login();
                result &= ftp.logout();

                final boolean finalResult = result;

                if (finalResult) {
                    listener.startingCheckForUpdates();
                    executeFtpScriptDownload(listener, globalPt, ftpCredentialsHelper);
                    executeFtpPlayerDownload(listener, globalPt, ftpCredentialsHelper);
                    executeFtpSoftwareUpdateDownload(listener, globalPt, ftpCredentialsHelper);
                    listener.checkForUpdatesFinished();

                    executePostInstallCleanup();

                    showUpdateSuccessfulDialog();
                } else {
                    showUpdateFailedDialog();
                }

            }
        });

        th.start();

    }

    private static void showUpdateSuccessfulDialog() {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                DialogFactory.showInformationDialog(Loc.get("dialog.check_for_updates.title"), Loc.get("dialog.check_for_updates.success.message"));
            }
        });

    }

    private static void showUpdateFailedDialog() {
        ensureInsideJavaFXThread(new Runnable() {
            @Override
            public void run() {
                DialogFactory.showErrorDialog(Loc.get("dialog.check_ftp_access.title"), Loc.get("dialog.check_ftp_access.error.message"));
            }
        });
    }
}
