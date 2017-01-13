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

import it.mbcraft.libraries.net.ftp.IFTPOperationListener;
import it.mbcraft.regiapn.tools.operations.CustomersOperationsHelper;
import it.mbcraft.regiapn.tools.operations.FtpCredentialsHelper;
import it.mbcraft.regiapn.tools.ui.INodeProvider;
import it.mbcraft.regiapn.tools.ui.strings.Loc;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public class CustomersFoldersPanelProvider implements INodeProvider {

    private static final Logger logger = LogManager.getLogger(MusicTracksPanelProvider.class.getName());

    private final FtpCredentialsHelper myFtpCredentialsHelper;

    private final Node myNode;
    private final IFTPOperationListener ftpOperationListener;

    public CustomersFoldersPanelProvider(FtpCredentialsHelper ftpCredentialsHelper, IFTPOperationListener listener) {
        ftpOperationListener = listener;
        myFtpCredentialsHelper = ftpCredentialsHelper;

        VBox allRows = new VBox();
        allRows.setSpacing(10);
        allRows.setPadding(new Insets(6));

        allRows.getChildren().addAll(makeRow1());

        TitledPane p = new TitledPane(Loc.get("customers.panel"), allRows);
        p.setExpanded(true);
        p.setAnimated(false);
        p.setCollapsible(false);
        myNode = p;
    }

    private HBox makeRow1() {
        HBox row1 = new HBox();
        row1.setSpacing(10);

        Label customersDownloadLabel = new Label(Loc.getLabel("customer_folders_download.label"));
        Button customersDownloadButton = new Button(Loc.get("download.button"));
        customersDownloadButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                CustomersOperationsHelper.doCustomersFoldersDownload(ftpOperationListener,myFtpCredentialsHelper);
            }
        });

        Label customersUploadLabel = new Label(Loc.getLabel("customer_folders_upload.label"));
        Button customersUploadButton = new Button(Loc.get("upload.button"));
        customersUploadButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                CustomersOperationsHelper.doCustomersSpotUpload(ftpOperationListener,myFtpCredentialsHelper);
            }
        });

        row1.getChildren().addAll(customersDownloadLabel, customersDownloadButton, customersUploadLabel, customersUploadButton);

        return row1;
    }


    @Override
    public Node getNode() {
        return myNode;
    }

}