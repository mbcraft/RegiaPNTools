/*
 * 
 *    Copyright MBCRAFT di Marco Bagnaresi - © 2015
 *    All rights reserved - Tutti i diritti riservati
 * 
 *    Mail : info [ at ] mbcraft [ dot ] it 
 *    Web : http://www.mbcraft.it
 * 
 */

package it.mbcraft.regiapn.tools.ui.panels;

import it.mbcraft.libraries.net.ftp.EasyFtp;
import it.mbcraft.regiapn.tools.Main;
import it.mbcraft.regiapn.tools.config.ConfigKeys;
import it.mbcraft.regiapn.tools.operations.FtpCredentialsHelper;
import it.mbcraft.regiapn.tools.ui.INodeProvider;
import it.mbcraft.regiapn.tools.operations.listeners.IConfigOperationListener;
import it.mbcraft.libraries.ui.dialogs.DialogFactory;
import it.mbcraft.regiapn.tools.config.ConfigHelper;
import it.mbcraft.regiapn.tools.ui.strings.Loc;
import it.mbcraft.regiapn.tools.utils.PropertiesUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public class GlobalConfigurationPanelProvider implements INodeProvider {

    private static final Logger logger = LogManager.getLogger(GlobalConfigurationPanelProvider.class.getName());
    private final Window myWindow;
    private final IConfigOperationListener myOpListener;

    private enum Mode {
        EDIT, SAVE
    }

    private final Node myNode;
    private TextField serverProtocolField;
    private TextField serverHostField;
    private TextField serverApiField;

    private TextField emailField;
    private TextField apiPasswordField;

    private TextField musicTracksRootPathField;
    private TextField customersRootPathField;

    private Button editSaveButton;
    private Button checkFtpButton;

    private Mode nextMode = Mode.EDIT;
    private final ReadOnlyBooleanProperty deployingFlagProperty;
    private final BooleanProperty editProperty = new SimpleBooleanProperty(false);

    private final FtpCredentialsHelper credentialsHelper;

    public GlobalConfigurationPanelProvider(final Window w, ReadOnlyBooleanProperty deployingFlag, IConfigOperationListener opListener) {

        myWindow = w;
        deployingFlagProperty = deployingFlag;
        myOpListener = opListener;

        HBox row1 = makeRow1();
        HBox row2 = makeRow2();
        HBox row3 = makeRow3();
        HBox row4 = makeRow4();
        HBox row5 = makeRow5();

        VBox allRows = new VBox();

        allRows.setSpacing(5);
        allRows.getChildren().addAll(row1, row2, row3, row4, row5);
        allRows.setPadding(new Insets(6));

        TitledPane p = new TitledPane(Loc.get("global_configuration.panel"), allRows);

        p.setExpanded(true);
        p.setAnimated(false);
        p.setCollapsible(false);
        myNode = p;

        credentialsHelper = new FtpCredentialsHelper(apiPasswordField);

        doLoad();
    }

    private void doLoad() {
        //actually save values into file ...
        Properties pt = new Properties();

        try (FileInputStream fis = new FileInputStream(Main.CONFIG_FILE)) {
            pt.load(fis);
        } catch (IOException ex) {
            logger.catching(ex);
        }

        PropertiesUtils.saveIntoField(pt, ConfigKeys.SERVER_PROTOCOL, serverProtocolField);
        PropertiesUtils.saveIntoField(pt, ConfigKeys.SERVER_HOST, serverHostField);
        PropertiesUtils.saveIntoField(pt, ConfigKeys.SERVER_API, serverApiField);
        PropertiesUtils.saveIntoField(pt, ConfigKeys.EMAIL, emailField);
        PropertiesUtils.saveIntoField(pt, ConfigKeys.MUSIC_TRACKS_ROOT_PATH, musicTracksRootPathField);
        PropertiesUtils.saveIntoField(pt, ConfigKeys.CLIENTI_ROOT_PATH, customersRootPathField);

        myOpListener.configLoaded();
    }

    private void doSave() {
        //actually save values into file ...
        Properties pt = new Properties();

        PropertiesUtils.saveIntoProperties(serverProtocolField, ConfigKeys.SERVER_PROTOCOL, pt);
        PropertiesUtils.saveIntoProperties(serverHostField, ConfigKeys.SERVER_HOST, pt);
        PropertiesUtils.saveIntoProperties(serverApiField, ConfigKeys.SERVER_API, pt);
        PropertiesUtils.saveIntoProperties(emailField, ConfigKeys.EMAIL, pt);
        PropertiesUtils.saveIntoProperties(musicTracksRootPathField, ConfigKeys.MUSIC_TRACKS_ROOT_PATH, pt);
        PropertiesUtils.saveIntoProperties(customersRootPathField, ConfigKeys.CLIENTI_ROOT_PATH, pt);

        ConfigHelper.writeMainConfig(pt);

        //puts the app fields into 'not editable state'
        editProperty.setValue(false);
        editSaveButton.setText(Loc.get("edit.button"));
        nextMode = Mode.EDIT;

        myOpListener.configSaved();
    }

    private void doEdit() {
        editProperty.setValue(true);
        editSaveButton.setText(Loc.get("save.button"));
        nextMode = Mode.SAVE;

        myOpListener.configEditable();
    }

    private void doEditSave() {
        if (nextMode == Mode.EDIT) {
            doEdit();
            return;
        }
        if (nextMode == Mode.SAVE) {
            doSave();
            return;
        }
    }

    public TextField getApiPasswordField() {
        return apiPasswordField;
    }

    public FtpCredentialsHelper getCredentialsHelper() {
        return credentialsHelper;
    }

    private void browseForTracksPath(Window w) {
        DirectoryChooser ch = new DirectoryChooser();
        ch.setTitle(Loc.get("browse_music_root_window.title"));
        File selectedFolder = ch.showDialog(w);
        if (selectedFolder != null) {
            musicTracksRootPathField.setText(selectedFolder.getAbsolutePath());
        }
    }

    private void browseForCustomersPath(Window w) {
        DirectoryChooser ch = new DirectoryChooser();
        ch.setTitle(Loc.get("browse_customers_root_window.title"));
        File selectedFolder = ch.showDialog(w);
        if (selectedFolder != null) {
            customersRootPathField.setText(selectedFolder.getAbsolutePath());
        }
    }

    private HBox makeRow1() {
        HBox row = new HBox();
        row.setSpacing(10);

        Label serverProtocolLabel = new Label(Loc.getLabel("server_protocol.label"));
        serverProtocolField = new TextField();
        serverProtocolField.setPrefColumnCount(10);
        serverProtocolField.editableProperty().bind(editProperty);
        serverProtocolField.disableProperty().bind(editProperty.not());

        Label serverHostLabel = new Label(Loc.getLabel("server_host.label"));
        serverHostField = new TextField();
        serverHostField.editableProperty().bind(editProperty);
        serverHostField.disableProperty().bind(editProperty.not());

        Label serverApiLabel = new Label(Loc.getLabel("server_api.label"));
        serverApiField = new TextField();
        serverApiField.setPrefColumnCount(10);
        serverApiField.editableProperty().bind(editProperty);
        serverApiField.disableProperty().bind(editProperty.not());

        row.getChildren().addAll(serverProtocolLabel, serverProtocolField, serverHostLabel, serverHostField, serverApiLabel, serverApiField);

        return row;
    }

    private HBox makeRow2() {
        HBox row = new HBox();
        row.setSpacing(10);

        Label usernameLabel = new Label(Loc.getLabel("email.label"));
        emailField = new TextField();
        emailField.editableProperty().bind(editProperty);
        emailField.disableProperty().bind(editProperty.not());

        Label apiPasswordLabel = new Label(Loc.getLabel("api_password.label"));
        apiPasswordField = new PasswordField();

        row.getChildren().addAll(usernameLabel, emailField, apiPasswordLabel, apiPasswordField);

        return row;
    }

    private HBox makeRow3() {
        HBox row = new HBox();
        row.setSpacing(10);

        Label musicTracksPathLabel = new Label(Loc.getLabel("music_tracks_path.label"));
        musicTracksRootPathField = new TextField();
        musicTracksRootPathField.setPrefColumnCount(30);
        musicTracksRootPathField.editableProperty().bind(editProperty);
        musicTracksRootPathField.disableProperty().bind(editProperty.not());
        Button browseMusicTracksPathButton = new Button(Loc.get("browse.button"));
        browseMusicTracksPathButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                browseForTracksPath(myWindow);
            }
        });
        browseMusicTracksPathButton.disableProperty().bind(editProperty.not());

        row.getChildren().addAll(musicTracksPathLabel, musicTracksRootPathField, browseMusicTracksPathButton);
        return row;
    }

    private HBox makeRow4() {
        HBox row = new HBox();
        row.setSpacing(10);

        Label clientiRootPathLabel = new Label(Loc.getLabel("customers_folder_path.label"));
        customersRootPathField = new TextField();
        customersRootPathField.setPrefColumnCount(30);
        customersRootPathField.editableProperty().bind(editProperty);
        customersRootPathField.disableProperty().bind(editProperty.not());
        Button clientiRootPathButton = new Button(Loc.get("browse.button"));
        clientiRootPathButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                browseForCustomersPath(myWindow);
            }
        });
        clientiRootPathButton.disableProperty().bind(editProperty.not());

        row.getChildren().addAll(clientiRootPathLabel, customersRootPathField, clientiRootPathButton);
        return row;
    }

    private HBox makeRow5() {
        HBox row = new HBox();
        row.setSpacing(10);

        Label editSaveLabel = new Label(Loc.getLabel("edit_or_save.label"));

        editSaveButton = new Button(Loc.get("edit.button"));
        editSaveButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                doEditSave();
            }
        });
        editSaveButton.disableProperty().bind(deployingFlagProperty);

        Label checkFtpCredentialsLabel = new Label(Loc.getLabel("check_ftp_access.label"));
        checkFtpButton = new Button(Loc.get("check_ftp_access.button"));
        checkFtpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                doCheckFtp();
            }
        });

        row.getChildren().addAll(editSaveLabel, editSaveButton, checkFtpCredentialsLabel, checkFtpButton);
        return row;
    }

    private void doCheckFtp() {

        credentialsHelper.readCredentials();

        Thread th = new Thread(new Runnable() {

            @Override
            public void run() {

                if (credentialsHelper.areCredentialsAvailable()) {

                    EasyFtp ftp = new EasyFtp(serverHostField.getText(), credentialsHelper.getFtpUsername(), credentialsHelper.getFtpPassword());
                    boolean result = true;
                    result &= ftp.login();
                    result &= ftp.logout();

                    final boolean finalResult = result;

                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            if (finalResult)
                                DialogFactory.showInformationDialog(Loc.get("dialog.check_ftp_access.title"), Loc.get("dialog.check_ftp_access.success.message"));
                            else {
                                DialogFactory.showErrorDialog(Loc.get("dialog.check_ftp_access.title"), Loc.get("dialog.check_ftp_access.error.message"));
                            }
                        }
                    });
                } else {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            DialogFactory.showErrorDialog(Loc.get("dialog.check_ftp_access.title"), Loc.get("dialog.check_ftp_access.error.message"));
                        }
                    });
                }


            }
        });

        th.start();

    }

    @Override
    public Node getNode() {
        return myNode;
    }

}
