package net.ccmob.fabfileconverter.FabFileConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import net.ccmob.fabfileconverter.converter.SettingsReader;
import net.ccmob.fabfileconverter.converter.gui.MainGui;

public class Updater {

	public final String	defaultBasePath	     = "http://www.alpha.ccmob.de/fabfileconverter/";
	public final String	defaultVersionFile	 = "lastversion.xml";
	public final String	defaultBotRemotePath	= "FabFileBot.jar";
	public String	      localFileName	       = "randomFilePleaseDoNotDeleteIt.xml";
	public String	      localBotName	       = System.getenv("temp") + File.separator + "FabFileBot.jar";
	
	SettingsReader	    settings;
	
	public String	      remoteFilePath	     = "";

	public Updater(SettingsReader settings) {
		this.settings = settings;
	}

	public void lookForUpdates() {
		System.out.println("Searching for updates ....");
		String cmds[] = checkForNewVersion();
		if (cmds.length > 1) {
			System.out.println("New version found : " + cmds[0]);
			System.out.println("Downloading FabFileBot.jar ...");
			String loadPath = settings.get("updateServer", defaultBasePath)
			    + settings.get("fabfilebot", defaultBotRemotePath);
			System.out.println("Remote location : " + loadPath);
			downloadFile(loadPath, localBotName);
			System.out.println("Done.");
			System.out.println("Starting slavebot for update.");
			File currentJavaJarFile = new File(MainGui.class.getProtectionDomain()
			    .getCodeSource().getLocation().getPath());
			String currentJavaJarFilePath = currentJavaJarFile.getAbsolutePath();
			if (currentJavaJarFilePath.contains(".jar")) {
				ProcessBuilder bot = new ProcessBuilder("java", "-jar", localBotName,
				    remoteFilePath, currentJavaJarFilePath);
				try {
					bot.start();
					System.out.println("Done.");
					System.exit(1);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Failed starting slavebot for update.");
				}
			} else {
				System.out
				    .println("Could not found executed jar file. Are you running this programm from Eclipse ?");
			}
		} else {
			System.out.println("No new version available.");
		}
	}

	public static String dir() {
		URI path;
		try {
			path = MainGui.class.getProtectionDomain().getCodeSource().getLocation()
			    .toURI();
			String name = MainGui.class.getPackage().getName() + ".jar";
			String path2 = path.getRawPath();
			path2 = path2.substring(1);

			if (path2.contains(".jar")) {
				path2 = path2.replace(name, "");
			}
			return path2;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String[] checkForNewVersion() {
		if (downloadFile(getUpdateServerFromConfig() + getVersionFileFromConfig(),
		    localFileName)) {
			try {
				return loadPropertiesFromFile(localFileName);
			} catch (Exception e) {
				e.printStackTrace();
				return new String[0];
			}
		} else {
			return new String[0];
		}
	}

	public boolean downloadFile(String url_, String localPath) {
		try {
			URL url = new URL(url_.replace(" ", "%20"));
			FileOutputStream fstream = new FileOutputStream(new File(localPath));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			int code = conn.getResponseCode();
			if (code == HttpURLConnection.HTTP_OK) {
				byte tmp_buffer[] = new byte[2048];
				InputStream stream = conn.getInputStream();
				int n;
				while ((n = stream.read(tmp_buffer)) > 0) {
					fstream.write(tmp_buffer, 0, n);
					fstream.flush();
				}
				fstream.close();
				return true;
			} else {
				System.out.println("HTTP Core : " + code);
				System.out.println("Error while loading information.");
				fstream.close();
				return false;
			}
		} catch (Exception e) {
			if (settings.getBoolean("showDebugConsole"))
				e.printStackTrace();
		}
		return false;
	}

	public String[] loadPropertiesFromFile(String localPath)
	    throws InvalidPropertiesFormatException, FileNotFoundException,
	    IOException {
		Properties file = new Properties();
		file.loadFromXML(new FileInputStream(new File(localPath)));
		if (file.getProperty("version") != null) {
			try {
				String newVersion = file.getProperty("version");
				String filePath = file.getProperty("remoteFilePath");
				double nV = Double.valueOf(newVersion);
				double aV = Double.valueOf(Core.version);
				if (nV > aV) {
					remoteFilePath = file.getProperty("remoteFilePath");
					new File(localPath).delete();
					String[] c = { newVersion, filePath };
					return (c);
				}
			} catch (Exception e) {
				return new String[0];
			}
		}
		return new String[0];
	}

	public String getVersionFileFromConfig() {
		return settings.get("updateServerVersionFile", defaultVersionFile);
	}

	public String getUpdateServerFromConfig() {
		return settings.get("updateServer", defaultBasePath);
	}

}
