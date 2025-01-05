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

public class ColorMenu {
    private final RSDK rsdk;

    public ColorMenu(RSDK rsdk) {
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
        root.setSpacing(50);

        VerticalLayout themeBox = new VerticalLayout(1200, 300);
        root.addChildLayout(themeBox);

        TextOverlay theme = new TextOverlay("Please select a theme. Currently white is not supported.");
        theme.setFont(textFont);
        themeBox.addOverlay(theme);

        List<String> items = new ArrayList<>();
        items.add("Dark");
        items.add("White");

        ComboBoxOverlay themeSelect = new ComboBoxOverlay(items, 100, 20);
        themeSelect.onItemSelect(event -> {
            rsdk.setTheme(event.getItem().toLowerCase());
        });
        themeBox.addOverlay(themeSelect);

        VerticalLayout colorBox = new VerticalLayout(1200, 300);
        root.addChildLayout(colorBox);

        TextOverlay color = new TextOverlay("Please select a color.");
        color.setFont(textFont);
        colorBox.addOverlay(color);

        items = new ArrayList<>();
        items.add("Aqua");
        items.add("Blue");
        items.add("Cyan");
        items.add("Green");
        items.add("Light Blue");
        items.add("Orange");
        items.add("Pink");
        items.add("Purple");
        items.add("Red");
        items.add("Yellow");

        ComboBoxOverlay colorSelect = new ComboBoxOverlay(items, 100, 50);
        colorSelect.onItemSelect(event -> {
            rsdk.setColor(event.getItem().toLowerCase().replace(" ", "_"));
        });
        colorBox.addOverlay(colorSelect);

        container.addLayout(root);

        ButtonOverlay next = new ButtonOverlay("next-2", "Next", Color.LIMEGREEN);
        next.onClick(event -> {

            if (rsdk.getTheme().equalsIgnoreCase("white")) {

                if (container.getOverlaySize() > 3) {
                    container.removeOverlay(container.getOverlays().getLast());
                }

                TextOverlay error = new TextOverlay("White is not supported.", Color.RED, textFont);
                error.setX(400);
                error.setY(700);

                System.out.println("Overlay Size: " + container.getOverlaySize());

                container.addOverlay(error);

                rsdk.getWindow().render();
                return;
            }

            Container c = new DirectoryMenu(rsdk).build();
            rsdk.getWindow().clearContainers();
            rsdk.getWindow().addContainer(c);
            rsdk.getWindow().render();
        });
        next.setFont(buttonFont);
        next.setFont(buttonFont);
        next.setX(1000);
        next.setY(800);

        container.addOverlays(next);

        ButtonOverlay back = new ButtonOverlay("back-2", "Back", Color.LIMEGREEN);
        back.onClick(event -> {
            rsdk.setRelease("");
            rsdk.setColor("");
            rsdk.setTheme("");
            rsdk.getWindow().clearContainers();

            Container c = new RenJavaMenu(rsdk).build();
            rsdk.getWindow().addContainer(c);
            rsdk.getWindow().render();
        });
        back.setFont(buttonFont);
        back.setX(100);
        back.setY(800);

        container.addOverlays(back);

        return container;
    }
}
