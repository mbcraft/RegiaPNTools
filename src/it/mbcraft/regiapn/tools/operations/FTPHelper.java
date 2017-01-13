package it.mbcraft.regiapn.tools.operations;

import it.mbcraft.libraries.net.ftp.EasyFtp;
import it.mbcraft.libraries.net.ftp.FolderElement;
import it.sauronsoftware.ftp4j.FTPFile;

import java.io.File;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 07/07/16.
 */
public class FTPHelper {

    public static boolean ftpUploadWithTips(EasyFtp ftp, String remotePath, FTPFile remoteFtpFolder, File localFolder) {
        FolderElement remoteTips = FolderElement.createFromFTPFile(null, ftp, remotePath, remoteFtpFolder);
        FolderElement localTips = FolderElement.createFromLocalDir(null, localFolder);

        localTips.markIfNewOrModified(remoteTips);
        localTips.removeUnmodifiedElements();

        return ftp.uploadFolderWithTips(localTips, localFolder, remotePath);
    }

    public static boolean ftpDownloadWithTips(EasyFtp ftp,String remotePath,FTPFile remoteFtpFolder,File localFolder) {
        FolderElement remoteTips = FolderElement.createFromFTPFile(null, ftp, remotePath, remoteFtpFolder);
        FolderElement localTips = FolderElement.createFromLocalDir(null, localFolder);

        remoteTips.markIfNewOrModified(localTips);
        remoteTips.removeUnmodifiedElements();

        return ftp.downloadFolderWithTips(remoteTips, remotePath, localFolder);
    }


}
