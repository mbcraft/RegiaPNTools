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

import java.awt.*;
import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class rapresents a Drive to be used as target for image writing.
 * The constructor should need all the parameters for : rapresentation (description)
 * and driver commands execution.
 * 
 * Actually :
 * number, name : used for identification
 * description : used for description purposes
 *
 * @author Marco Bagnaresi
 */
public class Drive {
    
    private final int myNumber;
    private final String myPath;
    private final String myDescription;

    /**
     * Builds a Drive instance.
     * 
     * @param number The unique drive number
     * @param path The drive name, eg 'H:\'
     * @param description The drive description, eg 'SD Card Reader Writer (H:\)'
     */
    public Drive(int number,String path,String description) {
        if (path==null)
            throw new InvalidParameterException("The drive path can't be null!");
        myNumber = number;
        myPath = path;
        myDescription = description;
    }


    public int getNumber() {
        return myNumber;
    }
    
    public String getPath() {
        return myPath;
    }
    
    public String getDescription() {
        return myDescription;
    }
    
    @Override
    public String toString() {
        return myDescription;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Drive) {
            Drive other = (Drive) o;
            return other.myPath.equals(myPath);
        } else return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.myPath);
        return hash;
    }

}
