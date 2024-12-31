package me.piitex.rsdk.gui;

import me.piitex.engine.Container;
import me.piitex.engine.containers.EmptyContainer;

import me.piitex.engine.layouts.VerticalLayout;
import me.piitex.engine.loaders.FontLoader;
import me.piitex.engine.overlays.TextOverlay;

public class ConsoleMenu {
    private VerticalLayout layout;

    public Container build() {
        Container container = new EmptyContainer(1200, 900);

        FontLoader textFont = new FontLoader("Roboto-Regular.ttf", 18);
        FontLoader conFont = new FontLoader("Roboto-Regular.ttf", 12);
        FontLoader buttonFont = new FontLoader("Roboto-Black.ttf", 28);

        TextOverlay info = new TextOverlay("Your project is being built. Below is the progress. Wait until a green done button appears.");
        info.setFont(textFont);
        info.setX(200);
        info.setY(100);

        container.addOverlay(info);

        layout = new VerticalLayout(500, 600);
        layout.setX(200);
        layout.setY(150);
        layout.setScroll(true);

        TextOverlay prompt = new TextOverlay("Starting runner...");
        prompt.setX(-100);
        prompt.setFont(conFont);

        layout.addOverlay(prompt);

        container.addLayout(layout);

        return container;
    }

    public VerticalLayout getLayout() {
        return layout;
    }
}
