package it.mbcraft.regiapn.tools.operations;

import it.mbcraft.libraries.net.ftp.EasyFtp;
import it.mbcraft.libraries.net.ftp.IFTPOperationListener;
import it.mbcraft.libraries.ui.dialogs.DialogFactory;
import it.mbcraft.regiapn.tools.config.ConfigHelper;
import it.mbcraft.regiapn.tools.config.ConfigKeys;
import it.mbcraft.regiapn.tools.config.EnvKeys;
import it.mbcraft.regiapn.tools.ui.strings.Loc;
import it.mbcraft.regiapn.tools.ui.strings.ValidatorHelper;
import it.mbcraft.regiapn.tools.utils.PropertiesUtils;
import it.sauronsoftware.ftp4j.FTPFile;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Properties;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 07/07/16.
 */
public class MusicOperationsHelper {

    private static final Logger logger = LogManager.getLogger(MusicOperationsHelper.class);

    public static void doMusicTracksUpload(final IFTPOperationListener listener, final FtpCredentialsHelper ftpCredentialsHelper) {
        //load credentials
        ftpCredentialsHelper.readCredentials();
        //load from config file
        final Properties pt = ConfigHelper.readMainConfigs();

        if (ftpCredentialsHelper.areCredentialsAvailable() && ValidatorHelper.checkValidUsername(ftpCredentialsHelper.getFtpUsername()) && ValidatorHelper.checkValidPassword(ftpCredentialsHelper.getFtpPassword())) {

            logger.info("Trying to upload music tracks with ftp ...");
            //get from password field

            Thread th = new Thread(
                    new Runnable() {

                        @Override
                        public void run() {
                            final boolean result = executeFtpMusicTracksUpload(listener,pt,ftpCredentialsHelper);

                            Platform.runLater(new Runnable() {

                                @Override
                                public void run() {
                                    if (result)
                                        DialogFactory.showInformationDialog(Loc.get("dialog.music_tracks_upload.title"), Loc.get("dialog.music_tracks_upload.success.message"));
                                    else {
                                        DialogFactory.showErrorDialog(Loc.get("dialog.music_tracks_upload.title"), Loc.get("dialog.music_tracks_upload.error.message"));
                                    }
                                }
                            });
                        }
                    }
            );
            th.start();

        }
    }

    private static boolean executeFtpMusicTracksUpload(IFTPOperationListener ftpOperationListener, Properties pt, FtpCredentialsHelper credentialsHelper) {

        boolean allOk = true;

        EasyFtp ftp = new EasyFtp(PropertiesUtils.safeGet(pt,ConfigKeys.SERVER_HOST), credentialsHelper.getFtpUsername(), credentialsHelper.getFtpPassword());
        ftp.setFTPOperationListener(ftpOperationListener);
        allOk &= ftp.login();

        File musicTracksRoot = new File(PropertiesUtils.safeGet(pt,ConfigKeys.MUSIC_TRACKS_ROOT_PATH));

        Properties env = ConfigHelper.readEnvConfigs();
        String remotePath = PropertiesUtils.safeGet(env,EnvKeys.REMOTE_UPLOAD_ROOT_PATH) + "music";

        FTPFile remoteMusicRoot = ftp.getFileInfo(remotePath, true);

        allOk &= remoteMusicRoot!=null;

        if (allOk)
            allOk &= FTPHelper.ftpUploadWithTips(ftp,remotePath,remoteMusicRoot,musicTracksRoot);

        allOk &= ftp.logout();

        return allOk;
    }
}
