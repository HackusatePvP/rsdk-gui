package me.piitex.rsdk.gui;

import javafx.scene.paint.Color;
import me.piitex.engine.Container;
import me.piitex.engine.DisplayOrder;
import me.piitex.engine.containers.EmptyContainer;
import me.piitex.engine.layouts.VerticalLayout;
import me.piitex.engine.loaders.FontLoader;
import me.piitex.engine.overlays.BoxOverlay;
import me.piitex.engine.overlays.ButtonOverlay;
import me.piitex.engine.overlays.ComboBoxOverlay;
import me.piitex.engine.overlays.TextOverlay;
import me.piitex.rsdk.RSDK;

import java.util.ArrayList;
import java.util.List;

public class RenJavaMenu {
    private final RSDK rsdk;

    public RenJavaMenu(RSDK rsdk) {
        this.rsdk = rsdk;
    }

    public Container build() {
        Container container = new EmptyContainer(1200, 900);

        BoxOverlay boxOverlay = new BoxOverlay(1200, 900, Color.GRAY);
        boxOverlay.setOrder(DisplayOrder.LOW);

        container.addOverlay(boxOverlay);

        FontLoader textFont = new FontLoader("Roboto-Regular.ttf", 18);
        FontLoader buttonFont = new FontLoader("Roboto-Black.ttf", 28);


        VerticalLayout root = new VerticalLayout(1200, 900);
        root.setX(200);
        root.setY(150);

        TextOverlay prompt = new TextOverlay("Please select the release type.", textFont);
        root.addOverlay(prompt);

        List<String> items = new ArrayList<>();
        items.add("Release");
        items.add("Experimental");
        ComboBoxOverlay release = new ComboBoxOverlay(items, 100, 50);
        release.onItemSelect(event -> {
            if (event.getItem().equalsIgnoreCase("release")) {
                rsdk.setRelease("master");
            } else {
                rsdk.setRelease(event.getItem().toLowerCase());
            }
        });

        root.addOverlay(release);

        ButtonOverlay next = new ButtonOverlay("next-1", "Next", Color.LIMEGREEN);
        next.onClick(event -> {
            Container c = new ColorMenu(rsdk).build();
            rsdk.getWindow().clearContainers();
            rsdk.getWindow().addContainer(c);
            rsdk.getWindow().render();
        });

        next.setFont(buttonFont);
        next.setX(1000);
        next.setY(800);

        ButtonOverlay back = new ButtonOverlay("back-2", "Back", Color.LIMEGREEN, buttonFont);
        back.onClick(event -> {
            // Reset values
            rsdk.setArtifact("");
            rsdk.setGroupID("");
            rsdk.setVersion("");
            rsdk.setTheme("");
            rsdk.setColor("");

            rsdk.getWindow().clearContainers();

            Container c = new ProjectMenu(rsdk).build();
            rsdk.getWindow().addContainer(c);
            rsdk.getWindow().render();
        });
        back.setX(100);
        back.setY(800);

        container.addOverlays(next, back);

        container.addLayout(root);

        return container;
    }

    public RSDK getRsdk() {
        return rsdk;
    }
}
