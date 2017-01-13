package it.mbcraft.regiapntools;

import it.mbcraft.libraries.net.ftp.EasyFtp;
import it.mbcraft.libraries.net.ftp.FolderElement;
import it.sauronsoftware.ftp4j.FTPFile;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by marco on 25/05/16.
 */
public class TestRadioAndBusinessFtp {
    @Test
    public void testConnectWithFtp() throws IOException {
        EasyFtp ftp = new EasyFtp("185.17.106.140","ftpsecure","KXZ99457_P$");

        assertTrue("Il login remoto non è andato a buon fine!!",ftp.login());

        FTPFile[] files = ftp.listRootFiles();
        assertNotNull("I file remoti sono nulli!!",files);

        for (FTPFile f : files) {
            System.out.println("Root file : "+f.getName());
        }

        FTPFile[] files2 = ftp.listFiles("/radioandbusiness.com/");
        for (FTPFile f : files2) {
            System.out.println("File inside radioandbusiness.com : "+f.getName());
        }

        FTPFile[] files3 = ftp.listFiles("/radioandbusiness.com/files/upload");
        for (FTPFile f : files3) {
            System.out.println("File inside radioandbusiness.com/files/upload : "+f.getName());
        }

        FTPFile[] files4 = ftp.listFiles("/radioandbusiness.com/files/upload/music/");
        for (FTPFile f : files4) {
            System.out.println("File inside radioandbusiness.com/files/upload/music : "+f.getName());
        }

        FTPFile[] files5 = ftp.listFiles("/radioandbusiness.com/files/upload/clienti/");
        for (FTPFile f : files5) {
            System.out.println("File inside radioandbusiness.com/files/upload/clienti : "+f.getName());
        }

        FTPFile info = ftp.getFileInfo("/radioandbusiness.com/files/upload/clienti",false);
        assertNotNull("Le info ritornate sono nulle con mlist!!",info);

        assertTrue("Il logout remoto non è andato a buon fine!!",ftp.logout());

    }

    @Test
    public void testSimpleUploadMusicFolderWithSpaces() {
        EasyFtp ftp = new EasyFtp("radioandbusiness.com","ftpsecure","KXZ99457_P$");
        ftp.login();

        File musicTracksRoot = new File("test_data/music/");
        String remotePath = "/radioandbusiness.com/tests/music";

        ftp.uploadFolder(musicTracksRoot,remotePath);

        assertNotNull("La cartella in remoto non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/music/The Tragically Hip - World Container",true));
        assertNotNull("La cartella in remoto non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/music/The Tragically Hip - World Container/05.Luv (sic).txt",true));
        assertNotNull("La cartella in remoto non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/music/The Tragically Hip - World Container/08.Last Night I Dreamed You Didn't Love Me.txt",true));
        assertNotNull("La cartella in remoto non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/music/The Tragically Hip - World Container/09.The Drop-Off.txt",true));
        assertNotNull("La cartella in remoto non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/music/The Tragically Hip - World Container/folder.jpg",true));

        ftp.deleteFolderContent(remotePath);

        assertNull("La cartella in remoto non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/music/The Tragically Hip - World Container",true));

        ftp.logout();
    }

    @Test
    public void testUploadMusicFolderWithTips() {
        EasyFtp ftp = new EasyFtp("radioandbusiness.com","ftpsecure","KXZ99457_P$");
        ftp.login();

        File musicTracksRoot = new File("test_data/music/");
        String remotePath = "/radioandbusiness.com/tests/music";

        FTPFile ftpfile = ftp.getFileInfo(remotePath,true);

        assertNotNull("Le info del file in remoto sono nulle!!",ftpfile);

        FolderElement remote = FolderElement.createFromFTPFile(null,ftp,remotePath,ftpfile);
        FolderElement localRootTips = FolderElement.createFromLocalDir(null,musicTracksRoot);
        localRootTips.markIfNewOrModified(remote);
        localRootTips.removeUnmodifiedElements();

        ftp.uploadFolderWithTips(localRootTips,musicTracksRoot,remotePath);

        assertNotNull("La cartella in remoto non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/music/The Tragically Hip - World Container",true));
        assertNotNull("La cartella in remoto non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/music/The Tragically Hip - World Container/05.Luv (sic).txt",true));
        assertNotNull("La cartella in remoto non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/music/The Tragically Hip - World Container/08.Last Night I Dreamed You Didn't Love Me.txt",true));
        assertNotNull("La cartella in remoto non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/music/The Tragically Hip - World Container/09.The Drop-Off.txt",true));
        assertNotNull("La cartella in remoto non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/music/The Tragically Hip - World Container/folder.jpg",true));

        ftp.deleteFolderContent(remotePath);

        assertNull("La cartella in remoto non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/music/The Tragically Hip - World Container",true));

        ftp.logout();
    }

    @Test
    public void testDownloadCustomersWithTips() {
        EasyFtp ftp = new EasyFtp("radioandbusiness.com","ftpsecure","KXZ99457_P$");
        ftp.login();

        File clientiRoot = new File("test_data/empty_clienti");
        String remotePath = "/radioandbusiness.com/tests/clienti";
        FTPFile remoteClienteRoot = ftp.getFileInfo(remotePath,false);
        FolderElement remoteTips = FolderElement.createFromFTPFile(null,ftp,remotePath,remoteClienteRoot);
        FolderElement localTips = FolderElement.createFromLocalDir(null,clientiRoot);

        remoteTips.markIfNewOrModified(localTips);
        remoteTips.removeUnmodifiedElements();

        ftp.downloadFolderWithTips(remoteTips,remotePath,clientiRoot);

        assertTrue("La cartella barbiere_puffolandia non è stata scaricata!!",new File("test_data/empty_clienti/barbiere_puffolandia/").exists());
        assertTrue("La cartella capelli_paperopoli non è stata scaricata!!",new File("test_data/empty_clienti/capelli_paperopoli/").exists());
        assertTrue("La cartella gommista_puffolandia non è stata scaricata!!",new File("test_data/empty_clienti/gommista_puffolandia/").exists());

        deleteDirectory(clientiRoot);
        clientiRoot.mkdir();

        assertFalse("La cartella barbiere_puffolandia non è stata eliminata!!",new File("test_data/empty_clienti/barbiere_puffolandia/").exists());
        assertFalse("La cartella capelli_paperopoli non è stata eliminata!!",new File("test_data/empty_clienti/capelli_paperopoli/").exists());
        assertFalse("La cartella gommista_puffolandia non è stata eliminata!!",new File("test_data/empty_clienti/gommista_puffolandia/").exists());

        ftp.logout();
    }

    @Test
    public void testUploadCustomersWithTips() {
        EasyFtp ftp = new EasyFtp("radioandbusiness.com","ftpsecure","KXZ99457_P$");
        ftp.login();

        File clientiRoot = new File("test_data/clienti");
        String remotePath = "/radioandbusiness.com/tests/empty_clienti";

        FTPFile remoteClientiRoot = ftp.getFileInfo(remotePath,true);
        FolderElement remoteTips = FolderElement.createFromFTPFile(null,ftp,remotePath,remoteClientiRoot);
        FolderElement localTips = FolderElement.createFromLocalDir(null,clientiRoot);

        localTips.markIfNewOrModified(remoteTips);
        localTips.removeUnmodifiedElements();

        ftp.uploadFolderWithTips(localTips,clientiRoot,remotePath);

        assertNotNull("La cartella gommista_puffolandia non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/empty_clienti/gommista_puffolandia",true));
        assertNotNull("La cartella barbiere_puffolandia non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/empty_clienti/barbiere_puffolandia",true));
        assertNotNull("La cartella capelli_paperopoli non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/empty_clienti/capelli_paperopoli",true));

        ftp.deleteFolderContent(remotePath);

        assertNull("La cartella gommista_puffolandia non è stata eliminata!!",ftp.getFileInfo("/radioandbusiness.com/tests/empty_clienti/gommista_puffolandia",true));
        assertNull("La cartella barbiere_puffolandia non è stata eliminata!!",ftp.getFileInfo("/radioandbusiness.com/tests/empty_clienti/barbiere_puffolandia",true));
        assertNull("La cartella capelli_paperopoli non è stata eliminata!!",ftp.getFileInfo("/radioandbusiness.com/tests/empty_clienti/capelli_paperopoli",true));

        ftp.logout();
    }

    @Test
    public void testUploadToPartialCustomersWithTips() {
        EasyFtp ftp = new EasyFtp("radioandbusiness.com","ftpsecure","KXZ99457_P$");
        ftp.login();

        File clientiRoot = new File("test_data/clienti");
        String remotePath = "/radioandbusiness.com/tests/partial_clienti";

        ftp.deleteFolder(remotePath);
        ftp.uploadFolder(new File("test_data/partial_clienti"),remotePath);

        FTPFile remoteClientiRoot = ftp.getFileInfo(remotePath,true);
        FolderElement remoteTips = FolderElement.createFromFTPFile(null,ftp,remotePath,remoteClientiRoot);
        FolderElement localTips = FolderElement.createFromLocalDir(null,clientiRoot);

        localTips.markIfNewOrModified(remoteTips);
        localTips.removeUnmodifiedElements();

        assertTrue("La cartella gommista_puffolandia non è inclusa nei tips!",localTips.containsFolder("gommista_puffolandia"));
        assertTrue("La cartella capelli_paperopoli non è inclusa nei tips!",localTips.containsFolder("capelli_paperopoli"));
        assertFalse("La cartella barbiere_puffolandia è inclusa nei tips!",localTips.containsFolder("barbiere_puffolandia"));

        ftp.uploadFolderWithTips(localTips,clientiRoot,remotePath);

        assertNotNull("La cartella gommista_puffolandia non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/partial_clienti/gommista_puffolandia",true));
        assertNotNull("La cartella barbiere_puffolandia non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/partial_clienti/barbiere_puffolandia",true));
        assertNotNull("La cartella capelli_paperopoli non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/partial_clienti/capelli_paperopoli",true));

        assertNotNull("La cartella capelli_paperopoli non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/partial_clienti/capelli_paperopoli/via_tavole/spot/Leggi_una_rivista.txt",true));
        assertNotNull("La cartella capelli_paperopoli non è stata caricata!!",ftp.getFileInfo("/radioandbusiness.com/tests/partial_clienti/gommista_puffolandia/steppe/spot/Riparazione_in_corso.txt",true));

        ftp.deleteFolder(remotePath);

        assertNull("La cartella gommista_puffolandia non è stata eliminata!!",ftp.getFileInfo("/radioandbusiness.com/tests/partial_clienti/gommista_puffolandia",true));
        assertNull("La cartella barbiere_puffolandia non è stata eliminata!!",ftp.getFileInfo("/radioandbusiness.com/tests/partial_clienti/barbiere_puffolandia",true));
        assertNull("La cartella capelli_paperopoli non è stata eliminata!!",ftp.getFileInfo("/radioandbusiness.com/tests/partial_clienti/capelli_paperopoli",true));

        ftp.uploadFolder(new File("test_data/partial_clienti"),remotePath);

        ftp.logout();
    }

    @Test
    public void testFoldersWithNotWorkingFileInfo() {
        EasyFtp ftp = new EasyFtp("radioandbusiness.com","ftpsecure","KXZ99457_P$");

        assertTrue("Il login remoto non è andato a buon fine!!",ftp.login());

        assertNotNull(ftp.getFileInfo("/radioandbusiness.com/tests/upload/music",false));
        assertNotNull(ftp.getFileInfo("/radioandbusiness.com/tests/upload/clienti",false));

        assertTrue("Il logout remoto non è andato a buon fine!!",ftp.logout());

    }

    private void deleteDirectory(File f) {
        File[] files = f.listFiles();
        for (File ff : files) {
            if (ff.isFile()) {
                ff.delete();
                continue;
            }
            if (ff.isDirectory())
                deleteDirectory(ff);
        }
        f.delete();
    }
}
