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

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
class OnlyFilesFileFilter implements FileFilter {

    @Override
    public boolean accept(File pathname) {
        return pathname.isFile();
    }
    
}
