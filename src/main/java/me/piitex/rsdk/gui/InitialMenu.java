package me.piitex.rsdk.gui;

import javafx.scene.paint.Color;
import me.piitex.engine.Container;
import me.piitex.engine.DisplayOrder;
import me.piitex.engine.containers.EmptyContainer;
import me.piitex.engine.loaders.FontLoader;
import me.piitex.engine.overlays.BoxOverlay;
import me.piitex.engine.overlays.ButtonOverlay;
import me.piitex.engine.overlays.TextFlowOverlay;
import me.piitex.rsdk.RSDK;

public class InitialMenu {
    private final RSDK rsdk;

    public InitialMenu(RSDK rsdk) {
        this.rsdk = rsdk;
    }

    public Container build() {
        Container container = new EmptyContainer(1200, 900);

        BoxOverlay boxOverlay = new BoxOverlay(1200, 900, Color.GRAY);
        boxOverlay.setOrder(DisplayOrder.LOW);

        container.addOverlay(boxOverlay);

        FontLoader textFont = new FontLoader("Roboto-Regular.ttf", 18);
        FontLoader buttonFont = new FontLoader("Roboto-Black.ttf", 28);

        TextFlowOverlay textFlowOverlay = new TextFlowOverlay("Welcome to RSDK-GUI. This is an automated deployment tool used for creating RenJava projects. Please ensure you are connected to the internet as this is not an offline installer. The only connections that are made are to the RenJava github page. This connection is needed to clone the repository and install it." +
                "The only data collected is the data from GitHub. This tool does not collect or use data. \n\nPlease press 'create' when you  are ready.", 1000, 600);
        textFlowOverlay.setFont(textFont);
        textFlowOverlay.setX(100);
        textFlowOverlay.setY(150);

        ButtonOverlay create = new ButtonOverlay("create", "Create", Color.LIMEGREEN);
        create.onClick(event -> {
            rsdk.getWindow().clearContainers();

            Container c = new ProjectMenu(rsdk).build();
            rsdk.getWindow().addContainer(c);
            rsdk.getWindow().render();
        });
        create.setFont(buttonFont);
        create.setX(350);
        create.setY(400);

        container.addOverlays(textFlowOverlay, create);
        return container;
    }
}
