package me.piitex.rsdk.gui;

import javafx.scene.paint.Color;
import me.piitex.engine.Container;
import me.piitex.engine.DisplayOrder;
import me.piitex.engine.containers.EmptyContainer;
import me.piitex.engine.layouts.VerticalLayout;
import me.piitex.engine.loaders.FontLoader;
import me.piitex.engine.overlays.BoxOverlay;
import me.piitex.engine.overlays.ButtonOverlay;
import me.piitex.engine.overlays.DirectoryOverlay;
import me.piitex.engine.overlays.TextOverlay;
import me.piitex.rsdk.RSDK;
import me.piitex.rsdk.Runner;

import java.io.File;

public class DirectoryMenu {
    private final RSDK rsdk;

    public DirectoryMenu(RSDK rsdk) {
        this.rsdk = rsdk;
    }

    public Container build() {
        Container container = new EmptyContainer(1200, 900);

        BoxOverlay boxOverlay = new BoxOverlay(1200, 900, Color.GRAY);
        boxOverlay.setOrder(DisplayOrder.LOW);

        container.addOverlay(boxOverlay);

        FontLoader textFont = new FontLoader("Roboto-Regular.ttf", 18);
        FontLoader buttonFont = new FontLoader("Roboto-Black.ttf", 28);

        VerticalLayout directoryBox = new VerticalLayout(1200, 900);
        directoryBox.setX(200);
        directoryBox.setY(150);

        TextOverlay directory = new TextOverlay("Please select the project directory. It must be an empty folder!");
        directory.setFont(textFont);
        directoryBox.addOverlay(directory);

        DirectoryOverlay dir = new DirectoryOverlay(rsdk.getWindow(), "Select a Folder");
        dir.onDirectorySelect(event -> {
            File file = event.getDirectory();
            if (file.listFiles() != null && file.listFiles().length > 0) {
                System.out.println("ERROR: Could not set directory!!!");
            } else {
                if (!file.exists())  {
                    file.mkdir();
                }
                rsdk.setDirectory(file);
            }

            ButtonOverlay next = new ButtonOverlay("next-3", "Next", Color.LIMEGREEN);
            next.onClick(event1 -> {
                // Start console runner
                ConsoleMenu consoleMenu = new ConsoleMenu();
                Container c = consoleMenu.build();
                rsdk.getWindow().clearContainers();
                rsdk.getWindow().addContainer(c);
                rsdk.getWindow().updateBackground(Color.WHITE);
                rsdk.getWindow().render();

                new Runner(consoleMenu.getLayout(), rsdk, rsdk.getDirectory());
            });
            next.setFont(buttonFont);
            next.setFont(buttonFont);
            next.setX(1000);
            next.setY(800);

            TextOverlay textOverlay = new TextOverlay(event.getDirectory().getAbsolutePath(), Color.LIMEGREEN, textFont);
            textOverlay.setX(200);
            textOverlay.setY(250);
            System.out.println("Size: " + container.getOverlaySize());
            if (container.getOverlaySize() > 5) {
                container.removeOverlay(container.getOverlays().getLast());
                container.removeOverlay(container.getOverlays().get(container.getOverlays().size() - 2));
            }

            container.addOverlays(next);
            container.addOverlays(textOverlay);

            rsdk.getWindow().render();
        });

        directoryBox.addOverlay(dir);

        container.addLayout(directoryBox);

        ButtonOverlay back = new ButtonOverlay("back-2", "Back", Color.LIMEGREEN);
        back.onClick(event -> {
            // Reset values
            rsdk.setDirectory(null);
            rsdk.setTheme("");
            rsdk.setColor("");

            rsdk.getWindow().clearContainers();

            Container c = new ProjectMenu(rsdk).build();
            rsdk.getWindow().addContainer(c);
            rsdk.getWindow().render();
        });

        back.setFont(buttonFont);
        back.setX(100);
        back.setY(800);

        return container;
    }
}
