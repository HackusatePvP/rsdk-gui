package me.piitex.rsdk;

import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import me.piitex.engine.Container;
import me.piitex.engine.Window;
import me.piitex.engine.layouts.VerticalLayout;
import me.piitex.rsdk.gui.InitialMenu;

import java.io.File;

public class RSDK {
    // Store application data
    private String artifact;
    private String groupID;
    private String version;
    private String release;
    private String theme;
    private String color;
    private File directory;

    private Window window;

    private VerticalLayout console;
    public static RSDK rsdk;

    public RSDK() {
        rsdk = this;

        window = new Window("RSDK GUI", StageStyle.DECORATED, null, 1200, 900);
        window.getStage().setResizable(false);
        Container initial = new InitialMenu(this).build();
        window.updateBackground(Color.WHITE);
        window.addContainer(initial);
        window.render();
    }

    public Window getWindow() {
        return window;
    }

    public String getArtifact() {
        return artifact;
    }

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public void reset() {
        artifact = "";
        groupID = "";
        version = "";
        theme = "";
        color = "";
        directory = null;
    }

    public VerticalLayout getConsole() {
        return console;
    }

    public void setConsole(VerticalLayout console) {
        this.console = console;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }
}
