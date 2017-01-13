/*
 * 
 *    Copyright MBCRAFT di Marco Bagnaresi - © 2015
 *    All rights reserved - Tutti i diritti riservati
 * 
 *    Mail : info [ at ] mbcraft [ dot ] it 
 *    Web : http://www.mbcraft.it
 * 
 */

package it.mbcraft.regiapn.tools.sd_drivers;

import it.mbcraft.regiapn.tools.config.ToolsKeys;
import it.mbcraft.regiapn.tools.operations.listeners.IDeployProgressListener;
import it.mbcraft.regiapn.tools.utils.PropertiesUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Marco Bagnaresi
 */
public class USBITDriver implements ISDDeployDriver {

    private static final Logger logger = LogManager.getLogger(USBITDriver.class);

    @Override
    public boolean writeImage(Drive target, Properties globalConfig, Properties toolsConfig, final IDeployProgressListener listener) {
        String imagePath = PropertiesUtils.safeGet(toolsConfig,ToolsKeys.IMAGE_PATH_KEY);

        final File workingDir = new File("data\\");

        final File imageFile = new File(workingDir, imagePath);

        final List<String> commandParts = new ArrayList<>();
        commandParts.add("\"" + workingDir.getAbsolutePath() + "\\" + PropertiesUtils.safeGet(toolsConfig,ToolsKeys.IMAGE_WRITER_COMMAND_PATH_KEY) + "\""); // command line tools
        commandParts.add("r");  // restore 
        commandParts.add("" + target.getNumber()); // target device
        commandParts.add('"' + imageFile.getAbsolutePath() + '"'); // image path
        commandParts.add("/d"); // it is a bootable device
        commandParts.add("/s"); // scroll mode

        StringBuilder sb = new StringBuilder();
        for (String part : commandParts) {
            sb.append(part).append(" ");
        }

        final ProcessBuilder pb = new ProcessBuilder();

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    listener.startImageWriting();
                    Process p = pb.directory(workingDir).command(commandParts).start();

                    BufferedReader rd = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String ln;
                    boolean started = false;

                    while ((ln = rd.readLine()) != null) {
                        if (ln.contains("%")) {
                            String onlyPercentage = ln.replace("%", "").trim();
                            try {
                                int percentage = Integer.parseInt(onlyPercentage);
                                listener.updateImagePercentage(percentage);
                                started = true;
                            } catch (NumberFormatException nfe) {
                                logger.debug("Trashing string from usbit output : "+ln);
                            }
                        } else if (started && ln.contains("ok")) {
                            listener.updateImagePercentage(100);
                            break;
                        }
                    }
                    p.waitFor();

                } catch (IOException | InterruptedException ex) {
                    listener.error(ex.getMessage());
                    logger.catching(ex);
                } finally {
                    listener.endImageWriting();
                }
            }
        });
        logger.info("Running command : " + sb.toString());
        th.setName("External process thread");
        th.setDaemon(true);
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            logger.catching(e);
        }
        return !listener.hasImageWritingErrors();
    }
    

    @Override
    public List<Drive> getDriveList(Properties globalConfig, Properties toolsConfig) {
        
        File workingDir = new File("data\\");
        
        List<String> commandParts = new ArrayList<>();
        commandParts.add(workingDir.getAbsolutePath()+"\\"+ PropertiesUtils.safeGet(toolsConfig,ToolsKeys.IMAGE_WRITER_COMMAND_PATH_KEY));
        commandParts.add("l");
        
        List<Drive> result = new ArrayList<>();
        
        try {
            ProcessBuilder pb = new ProcessBuilder();
            Map<String,String> env = pb.environment();
            env.put("Path", env.get("Path")+File.pathSeparator+workingDir.getAbsolutePath());

            Process p = pb.directory(workingDir).command(commandParts).start();
            p.waitFor();
            BufferedReader rd = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String ln;
            boolean nextAreDevices = false;
            while ((ln = rd.readLine())!=null) {
                if (nextAreDevices && ln.trim().length()>2) {
                    String[] parts = ln.split("\\|");
                    int number = Integer.parseInt(parts[0].trim());
                    String driveName = parts[2].trim();
                    String path = parts[5].trim();

                    logger.info("Found sd drive num : "+number+" Volume name : "+path);
                    Drive d = new Drive(number,path,"("+path+") "+driveName);
                    
                    result.add(d);
                }
                
                if (ln.contains("---------------------------------------------"))
                    nextAreDevices = true;
                
            }

            return result;
        } catch (IOException | InterruptedException ex) {
            logger.catching(ex);
            return result;
        }
    }

}
