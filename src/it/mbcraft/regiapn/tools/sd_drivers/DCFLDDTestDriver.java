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

import java.io.*;
import java.util.*;

/**
 * Some suggestions from :
 * http://stackoverflow.com/questions/32314645/java-processbuilder-command-arguments-with-spaces-and-double-quotes-fails
 * and
 * http://stackoverflow.com/questions/24325432/execute-process-with-a-path-containing-address
 * <p>
 * bringed me to a different solution.
 * I got to the solution by trial and error.
 * <p>
 * It DOESN'T WORK on windows. It is kept do be used (maybe) on Linux.
 *
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public class DCFLDDTestDriver implements ISDDeployDriver {

    private static final Logger logger = LogManager.getLogger(DCFLDDTestDriver.class);

    @Override
    public boolean writeImage(Drive target, Properties globalConfig, Properties toolsConfig, final IDeployProgressListener listener) {

        String imagePath = PropertiesUtils.safeGet(toolsConfig,ToolsKeys.IMAGE_PATH_KEY);

        final File workingDir = new File("data/");

        File imageFile = new File(workingDir, imagePath);
        final long imageSize = imageFile.length();

        final List<String> commandParts = new ArrayList();
        commandParts.add("dcfldd");
        commandParts.add("status=on");
        commandParts.add("statusinterval=32");
        commandParts.add("if=" + imageFile.getAbsolutePath());
        commandParts.add("of=" + target.getPath());

        final ProcessBuilder pb = new ProcessBuilder();

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    listener.startImageWriting();
                    Process p = pb.directory(workingDir).command(commandParts).start();

                    pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
                    pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);

                    BufferedReader rd = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String ln;
                    boolean started = false;

                    while ((ln = rd.readLine()) != null) {
                        int percentage = calculatePercentageFromOutputLine(ln, imageSize);
                        listener.updateImagePercentage(percentage);
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
        logger.info("Running command : " + pb.toString());
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

    private int calculatePercentageFromOutputLine(String line, long imageSize) {
        int posOfOpenBracked = line.indexOf('(');
        int posOfClosingBracket = line.indexOf(')');
        String stSize = line.substring(posOfOpenBracked+1,posOfClosingBracket);
        long currentBytes = 1;
        if (stSize.indexOf('M')!=-1) {
            currentBytes*= 1000*1000;
            stSize = stSize.substring(0,stSize.indexOf('M'));
        }
        currentBytes*=Integer.parseInt(stSize);
        return (int) ((currentBytes*100)/imageSize);
    }

    @Override
    public List<Drive> getDriveList(Properties globalProperties, Properties toolsProperties) {
        return new ArrayList<>();
    }

    private Map<String,String> getBlockDriveList() {
        try {
            Process p = Runtime.getRuntime().exec("lsblk");
            BufferedReader rd = new BufferedReader(new InputStreamReader(p.getInputStream()));

            Map<String,String> result = new HashMap<>();

            //discard first line
            rd.readLine();
            String ln;
            while ((ln=rd.readLine())!=null) {
                if (ln.charAt(1)=='─') continue;
                String parts[] = ln.split(" ",-1);
                result.put("/dev/"+parts[0],parts[3]);
            }

            return result;

        } catch (IOException e) {
            logger.error("Unable to read block drive list",e);
        }
        throw new IllegalStateException("Unable to read block drive list");
    }

    private Map<String,String> getMountedDriveList() {
        try {
            Process p = Runtime.getRuntime().exec("mount");
            BufferedReader rd = new BufferedReader(new InputStreamReader(p.getInputStream()));

            Map<String, String> result = new HashMap<>();

            String ln;
            while ((ln = rd.readLine()) != null) {
                String[] parts = ln.split(" ");
                result.put(parts[0], parts[2]);
            }
            return result;
        } catch (IOException e) {
            logger.error("Unable to read mounted drives.", e);
        }
        throw new IllegalStateException("Unable to read mounted drives.");
    }

}