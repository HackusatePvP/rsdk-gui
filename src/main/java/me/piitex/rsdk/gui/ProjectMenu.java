package me.piitex.rsdk.gui;

import javafx.scene.paint.Color;
import me.piitex.engine.Container;
import me.piitex.engine.DisplayOrder;
import me.piitex.engine.containers.EmptyContainer;
import me.piitex.engine.layouts.VerticalLayout;
import me.piitex.engine.loaders.FontLoader;
import me.piitex.engine.overlays.BoxOverlay;
import me.piitex.engine.overlays.ButtonOverlay;
import me.piitex.engine.overlays.InputFieldOverlay;
import me.piitex.engine.overlays.TextOverlay;
import me.piitex.rsdk.RSDK;

public class ProjectMenu {
    private final RSDK rsdk;

    public ProjectMenu(RSDK rsdk) {
        this.rsdk = rsdk;
    }

    public Container build() {
        Container container = new EmptyContainer(1200, 900);

        BoxOverlay boxOverlay = new BoxOverlay(1200, 900, Color.GRAY);
        boxOverlay.setOrder(DisplayOrder.LOW);

        container.addOverlay(boxOverlay);

        FontLoader textFont = new FontLoader("Roboto-Regular.ttf", 24);
        FontLoader buttonFont = new FontLoader("Roboto-Black.ttf", 28);

        VerticalLayout root = new VerticalLayout(1200, 900);
        root.setSpacing(100);
        root.setX(200);
        root.setY(150);

        VerticalLayout nameBox = new VerticalLayout(1200, 300);
        root.addChildLayout(nameBox);

        TextOverlay name = new TextOverlay("Please type the name of your project. Do not use spaces.");
        name.setFont(textFont);
        nameBox.addOverlay(name);

        InputFieldOverlay nameInput = new InputFieldOverlay("MyGame", "MyGame", 0, 0, 100, 30);
        nameInput.onInputSetEvent(event -> {
            System.out.println("Project:" + event.getInput());
            rsdk.setArtifact(event.getInput());
        });
        nameInput.setFont(textFont);
        nameBox.addOverlay(nameInput);

        VerticalLayout groupBox = new VerticalLayout(1200, 300);
        root.addChildLayout(groupBox);

        TextOverlay group = new TextOverlay("Please type the packaging scheme (groupID). Instead of spaces use dots '.'");
        group.setFont(textFont);
        groupBox.addOverlay(group);

        InputFieldOverlay groupInput = new InputFieldOverlay("me.youralias", "me.youralias", 0, 0, 100, 30);
        groupInput.onInputSetEvent(event -> {
            rsdk.setGroupID(event.getInput());
        });
        groupInput.setFont(textFont);
        groupBox.addOverlay(groupInput);

        VerticalLayout versionBox = new VerticalLayout(1200, 300);
        root.addChildLayout(versionBox);

        TextOverlay version = new TextOverlay("Please type the initial version of your game.");
        version.setFont(textFont);
        versionBox.addOverlay(version);

        InputFieldOverlay versionInput = new InputFieldOverlay("1.0.0", "1.0.0", 0, 0, 100, 30);
        versionInput.onInputSetEvent(event -> {
            rsdk.setVersion(event.getInput());
        });
        versionInput.setFont(textFont);
        versionBox.addOverlay(versionInput);

        ButtonOverlay next = new ButtonOverlay("next-1", "Next", Color.LIMEGREEN);
        next.onClick(event -> {
            TextOverlay error;
            if (rsdk.getArtifact().contains(" ")) {
                // ERROR It has a space
                System.out.println("Artifact has space:" + rsdk.getArtifact());
                error = new TextOverlay("Project name cannot contain spaces.");
            } else if (rsdk.getGroupID().contains(" ")) {
                error = new TextOverlay("Group ID/Packaging cannot contain spaces.");
            } else if (rsdk.getVersion().contains(" ")) {
                error = new TextOverlay("Version cannot contain spaces.");
            } else if (rsdk.getArtifact().isEmpty()) {
                System.out.println("Project is empty:" + rsdk.getArtifact());

                error = new TextOverlay("Project name must be set.");
            } else if (rsdk.getVersion().isEmpty()) {
                error = new TextOverlay("Version must be set.");
            } else if (rsdk.getGroupID().isEmpty()) {
                error = new TextOverlay("Group ID/Packaging must be set.");
            } else {
                // Passed
                error = null;

                Container c = new ColorMenu(rsdk).build();
                rsdk.getWindow().clearContainers();
                rsdk.getWindow().addContainer(c);
                rsdk.getWindow().render();
            }

            if (error != null) {
                error.setFont(textFont);
                error.setTextFill(Color.RED);
                error.setX(400);
                error.setY(700);
                System.out.println("Overlay size: " + container.getOverlaySize());
                if (container.getOverlaySize() > 2) {
                    container.removeOverlay(container.getOverlays().getLast());
                }

                rsdk.getWindow().getContainers().getFirst().addOverlays(error);

                rsdk.getWindow().render();
            }
        });
        next.setFont(buttonFont);
        next.setX(1000);
        next.setY(800);

        container.addOverlays(next);

        container.addLayout(root);
        return container;
    }
}
