package it.mbcraft.regiapn.tools.ui.panels;

import it.mbcraft.libraries.net.ftp.IFTPOperationListener;
import it.mbcraft.regiapn.tools.operations.listeners.ICheckForUpdatesOperationListener;
import it.mbcraft.regiapn.tools.operations.listeners.IConfigOperationListener;
import it.mbcraft.regiapn.tools.ui.INodeProvider;
import it.mbcraft.regiapn.tools.ui.strings.Loc;
import it.mbcraft.regiapn.tools.operations.listeners.IDeployProgressListener;
import it.mbcraft.regiapn.tools.operations.listeners.impl.CheckForUpdatesLogWriter;
import it.mbcraft.regiapn.tools.operations.listeners.impl.ConfigLogWriter;
import it.mbcraft.regiapn.tools.operations.listeners.impl.DeployLogWriter;
import it.mbcraft.regiapn.tools.operations.listeners.impl.FTPLogWriter;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 *
 * Created by marco on 26/05/16.
 */
public class OperationLogPanelProvider implements INodeProvider {

    private final Node myNode;

    private final ICheckForUpdatesOperationListener myCheckForUpdatesListener;
    private final IConfigOperationListener myMiscListener;
    private final IFTPOperationListener myFTPListener;
    private final IDeployProgressListener myDeployListener;

    public OperationLogPanelProvider() {
        TextArea operationLogArea = new TextArea();
        operationLogArea.setEditable(false);
        operationLogArea.setWrapText(false);

        myCheckForUpdatesListener = new CheckForUpdatesLogWriter(operationLogArea);
        myFTPListener = new FTPLogWriter(operationLogArea);
        myMiscListener = new ConfigLogWriter(operationLogArea);
        myDeployListener = new DeployLogWriter(operationLogArea);

        TitledPane p = new TitledPane(Loc.get("operation_log.panel"), operationLogArea);
        p.setExpanded(true);
        p.setAnimated(false);
        p.setCollapsible(false);

        myNode = p;
    }

    @Override
    public Node getNode() {
        return myNode;
    }

    public ICheckForUpdatesOperationListener getGetCheckForUpdatesOperationListener() {
        return myCheckForUpdatesListener;
    }

    public IFTPOperationListener getFTPLogWriter() {
        return myFTPListener;
    }

    public IConfigOperationListener getConfigLogWriter() {
        return myMiscListener;
    }

    public IDeployProgressListener getDeployLogWriter() { return myDeployListener; }

}
