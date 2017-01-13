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
import it.mbcraft.regiapn.tools.operations.FtpCredentialsHelper;
import it.mbcraft.regiapn.tools.operations.MusicOperationsHelper;
import it.mbcraft.regiapn.tools.ui.INodeProvider;
import it.mbcraft.regiapn.tools.ui.strings.Loc;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public class MusicTracksPanelProvider implements INodeProvider {

    private static final Logger logger = LogManager.getLogger(MusicTracksPanelProvider.class.getName());

    private final FtpCredentialsHelper myFtpCredentialsHelper;
    private final IFTPOperationListener ftpOperationListener;

    private final Node myNode;

    public MusicTracksPanelProvider(FtpCredentialsHelper ftpCredentialsHelper, IFTPOperationListener listener) {
        ftpOperationListener = listener;
        myFtpCredentialsHelper = ftpCredentialsHelper;

        VBox allRows = new VBox();
        allRows.setSpacing(10);
        allRows.setPadding(new Insets(6));

        allRows.getChildren().addAll(makeRow1());

        TitledPane p = new TitledPane(Loc.get("music.panel"), allRows);
        p.setExpanded(true);
        p.setAnimated(false);
        p.setCollapsible(false);
        myNode = p;
    }

    private HBox makeRow1() {
        HBox row1 = new HBox();

        Label musicTracksLabel = new Label(Loc.getLabel("music_tracks_upload.label"));
        Button musicTracksButton = new Button(Loc.get("upload.button"));
        musicTracksButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                MusicOperationsHelper.doMusicTracksUpload(ftpOperationListener, myFtpCredentialsHelper);
            }
        });

        row1.getChildren().addAll(musicTracksLabel, musicTracksButton);
        return row1;
    }

    @Override
    public Node getNode() {
        return myNode;
    }

}