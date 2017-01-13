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

import it.mbcraft.regiapn.tools.sd_drivers.Drive;
import it.mbcraft.regiapn.tools.ui.INodeProvider;
import it.mbcraft.libraries.ui.dialogs.DialogFactory;
import it.mbcraft.regiapn.tools.config.ConfigHelper;
import it.mbcraft.regiapn.tools.operations.DeploymentHelper;
import it.mbcraft.regiapn.tools.ui.strings.Loc;
import it.mbcraft.regiapn.tools.ui.strings.ValidatorHelper;

import java.util.*;

import it.mbcraft.regiapn.tools.operations.listeners.IDeployProgressListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import static it.mbcraft.libraries.ui.utils.Exec.ensureInsideJavaFXThread;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public class PlayerDeploymentPanelProvider implements INodeProvider {

    private final Node myNode;

    private ComboBox driveSelectionField;
    private TextField playerRegistrationCodeField;

    private final BooleanProperty hasRegistrationCodeProperty = new SimpleBooleanProperty(false);

    private final BooleanProperty deployingFlagProperty = new SimpleBooleanProperty(false);

    private final BooleanProperty deployEnabledProperty = new SimpleBooleanProperty();

    private final IDeployProgressListener myDpListener;

    public PlayerDeploymentPanelProvider(IDeployProgressListener deployListener) {
        myDpListener = deployListener;

        deployEnabledProperty.bind(hasRegistrationCodeProperty.and(deployingFlagProperty.not()));

        VBox allRows = new VBox();
        allRows.setPadding(new Insets(6));
        allRows.getChildren().addAll(makeRow1());

        TitledPane p = new TitledPane(Loc.get("player_deployment.panel"), allRows);
        p.setExpanded(true);
        p.setAnimated(false);
        p.setCollapsible(false);
        myNode = p;
    }

    public ReadOnlyBooleanProperty getDeployingFlagProperty() {
        return deployingFlagProperty;
    }

    private HBox makeRow1() {
        HBox row1 = new HBox();
        row1.setSpacing(10);
        row1.setPadding(new Insets(6));

        Label chooseDriveLabel = new Label(Loc.getLabel("choose_deploy_drive.label"));

        driveSelectionField = new ComboBox(FXCollections.observableArrayList());

        driveSelectionField.getItems().setAll(DeploymentHelper.getDriveList());
        driveSelectionField.disableProperty().bind(deployingFlagProperty);

        Button driveRefreshButton = new Button(Loc.get("drive_refresh.button"));
        driveRefreshButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                doUpdateDrives();
            }
        });
        driveRefreshButton.disableProperty().bind(deployingFlagProperty);

        Label playerRegistrationCodeLabel = new Label(Loc.getLabel("player_registration_code.label"));
        playerRegistrationCodeField = new TextField();
        playerRegistrationCodeField.setPrefColumnCount(7);
        playerRegistrationCodeField.setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                if (playerRegistrationCodeField.getText().isEmpty()) {
                    hasRegistrationCodeProperty.setValue(false);
                } else {
                    hasRegistrationCodeProperty.setValue(true);
                }
            }
        });
        playerRegistrationCodeField.disableProperty().bind(deployingFlagProperty);

        Button deployPlayerButton = new Button(Loc.get("deploy_player.button"));
        deployPlayerButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                doPlayerDeploy(myDpListener, playerRegistrationCodeField.getText());
            }
        });
        deployPlayerButton.disableProperty().bind(deployEnabledProperty.not());

        row1.getChildren().addAll(chooseDriveLabel, driveSelectionField, driveRefreshButton, playerRegistrationCodeLabel, playerRegistrationCodeField, deployPlayerButton);

        return row1;
    }

    private void doUpdateDrives() {
        driveSelectionField.getItems().setAll(DeploymentHelper.getDriveList());
    }

    private void doPlayerDeploy(final IDeployProgressListener progressListener, final String registrationCode) {

        progressListener.startDeploy();

        String registration_code = playerRegistrationCodeField.getText();

        if (ValidatorHelper.checkValidRegistrationCode(registration_code)) {
            final Properties globalProperties = ConfigHelper.readMainConfigs();

            final Drive selected = (Drive) driveSelectionField.getValue();
            final Properties toolsProperties = ConfigHelper.readToolsConfig();

            final Thread th = new Thread(new Runnable() {

                @Override
                public void run() {
                    deployingFlagProperty.setValue(true);
                    progressListener.startDeploy();
                    final boolean result = DeploymentHelper.fullDeployPlayer(selected, globalProperties, toolsProperties, myDpListener, registrationCode);

                    ensureInsideJavaFXThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result) {
                                progressListener.deployFinished(true);
                                DialogFactory.showInformationDialog(Loc.get("operation.dialog.title"), Loc.get("deploy.operation.dialog.success.text"));
                            } else

                            {
                                progressListener.deployFinished(false);
                                DialogFactory.showErrorDialog(Loc.get("operation.dialog.title"), Loc.get("deploy.operation.dialog.error.text"));
                            }
                            deployingFlagProperty.setValue(false);
                        }

                    });

                }
            });

            th.setName("Player deploy thread");
            th.setDaemon(true);
            th.start();

        }
    }


    @Override
    public Node getNode() {
        return myNode;
    }
}
