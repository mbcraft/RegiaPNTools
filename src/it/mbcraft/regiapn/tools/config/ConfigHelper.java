/*
 * 
 *    Copyright MBCRAFT di Marco Bagnaresi - © 2015
 *    All rights reserved - Tutti i diritti riservati
 * 
 *    Mail : info [ at ] mbcraft [ dot ] it 
 *    Web : http://www.mbcraft.it
 * 
 */

package it.mbcraft.regiapn.tools.config;

import it.mbcraft.regiapn.tools.Main;
import it.mbcraft.regiapn.tools.utils.PropertiesUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


/**
 *
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public class ConfigHelper {
    
    private static final Logger logger = LogManager.getLogger(ConfigHelper.class.getName());

    /**
     * Reads the configuration from the configuration file.
     *
     * @return The configuration properties
     */
    public static Properties readEnvConfigs() {
        Properties pt = new Properties();
        try (FileInputStream fis = new FileInputStream(Main.ENV_FILE)) {
            pt.load(fis);
        }
        catch (IOException ex) {
            logger.catching(ex);
        }
        return pt;
    }

    /**
     * Reads the configuration from the configuration file.
     * 
     * @return The configuration properties
     */
    public static Properties readMainConfigs() {
        Properties pt = new Properties();
        try (FileInputStream fis = new FileInputStream(Main.CONFIG_FILE)) {
            pt.load(fis);
        }
        catch (IOException ex) {
            logger.catching(ex);
        }
        return pt;
    }
    
    /**
     * Writes the configuration data inside the config file.
     * 
     * @param pt The configuration data
     */
    public static void writeMainConfig(Properties pt) {
        try (FileOutputStream fos = new FileOutputStream(Main.CONFIG_FILE)) {
            pt.store(fos,"RegiaPN Tools Configuration");
        }
        catch (IOException ex) {
            logger.catching(ex);
        }
    }
    
    /**
     * Returns the tools properties.
     * 
     * @return The tools properties as a Properties object.
     */
    public static Properties readToolsConfig() {
        Properties globalPt = readMainConfigs();
        
        String os = System.getProperty("os.name");
        String toolsConfigFilename;
        if (os.toLowerCase().contains("windows"))
            toolsConfigFilename = "tools_windows.ini";
        else
            toolsConfigFilename = "tools_linux.ini";

        Properties env = readEnvConfigs();

        String dataPath = PropertiesUtils.safeGet(env, EnvKeys.DATA_ROOT_PATH);
        if (dataPath==null || dataPath.equals("")) dataPath = "data";
        
        File toolsDataRootPath = new File(dataPath);
        File toolsConfig = new File(toolsDataRootPath,toolsConfigFilename);
        
        if (!toolsConfig.exists()) {
            throw new IllegalStateException("Unable to found file : "+toolsConfig.getAbsolutePath());
        }
        
        Properties toolsProperties = new Properties();
        try (FileInputStream fis = new FileInputStream(toolsConfig)) {
            toolsProperties.load(fis);
            return toolsProperties;
        } catch (IOException ex) {
            logger.error("Errore durante la lettura della configurazione dei tools esterni.");
            throw new IllegalStateException("Unable to read tools config file.");
        }
    }   
}