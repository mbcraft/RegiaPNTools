/*
 * 
 *    Copyright MBCRAFT di Marco Bagnaresi - © 2015
 *    All rights reserved - Tutti i diritti riservati
 * 
 *    Mail : info [ at ] mbcraft [ dot ] it 
 *    Web : http://www.mbcraft.it
 * 
 */
package it.mbcraft.regiapn.tools.operations;

import it.mbcraft.regiapn.tools.config.ConfigHelper;
import it.mbcraft.regiapn.tools.config.ConfigKeys;
import it.mbcraft.regiapn.tools.config.ToolsKeys;
import it.mbcraft.regiapn.tools.sd_drivers.Drive;
import it.mbcraft.regiapn.tools.sd_drivers.ISDDeployDriver;
import it.mbcraft.regiapn.tools.operations.listeners.IDeployProgressListener;
import it.mbcraft.regiapn.tools.utils.PropertiesUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * This class contains method used in the player deployment phase.
 *
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public class DeploymentHelper {

    private static final Logger logger = LogManager.getLogger(DeploymentHelper.class.getName());

    private static final String TARGET_BOOT_FILENAME = "player_boot.xml";


    /**
     * Returns the driver used to deploy on SD card.
     *
     * @param globalProperties The global properties
     * @param toolsProperties The tools properties
     *
     * @return The driver instance
     */
    private static ISDDeployDriver getSDDriver(Properties globalProperties, Properties toolsProperties) {

        try {
            return (ISDDeployDriver) Class.forName(PropertiesUtils.safeGet(toolsProperties,ToolsKeys.IMAGE_WRITER_DRIVER_CLASS_KEY)).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            logger.error("Errore durante la creazione del driver per la gestione delle schede SD.", ex);
            throw new IllegalStateException("Unable to create sd driver");
        }
    }

    /**
     * Returns the drive list
     *

     * @return The drive list
     */
    public static List<Drive> getDriveList() {

        Properties globalProperties = ConfigHelper.readMainConfigs();
        Properties toolsProperties = ConfigHelper.readToolsConfig();

        ISDDeployDriver deployDriver = getSDDriver(globalProperties,toolsProperties);
        List<Drive> allDrivesFound = deployDriver.getDriveList(globalProperties,toolsProperties);
        return getSafeDriveList(allDrivesFound);
    }

    /**
     * Writes the os image to the SD card.
     *
     * @param target The target drive
     * @param globalProperties The global properties
     * @param toolsProperties The tools properties
     * @param listener The progress listener
     *
     * @return true if the operation was successful, false otherwise
     */
    private static boolean writeSDImage(Drive target, Properties globalProperties, Properties toolsProperties, IDeployProgressListener listener) {

        ISDDeployDriver writerDriver = getSDDriver(globalProperties,toolsProperties);
        return writerDriver.writeImage(target, globalProperties, toolsProperties, listener);
    }

    /**
     * Writes the player boot configuration inside the player_boot.xml file, in the
     * specified folder.
     *
     * @param target The target drive
     * @param globalProperties The global properties
     * @param toolsProperties The tools properties
     * @param listener The progress listener
     *
     * @return true if the operation was successful, false otherwise
     */
    private static boolean writePlayerBootConfig(Drive target, Properties globalProperties, Properties toolsProperties, IDeployProgressListener listener, String registrationCode) {

        //writes to target drive

        File targetDriveFile = new File(target.getPath());
        File bootFile = new File(targetDriveFile, TARGET_BOOT_FILENAME);

        Properties bootFileProperties = new Properties();
        PropertiesUtils.safePut("registration_protocol", PropertiesUtils.safeGet(globalProperties,ConfigKeys.SERVER_PROTOCOL),bootFileProperties);
        PropertiesUtils.safePut("registration_host", PropertiesUtils.safeGet(globalProperties,ConfigKeys.SERVER_HOST),bootFileProperties);
        PropertiesUtils.safePut("registration_page", "/api/" + PropertiesUtils.safeGet(globalProperties,ConfigKeys.SERVER_API) + "/notify/register_instance",bootFileProperties);
        PropertiesUtils.safePut("registration_code", registrationCode,bootFileProperties);

        try (FileOutputStream fos = new FileOutputStream(bootFile)) {
            bootFileProperties.storeToXML(fos, "RegiaPN Player Boot Configuration");
            listener.endBootConfigurationWriting();
            return true;
        } catch (IOException ex) {
            logger.error("Errore durante la scrittura del file di configurazione di boot del player.", ex);
            return false;
        }
    }

    /**
     * Writes the latest player available.
     *
     * @param target The target drive
     * @param globalProperties The global properties
     * @param toolsProperties The tools properties
     * @param listener The progress listener
     *
     * @return true if the operation is successful, false otherwise
     */
    private static boolean writeLatestPlayer(Drive target, Properties globalProperties, Properties toolsProperties, IDeployProgressListener listener) {
        //writes to target drive

        File targetBootDir = new File(target.getPath());

        File sourceDir = new File("data/files/player/");
        File[] toCopy = sourceDir.listFiles(new OnlyFilesFileFilter());

        //last version
        File playerFile = toCopy[toCopy.length-1];
        
        try {
            //copio l'ultima versione del player
            File targetFile = new File(targetBootDir,playerFile.getName());
            Files.copy(playerFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            //ok
            File deploySpecsFile = new File(targetBootDir, "deploy_specs.txt");
            //scrivo il file delle specifiche di deploy
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(deploySpecsFile))) {
                bw.write(playerFile.getName());
            }

            listener.endPlayerWriting();

            return true;
        } catch (IOException ex) {
            logger.catching(ex);
            return false;
        }
    }

    /**
     * Writes the needed support scripts
     *
     * @param globalProperties The global properties
     * @param toolsProperties The tools properties
     * @param listener The progress listener
     *
     * @return true if the operation is successful, false otherwise
     */
    private static boolean writeSupportScripts(Drive target, Properties globalProperties, Properties toolsProperties, IDeployProgressListener listener) {
        //writes to target drive

        File targetBootDir = new File(target.getPath());
        File sourceDir = new File("data/files/script/");
        File[] toCopy = sourceDir.listFiles(new OnlyFilesFileFilter());

        try {
            for (File source : toCopy) {
                File targetFile = new File(targetBootDir,source.getName());
                Files.copy(source.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            listener.endSupportScriptsWriting();
            return true;
        } catch (IOException ex) {
            logger.catching(ex);
            return false;
        }
    }

    /**
     * Executes a full player deploy.
     *
     * @param target The target drive
     * @param globalProperties The global properties
     * @param toolsProperties The tools properties
     * @param listener The progress listener
     *
     * @return true if the deploy is fully successful, false otherwise
     */
    public static boolean fullDeployPlayer(Drive target, Properties globalProperties, Properties toolsProperties, IDeployProgressListener listener, String registrationCode) {

        if (target==null) {
            listener.error("Unable to fetch target drive!");
            return false;
        }
        //
        boolean op1 = writeSDImage(target, globalProperties, toolsProperties, listener);
        logger.info("SD Image writing operation result : "+(op1 ? "ok" : "error"));
        if (op1) {
            boolean op2 = writeLatestPlayer(target, globalProperties, toolsProperties, listener);
            logger.info("Writing latest player operation result : " + (op2 ? "ok" : "error"));
            if (op2) {
                boolean op3 = writePlayerBootConfig(target, globalProperties, toolsProperties, listener, registrationCode);
                logger.info("Writing player boot config operation result : " + (op3 ? "ok" : "error"));
                if (op3) {
                    boolean op4 = writeSupportScripts(target, globalProperties, toolsProperties, listener);
                    logger.info("Writing support scripts operation result : " + (op4 ? "ok" : "error"));
                } else return false;
            } else return false;
        } else return false;
        return true;
    }

    /**
     * Returns a safe drive list, excluding the drive from which this software is running.
     * @param allDrivesFound The drives found from the driver
     * @return The filtered drive list
     */
    private static List<Drive> getSafeDriveList(List<Drive> allDrivesFound) {
        File workingDir = new File(".");
        List<Drive> result = new ArrayList<>();
        for (Drive d : allDrivesFound) {
            File drivePath = new File(d.getPath());
            if (!workingDir.getAbsoluteFile().toPath().startsWith(drivePath.toPath())) {
                result.add(d);
            }
        }
        return result;
    }
}
