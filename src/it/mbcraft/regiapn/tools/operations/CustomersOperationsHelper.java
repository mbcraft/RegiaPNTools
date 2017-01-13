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
public class CustomersOperationsHelper {

    private static final Logger logger = LogManager.getLogger(CustomersOperationsHelper.class);

    public static void doCustomersFoldersDownload(final IFTPOperationListener ftpOperationListener, final FtpCredentialsHelper ftpCredentialsHelper) {

        ftpCredentialsHelper.readCredentials();
        //load from config file
        final Properties pt = ConfigHelper.readMainConfigs();
        pt.putAll(ConfigHelper.readEnvConfigs());

        if (ftpCredentialsHelper.areCredentialsAvailable() && ValidatorHelper.checkValidUsername(ftpCredentialsHelper.getFtpUsername()) && ValidatorHelper.checkValidPassword(ftpCredentialsHelper.getFtpPassword())) {

            logger.info("Trying to download customers folders with ftp ...");
            //get from password field


            Thread th = new Thread(new Runnable() {

                @Override
                public void run() {
                    final boolean result = executeFtpCustomersDownload(ftpOperationListener,pt,ftpCredentialsHelper);

                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            if (result)
                                DialogFactory.showInformationDialog(Loc.get("dialog.customer_folders_download.title"), Loc.get("dialog.customer_folders_download.success.message"));
                            else
                                DialogFactory.showErrorDialog(Loc.get("dialog.customer_folders_download.title"), Loc.get("dialog.customer_folders_download.error.message"));

                        }
                    });

                }
            });
            th.start();


        }
    }

    public static void doCustomersSpotUpload(final IFTPOperationListener ftpOperationListener, final FtpCredentialsHelper ftpCredentialsHelper) {
        //load from config file
        final Properties pt = ConfigHelper.readMainConfigs();

        ftpCredentialsHelper.readCredentials();

        if (ftpCredentialsHelper.areCredentialsAvailable() && ValidatorHelper.checkValidUsername(ftpCredentialsHelper.getFtpUsername()) && ValidatorHelper.checkValidPassword(ftpCredentialsHelper.getFtpPassword())) {

            logger.info("Trying to upload spot tracks to server with ftp ...");

            Thread th = new Thread(new Runnable() {

                @Override
                public void run() {
                    final boolean result = executeFtpSpotUpload(ftpOperationListener,pt,ftpCredentialsHelper);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (result)
                                DialogFactory.showInformationDialog(Loc.get("dialog.customer_folders_upload.title"), Loc.get("dialog.customer_folders_upload.success.message"));
                            else
                                DialogFactory.showErrorDialog(Loc.get("dialog.customer_folders_upload.title"), Loc.get("dialog.customer_folders_upload.error.message"));
                        }

                    });
                }
            });

            th.start();

        }
    }

    private static boolean executeFtpCustomersDownload(IFTPOperationListener ftpOperationListener, Properties pt, FtpCredentialsHelper ftpCredentialsHelper) {

        boolean allOk = true;

        EasyFtp ftp = new EasyFtp(PropertiesUtils.safeGet(pt,ConfigKeys.SERVER_HOST), ftpCredentialsHelper.getFtpUsername(), ftpCredentialsHelper.getFtpPassword());
        ftp.setFTPOperationListener(ftpOperationListener);
        allOk &= ftp.login();

        File clientiRoot = new File(PropertiesUtils.safeGet(pt,ConfigKeys.CLIENTI_ROOT_PATH));

        Properties env = ConfigHelper.readEnvConfigs();

        //REMOTE PATH FOR CUSTOMERS IS IN UPLOAD FOLDER!!!
        String remotePath = PropertiesUtils.safeGet(env,EnvKeys.REMOTE_UPLOAD_ROOT_PATH) + "clienti";

        FTPFile remoteClienteRoot = ftp.getFileInfo(remotePath, false);

        allOk &= remoteClienteRoot!=null;

        if (allOk) {
            allOk &= FTPHelper.ftpDownloadWithTips(ftp, remotePath, remoteClienteRoot, clientiRoot);
        }
        allOk &= ftp.logout();

        return allOk;
    }

    private static boolean executeFtpSpotUpload(IFTPOperationListener ftpOperationListener, Properties pt, FtpCredentialsHelper ftpCredentialsHelper) {

        boolean allOk = true;

        EasyFtp ftp = new EasyFtp(PropertiesUtils.safeGet(pt,ConfigKeys.SERVER_HOST), ftpCredentialsHelper.getFtpUsername(), ftpCredentialsHelper.getFtpPassword());
        ftp.setFTPOperationListener(ftpOperationListener);
        allOk &= ftp.login();

        File clientiRoot = new File(PropertiesUtils.safeGet(pt,ConfigKeys.CLIENTI_ROOT_PATH));

        Properties env = ConfigHelper.readEnvConfigs();

        //REMOTE PATH FOR CUSTOMERS IS IN UPLOAD FOLDER!!!
        String remotePath = PropertiesUtils.safeGet(env,EnvKeys.REMOTE_UPLOAD_ROOT_PATH) + "clienti";
        FTPFile remoteClientiRoot = ftp.getFileInfo(remotePath, true);

        allOk &= remoteClientiRoot!=null;

        if (allOk)
            allOk &= FTPHelper.ftpUploadWithTips(ftp,remotePath,remoteClientiRoot,clientiRoot);

        allOk &= ftp.logout();

        return allOk;
    }
}
