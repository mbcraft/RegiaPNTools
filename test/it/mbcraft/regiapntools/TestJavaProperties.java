package it.mbcraft.regiapntools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 02/07/16.
 */
public class TestJavaProperties {
    public static void main(String[] args) throws IOException {
        Properties pt = new Properties();
        pt.setProperty("aaa","bbb");
        pt.storeToXML(new FileOutputStream("test_prop.xml"),"Test java properties.");
    }
}
