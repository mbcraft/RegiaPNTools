package it.mbcraft.regiapn.tools.ui.panels;

import it.mbcraft.regiapn.tools.operations.FtpCredentialsHelper;
import it.mbcraft.regiapn.tools.ui.INodeProvider;
import it.mbcraft.regiapn.tools.config.ConfigHelper;
import it.mbcraft.regiapn.tools.ui.strings.Loc;
import it.mbcraft.regiapn.tools.operations.UpdatesHelper;
import it.mbcraft.regiapn.tools.operations.listeners.ICheckForUpdatesOperationListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import java.util.Properties;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 07/07/16.
 */
public class MenuBarPanelProvider implements INodeProvider {

    private final ICheckForUpdatesOperationListener listener;
    private final MenuBar menuBar;

    private final Menu fileMenu;

    public MenuBarPanelProvider(final ICheckForUpdatesOperationListener updateListener, final FtpCredentialsHelper ftpCredentialsHelper) {

        listener = updateListener;

        menuBar = new MenuBar();
        fileMenu = new Menu("File");
        menuBar.getMenus().add(fileMenu);

        MenuItem checkForUpdates = new MenuItem(Loc.get("menu.file.check_for_updates"));
        checkForUpdates.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Properties pt = ConfigHelper.readMainConfigs();
                UpdatesHelper.doCheckForUpdates(updateListener,pt,ftpCredentialsHelper);
            }
        });

        MenuItem quit = new MenuItem(Loc.get("menu.file.quit"));
        quit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.exit(0);
            }
        });

        fileMenu.getItems().addAll(checkForUpdates,quit);
    }

    @Override
    public Node getNode() {
        return menuBar;
    }
}
