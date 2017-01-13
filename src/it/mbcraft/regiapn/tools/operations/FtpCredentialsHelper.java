package it.mbcraft.regiapn.tools.operations;

import it.mbcraft.libraries.encryption.sca0.SCA0Decrypter;
import it.mbcraft.libraries.net.http.EasyHttp;
import it.mbcraft.libraries.net.http.HttpErrorException;
import it.mbcraft.regiapn.tools.config.ConfigHelper;
import it.mbcraft.regiapn.tools.config.ConfigKeys;
import javafx.scene.control.TextField;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 08/08/16.
 */
public class FtpCredentialsHelper {

    private final Properties myConfig;
    private final TextField myApiPasswordField;

    private boolean credentialsAvailable;
    private String ftpUsername;
    private String ftpPassword;

    public FtpCredentialsHelper(TextField api_password_field) {
        myConfig = ConfigHelper.readMainConfigs();
        myApiPasswordField = api_password_field;

    }

    public void readCredentials() {

        EasyHttp http = new EasyHttp();

        Properties pt = new Properties();
        pt.setProperty("email",myConfig.getProperty(ConfigKeys.EMAIL,""));
        pt.setProperty("api_password",myApiPasswordField.getText());

        String address = myConfig.getProperty(ConfigKeys.SERVER_PROTOCOL)+"://";
        address+=myConfig.getProperty(ConfigKeys.SERVER_HOST);
        address+="/api/"+myConfig.getProperty(ConfigKeys.SERVER_API)+"/access/get_ftp_login_tokens";

        try {
            String result = http.doPost(address, pt);

            if (http.isLastResponseOK()) {

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                SCA0Decrypter dec = new SCA0Decrypter(new ByteArrayInputStream(Base64.decodeBase64(result)),out);
                dec.decrypt();

                Properties responseProperties = new Properties();
                responseProperties.loadFromXML(new ByteArrayInputStream(out.toByteArray()));
                ftpUsername = responseProperties.getProperty("ftp_username");
                ftpPassword = responseProperties.getProperty("ftp_password");
                credentialsAvailable = true;
            }
        } catch (HttpErrorException | IOException ex) {
            credentialsAvailable = false;
        }
    }

    public boolean areCredentialsAvailable() {
        return credentialsAvailable;
    }

    public String getFtpUsername() {
        return ftpUsername;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }

}
