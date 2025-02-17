package me.piitex.rsdk.utils;

import me.piitex.rsdk.exceptions.URLNotResolvedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Git {

	public static int gitInit(Path directory) throws IOException, InterruptedException {
		return runCommand(directory, "git", "init");
	}

	public static int gitStage(Path directory) throws IOException, InterruptedException {
		return runCommand(directory, "git", "add", "-A");
	}

	public static int gitCommit(Path directory, String message) throws IOException, InterruptedException {
		return runCommand(directory, "git", "commit", "-m", message);
	}

	public static int gitPush(Path directory) throws IOException, InterruptedException {
		return runCommand(directory, "git", "push");
	}

	public static int gitClone(Path directory, String originUrl) throws IOException, InterruptedException {
		return runCommand(directory.getParent(), "git", "clone", originUrl, directory.getFileName().toString());
	}

	public static int gitCloneBranch(String branch, Path directory, String originUrl) throws IOException, InterruptedException {
		return runCommand(directory.getParent(), "git", "clone", "--branch", branch, originUrl, directory.getFileName().toString());
	}

	public static int runCommand(Path directory, String... command) throws IOException, InterruptedException {
		Objects.requireNonNull(directory, "directory");
		if (!Files.exists(directory)) {
			throw new RuntimeException("can't run command in non-existing directory '" + directory + "'");
		}
		ProcessBuilder pb = new ProcessBuilder()
				.command(command)
				.directory(directory.toFile());
		Process p = pb.start();
		StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");
		StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");

		outputGobbler.start();
		errorGobbler.start();

		int exit = p.waitFor();
		errorGobbler.join();
		outputGobbler.join();

		// Stream exits
		int errExit = errorGobbler.exit;
		if (errExit == 1) {
			return errExit;
		}

        return exit;
    }

	private static class StreamGobbler extends Thread {

		private final InputStream is;
		private final String type;
		private int exit = 0;

		private StreamGobbler(InputStream is, String type) {
			this.is = is;
			this.type = type;
		}

		@Override
		public void run() {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
				String line;
				while ((line = br.readLine()) != null) {
					if (line.contains("Could not resolve host")) {
						URLNotResolvedException exception = new URLNotResolvedException(line);
						System.out.println(exception.getMessage());
						exit = 1;
						break;
					} else {
						System.out.println("Proper: " + line);
					}
				}
			} catch (IOException ioe) {
				System.out.println("Stack!");
				ioe.printStackTrace();
			}
		}


	}

}