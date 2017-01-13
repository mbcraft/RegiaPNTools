/*
 * 
 *    Copyright MBCRAFT di Marco Bagnaresi - © 2015
 *    All rights reserved - Tutti i diritti riservati
 * 
 *    Mail : info [ at ] mbcraft [ dot ] it 
 *    Web : http://www.mbcraft.it
 * 
 */

package it.mbcraft.regiapn.tools;

import it.mbcraft.libraries.ui.common.WindowStack;
import it.mbcraft.regiapn.tools.operations.FtpCredentialsHelper;
import it.mbcraft.regiapn.tools.ui.strings.Loc;
import it.mbcraft.regiapn.tools.ui.panels.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public class Main extends Application {
    
    private static final String VERSION = "1.2.5";

    public static final String ENV_FILE = "env.ini";
    public static final String CONFIG_FILE = "config.ini";

    private static final Logger logger = LogManager.getLogger(Main.class);

    private MenuBarPanelProvider menuBarProvider;
    private GlobalConfigurationPanelProvider globalConfig;
    private MusicTracksPanelProvider musicTracks;
    private CustomersFoldersPanelProvider customersFolders;
    private PlayerDeploymentPanelProvider playerDeployment;
    private OperationLogPanelProvider operationLog;
    
    @Override
    public void start(Stage primaryStage) {
        
        WindowStack.push(primaryStage);

        initPanelProviders(primaryStage);

        setupAndShow(primaryStage);



    }

    private void initPanelProviders(Stage primaryStage) {

        MenuBar mb = new MenuBar();
        mb.getMenus().addAll(new Menu("File"));

        operationLog = new OperationLogPanelProvider();

        playerDeployment = new PlayerDeploymentPanelProvider(operationLog.getDeployLogWriter());

        globalConfig = new GlobalConfigurationPanelProvider(primaryStage,playerDeployment.getDeployingFlagProperty(),operationLog.getConfigLogWriter());
        musicTracks = new MusicTracksPanelProvider(globalConfig.getCredentialsHelper(),operationLog.getFTPLogWriter());
        customersFolders = new CustomersFoldersPanelProvider(globalConfig.getCredentialsHelper(),operationLog.getFTPLogWriter());

        menuBarProvider = new MenuBarPanelProvider(operationLog.getGetCheckForUpdatesOperationListener(),globalConfig.getCredentialsHelper());

    }

    private Node makeMainContentPanel() {
        VBox mainContent = new VBox();

        mainContent.getChildren().add(menuBarProvider.getNode());

        mainContent.getChildren().add(globalConfig.getNode());
        mainContent.getChildren().add(musicTracks.getNode());
        mainContent.getChildren().add(customersFolders.getNode());
        mainContent.getChildren().add(playerDeployment.getNode());
        mainContent.getChildren().add(operationLog.getNode());

        return mainContent;
    }

    private Node makeBottomCopyrightPanel() {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.BASELINE_LEFT);
        hbox.setPadding(new Insets(6));

        Label copyright = new Label("Copyright © MBCRAFT di Marco Bagnaresi - All rights reserved.");
        copyright.setAlignment(Pos.BASELINE_CENTER);
        copyright.setCursor(Cursor.HAND);
        copyright.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    Desktop.getDesktop().browse(new URI("http://www.mbcraft.it"));
                } catch (IOException | URISyntaxException e) {
                    logger.catching(e);
                }
            }
        });

        hbox.getChildren().add(copyright);

        return hbox;
    }

    private Scene initScene() {
        BorderPane border = new BorderPane();

        border.setCenter(makeMainContentPanel());
        border.setBottom(makeBottomCopyrightPanel());

        return new Scene(border, 800, 700);
    }

    private void setupAndShow(Stage primaryStage) {
        Scene scene = initScene();

        primaryStage.setTitle(Loc.get("main_window.title")+" - "+VERSION);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMaxHeight(700);

        primaryStage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
