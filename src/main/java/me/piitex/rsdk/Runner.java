package me.piitex.rsdk;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import me.piitex.engine.layouts.VerticalLayout;
import me.piitex.engine.loaders.FontLoader;
import me.piitex.engine.overlays.ButtonOverlay;
import me.piitex.engine.overlays.TextOverlay;
import me.piitex.rsdk.utils.Git;
import me.piitex.rsdk.utils.ResourceUtil;
import me.piitex.rsdk.utils.Tasks;
import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Runner {
    private final VerticalLayout console;
    private final RSDK rsdk;
    private final File directory;
    private final FontLoader conFont = new FontLoader("Roboto-Regular.ttf", 12);

    public Runner(VerticalLayout console, RSDK rsdk, File directory) {
        this.console = console;
        this.rsdk = rsdk;
        this.directory = directory;

        extractTemplate();
    }

    public void extractTemplate() {
        System.out.println("Extracting RenJava starting template...");

        // Input to Console
        Tasks.runJavaFXThread(() -> {
            TextOverlay update = new TextOverlay("Extracting template to " + rsdk.getDirectory().getAbsolutePath() + "...");
            update.setX(-200);
            update.setFont(conFont);
            console.addOverlay(update);
            rsdk.getWindow().render();
        });

        Tasks.runAsync(() -> {
            ResourceUtil.extractFromResources("src", directory);
            ResourceUtil.extractFromResources("pom", directory);
            ResourceUtil.extractFromResources("mvnw", directory);
            ResourceUtil.extractFromResources(".mvn", directory);

            // Once extracted, rename the package scheme.
            // template/renjava
            File main = new File(directory, "/src/main/java/template/renjava/Main.java");
            if (!main.exists()) {
                System.out.println("ERROR! Main does not exist!");
            }

            File menu = new File(directory, "/src/main/java/template/renjava/GameMenu.java");
            if (!menu.exists()) {
                System.out.println("ERROR! Menu does not exist!");
            }

            File pom = new File(directory, "pom.xml");
            if (!pom.exists()) {
                System.out.println("ERROR! Pom does not exist!");
            }

            // Inside of src/main/java/ create the group id
            String group = rsdk.getGroupID().replace(".", "/");
            System.out.println("Group: " + group);
            File srcDir = new File(directory, "/src/main/java/");
            File newDir = new File(srcDir, group + "/");
            newDir.mkdirs();


            // Re-write files with formatted data
            try {
                List<String> lines = FileUtils.readLines(main, StandardCharsets.UTF_8);
                lines = format(lines);
                FileWriter writer = new FileWriter(new File(newDir, main.getName()));
                for (String s : lines) {
                    writer.write(s + "\n");
                }
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                List<String> lines = FileUtils.readLines(menu, StandardCharsets.UTF_8);
                lines = format(lines);
                FileWriter writer = new FileWriter(new File(newDir, menu.getName()));
                for (String s : lines) {
                    writer.write(s + "\n");
                }
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                List<String> lines = FileUtils.readLines(pom, StandardCharsets.UTF_8);
                lines = format(lines);
                FileWriter writer = new FileWriter(pom);
                for (String s : lines) {
                    writer.write(s + "\n");
                }
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Input to Console
            Tasks.runJavaFXThread(() -> {
                TextOverlay update = new TextOverlay("Finished extraction");
                update.setX(-200);
                update.setFont(conFont);
                console.addOverlay(update);
                rsdk.getWindow().render();
            });

            // Input to Console
            Tasks.runJavaFXThread(() -> {
                TextOverlay update = new TextOverlay("Cloning RenJava...");
                update.setX(-200);
                update.setFont(conFont);
                console.addOverlay(update);
                rsdk.getWindow().render();
            });

            cloneRenJava();
        });
    }

    private List<String> format(List<String> lines) {
        List<String> toReturn = new ArrayList<>();

        for (String s : lines) {
            s = s.replace("template.renjava", rsdk.getGroupID());
            s = s.replace("<groupId>template.renjava</groupId>", "<groupId>" + rsdk.getGroupID() + "</groupId>");
            s = s.replace("<artifactId>renjava-template</artifactId>", "<artifactId>" + rsdk.getArtifact() + "</artifactId>");
            s = s.replace("<version>1.0-SNAPSHOT</version>", "<version>" + rsdk.getVersion() + "</version>");
            s = s.replace("Template Project", rsdk.getArtifact());
            s = s.replace("0.0.0", rsdk.getVersion());

            toReturn.add(s);
        }

        return toReturn;
    }

    public void cloneRenJava() {
        // Clone RenJava and run a clean install using mvnw
        File renDir = new File(directory, "/RenJava/");
        renDir.mkdir();

        try {
            Git.gitClone(renDir.toPath(), "https://github.com/HackusatePvP/RenJava");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        Tasks.runJavaFXThread(() -> {
            TextOverlay update = new TextOverlay("Finished clone! Extracting mvnw...");
            update.setX(-200);
            update.setFont(conFont);
            console.addOverlay(update);
            rsdk.getWindow().render();
        });

        // Pull information from RenJava pom. We need the artifact, group, and version
        File renPom = new File(renDir, "pom.xml");
        if (!renPom.exists()) {
            System.out.println("ERROR: Could not find RenJava pom.");
        } else {
            System.out.println("RenPom found!");
        }

        String renID = "";
        String renArtifact = "";
        String renVersion = "";

        try {
            List<String> lines = FileUtils.readLines(renPom, StandardCharsets.UTF_8);
            for (String s : lines) {
                if (s.contains("<groupId>") && renID.isEmpty()) {
                    System.out.println("Setting group id...");
                    renID = s;
                } else if (s.contains("<artifactId>") && renArtifact.isEmpty()) {
                    System.out.println("Setting artifact...");
                    renArtifact = s;
                } else if (s.contains("<version>") && renVersion.isEmpty()) {
                    System.out.println("Setting version...");
                    renVersion = s;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Once the RenInfo is retrieved re-write the project pom...
        if (!renID.isEmpty() && !renArtifact.isEmpty() && !renVersion.isEmpty()) {
            System.out.println("Found RenJava info...");
            File pom = new File(directory, "pom.xml");
            try {
                List<String> lines = FileUtils.readLines(pom, StandardCharsets.UTF_8);

                FileWriter writer = new FileWriter(pom);
                boolean depend = false;
                for (String s : lines) {
                    if (s.contains("<dependencies>")) {
                        depend = true;
                    }

                    if (depend) {
                        s = s.replace("<groupId>me.piitex</groupId>", renID);
                        s = s.replace("<artifactId>RenJava</artifactId>", renArtifact);
                        s = s.replace("<version>0.1.235-beta</version>", renVersion);
                    }

                    writer.write(s + "\n");
                }

                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        // Extract maven wrapper to RenJava
        ResourceUtil.extractFromResources("mvnw", renDir);
        ResourceUtil.extractFromResources(".mvn", renDir);

        // Run mvnw
        Tasks.runJavaFXThread(() -> {
            TextOverlay update = new TextOverlay("Running mvnw clean install...");
            update.setX(-200);
            update.setFont(conFont);
            console.addOverlay(update);
            rsdk.getWindow().render();
        });

        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "mvnw clean install");
        builder.directory(renDir);
        builder.redirectErrorStream(true);

        Process process;
        try {
            process = builder.start();
            String result = new String(process.getInputStream().readAllBytes());

            // Run mvnw
//            Tasks.runJavaFXThread(() -> {
//                TextOverlay update = new TextOverlay(result);
//                update.setX(-200);
//                update.setFont(conFont);
//                console.addOverlay(update);
//                rsdk.getWindow().render();
//            });

            process.waitFor();

            // Delete RenJava directory
            ResourceUtil.deleteDirectory(renDir);
            createEnvironment();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void createEnvironment() {
        Tasks.runJavaFXThread(() -> {
            TextOverlay update = new TextOverlay("Creating development environment...");
            update.setX(-200);
            update.setFont(conFont);
            console.addOverlay(update);
            rsdk.getWindow().render();
        });

        // Delete old template
        File template = new File(directory, "/src/main/java/template/");
        ResourceUtil.deleteDirectory(template);

        File environment = new File(directory, rsdk.getArtifact() + "-" + rsdk.getVersion() + "/");
        environment.mkdir();
        File renjava = new File(environment, "/renjava/");
        renjava.mkdir();
        File game = new File(environment, "/game/");
        game.mkdir();
        File css = new File(game, "/css/");
        css.mkdir();
        File images = new File(game, "/images/");
        images.mkdir();
        File gui = new File(images, "/gui/");
        gui.mkdir();

        Tasks.runJavaFXThread(() -> {
            TextOverlay update = new TextOverlay("Extracting gui...");
            update.setX(-200);
            update.setFont(conFont);
            console.addOverlay(update);
            rsdk.getWindow().render();
        });

        List<String> remove = new ArrayList<>();
        remove.add(rsdk.getTheme() + "/");
        remove.add(rsdk.getColor() + "/");
        ResourceUtil.extractFromResources("gui/" + rsdk.getTheme() + "/" + rsdk.getColor(), remove, images);

        Tasks.runJavaFXThread(() -> {
            TextOverlay update = new TextOverlay("Extracting css...");
            update.setX(-200);
            update.setFont(conFont);
            console.addOverlay(update);
            rsdk.getWindow().render();
        });

        ResourceUtil.extractFromResources("css/", game);

        Tasks.runJavaFXThread(() -> {
            TextOverlay update = new TextOverlay("Creating project jar file...");
            update.setX(-200);
            update.setFont(conFont);
            console.addOverlay(update);
            rsdk.getWindow().render();
        });

        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "mvnw clean install");
        builder.directory(directory);
        builder.redirectErrorStream(true);
        try {
            Process process = builder.start();

            process.waitFor();

            Tasks.runJavaFXThread(() -> {
                TextOverlay update = new TextOverlay("Finished executing jar. Verifying target...");
                update.setX(-200);
                update.setFont(conFont);
                console.addOverlay(update);
                rsdk.getWindow().render();
            });

            File target = new File(directory, "/target/");
            if (!target.exists()) {
                Tasks.runJavaFXThread(() -> {
                    TextOverlay update = new TextOverlay("ERROR: Target does not exist. You will have to manually build jar.");
                    update.setTextFill(Color.RED);
                    update.setX(-200);
                    update.setFont(conFont);
                    console.addOverlay(update);
                    rsdk.getWindow().render();
                });
            } else {
                System.out.println("Scanning jars...");
                for (File file : target.listFiles()) {
                    if (!file.getName().contains("original") && file.getName().endsWith(".jar")) {
                        System.out.println("Found file!");
                        Tasks.runJavaFXThread(() -> {
                            TextOverlay update = new TextOverlay("Target Jar: " + file.getAbsolutePath());
                            update.setX(-200);
                            update.setFont(conFont);
                            console.addOverlay(update);
                            rsdk.getWindow().render();

                            update = new TextOverlay("Creating development scripts..." + file.getAbsolutePath());
                            update.setX(-200);
                            update.setFont(conFont);
                            console.addOverlay(update);
                            rsdk.getWindow().render();
                        });

                        downloadJDKS(environment);

                        createScripts(environment, file);

                        break;
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void downloadJDKS(File environment) {
        Tasks.runJavaFXThread(() -> {
            TextOverlay update = new TextOverlay("Downloading Windows JDK...");
            update.setX(-200);
            update.setFont(conFont);
            console.addOverlay(update);
            rsdk.getWindow().render();
        });

        File jdk = new File(environment, "/jdk/");
        jdk.mkdir();

        File windowsFile = new File(jdk, "amazon-corretto-17-x64-windows-jdk.zip");
        windowsFile.delete();
        if (!windowsFile.exists() || new File(windowsFile, "jdk/windows/").listFiles() != null && new File(windowsFile, "/jdk/windows/").listFiles().length > 0) {
            try (BufferedInputStream in = new BufferedInputStream(new URL("https://corretto.aws/downloads/latest/amazon-corretto-21-x64-windows-jdk.zip").openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(new File(jdk, "amazon-corretto-17-x64-windows-jdk.zip"))) {
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            } catch (IOException e) {
                // handle exception
            }
        }

        Tasks.runJavaFXThread(() -> {
            TextOverlay update = new TextOverlay("Unzipping Windows JDK...");
            update.setX(-200);
            update.setFont(conFont);
            console.addOverlay(update);
            rsdk.getWindow().render();
        });

        File windowsJDK = new File(jdk, "/windows/");
        windowsJDK.mkdir();

        ZipUnArchiver zipUnArchiver = new ZipUnArchiver();
        zipUnArchiver.setSourceFile(windowsFile);
        zipUnArchiver.setDestDirectory(windowsJDK);
        zipUnArchiver.extract();
        System.out.println("Finished unzip.");
        System.out.println("Cleaning up...");

        String extractedDirectoryName = windowsJDK.list((dir, name) -> name.startsWith("amazon") || name.startsWith("jdk"))[0];
        System.out.println("Detected directory: " + extractedDirectoryName);
        try {
            ResourceUtil.copyDirectory(new File(windowsJDK, extractedDirectoryName), windowsJDK);
            ResourceUtil.deleteDirectory(new File(windowsJDK, extractedDirectoryName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Tasks.runJavaFXThread(() -> {
            TextOverlay update = new TextOverlay("Downloading Linux JDK...");
            update.setX(-200);
            update.setFont(conFont);
            console.addOverlay(update);
            rsdk.getWindow().render();
        });


        File jdkDirectory = new File(jdk, "jdk/linux/");
        jdkDirectory.mkdir();

        File linuxFile = new File(jdkDirectory, "amazon-corretto-21-x64-linux-jdk.deb");
        if (!linuxFile.exists()) {
            System.out.println("Downloading...");
            try (BufferedInputStream in = new BufferedInputStream(new URL("https://corretto.aws/downloads/latest/amazon-corretto-21-x64-linux-jdk.deb").openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(linuxFile)) {
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            } catch (IOException e) {
                // handle exception
                System.out.println("Could not download: " + e.getMessage());
            }
        } else {
            System.out.println("Linux file exists. TODO: Check to see if the file is completely downloaded.");
        }

    }

    public void createScripts(File environment, File targetFile) {
        File startBat = new File(environment, "start.bat");
        try {
            FileWriter writer = new FileWriter(startBat);
            writer.write("start jdk\\windows\\bin\\javaw.exe -jar " + targetFile.getName());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File debugBat = new File(environment, "debug.bat");
        try {
            FileWriter writer = new FileWriter(debugBat);
            writer.write("\"jdk\\windows\\bin\\java.exe\" -jar " + targetFile.getName());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File startLinux = new File(environment, "start_linux.sh");
        try {
            FileWriter writer = new FileWriter(startLinux, false);
            writer.write("if which java > /dev/null 2>&1; then\n");
            writer.write("  echo \"Java Installed.\"\n");
            writer.write("else\n");
            writer.write("  echo \"Not Installed.\"\n");
            writer.write("  sudo apt install ./jdk/linux/amazon-corretto-21-x64-linux-jdk.deb\n");
            writer.write("fi\n");
            writer.write("  java -jar " + targetFile.getName());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File debugSH = new File(environment, "debug.sh");
        try {
            FileWriter writer = new FileWriter(debugSH, false);
            writer.write("java -jar " + targetFile.getName());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File jdk = new File(environment, "/jdk/");

        File installLinux = new File(jdk, "install_linux_64.sh");
        try {
            FileWriter writer = new FileWriter(installLinux, false);
            writer.write("# To install JDK 21 on Debian distro x64\n");
            writer.write("# Get repo and key\n");
            writer.write("wget -O - https://apt.corretto.aws/corretto.key | sudo gpg --dearmor -o /usr/share/keyrings/corretto-keyring.gpg && \\\n");
            writer.write("echo \"deb [signed-by=/usr/share/keyrings/corretto-keyring.gpg] https://apt.corretto.aws stable main\" | sudo tee /etc/apt/sources.list.d/corretto.list\n");
            writer.write("# Install Amazon JDK 21\n");
            writer.write("apt-get update; apt-get install -y java-21-amazon-corretto-jdk\n");
            writer.write("# Verify installation\n");
            writer.write("java --version\n");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File installLinuxLocal = new File(jdk, "install_linux_local_64.sh");
        try {
            FileWriter writer = new FileWriter(installLinuxLocal, false);
            writer.write("sudo apt install /jdk/linux/amazon-corretto-21-x64-linux-jdk.deb");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Finally copy target file into environment
        File newTar = new File(environment, targetFile.getName());
        try {
            FileUtils.copyFile(targetFile, newTar);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Run application
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "start.bat");
        builder.directory(environment);
        builder.redirectErrorStream(true);

        try {
            builder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Add Done button
        Tasks.runJavaFXThread(() -> {
            TextOverlay update = new TextOverlay("Finished runner!");
            update.setX(-200);
            update.setFont(conFont);
            console.addOverlay(update);
            rsdk.getWindow().render();

            ButtonOverlay done = new ButtonOverlay("done", "Done", Color.LIMEGREEN);
            done.onClick(event -> {
                Platform.exit();
            });
            done.setFont(new FontLoader("Roboto-Black.ttf", 26));
            done.setX(750);
            done.setY(400);
            rsdk.getWindow().getContainers().getFirst().addOverlays(done);
            rsdk.getWindow().render();
        });
    }
}
