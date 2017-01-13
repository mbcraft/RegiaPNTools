/*
 * 
 *    Copyright MBCRAFT di Marco Bagnaresi - © 2015
 *    All rights reserved - Tutti i diritti riservati
 * 
 *    Mail : info [ at ] mbcraft [ dot ] it 
 *    Web : http://www.mbcraft.it
 * 
 */

package it.mbcraft.regiapn.tools.ui.strings;

import it.mbcraft.libraries.ui.dialogs.DialogFactory;

/**
 *
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public class ValidatorHelper {

    public static boolean checkValidUsername(String username) {
        if (username == null || username.trim().length() < 4) {
            DialogFactory.showErrorDialog("Error!!", "A username with at least 4 characters must be set!");
            return false;
        }
        else
            return true;
    }
    
    public static boolean checkValidPassword(String pwd) {
        if (pwd == null || pwd.trim().length() < 4) {
            DialogFactory.showErrorDialog("Error!!", "A password with at least 4 characters must be set!");
            return false;
        }
        else
            return true;
    }
    
    public static boolean checkValidRegistrationCode(String code) {
        if (code == null || code.trim().length() < 4) {
            DialogFactory.showErrorDialog("Error!!", "A registration code with at least 4 characters must be set!");
            return false;
        }
        else
            return true;
    }
}
