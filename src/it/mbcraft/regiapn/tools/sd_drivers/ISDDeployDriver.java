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

import it.mbcraft.regiapn.tools.operations.listeners.IDeployProgressListener;

import java.util.List;
import java.util.Properties;

/**
 *
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public interface ISDDeployDriver {

    /**
     * Apre il programma o scrive direttamente l'immagine del sistema operativo sulla scheda SD.
     * 
     * @param globalProperties Le configurazioni principali
     * @param toolsProperties Le configurazioni dei tool di terze parti
     * @return true se l'operazione è andata a buon fine, false altrimenti.
     */
    boolean writeImage(Drive target, Properties globalProperties, Properties toolsProperties, IDeployProgressListener listener);

    /**
     * Ritorna la lista dei drive disponibili.
     *
     * @param globalProperties Le configurazioni principali
     * @param toolsProperties La configurazione dei tool di terze parti
     * @return La lista dei drive disponibili, o una lista vuota se nessun drive è disponibile
     */
    List<Drive> getDriveList(Properties globalProperties, Properties toolsProperties);

}