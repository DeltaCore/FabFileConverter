package net.ccmob.fabfileconverter.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import net.ccmob.fabfileconverter.FabFileConverter.Core;

public class SettingsReader {

	String	         sFileName	= "";
	FileInputStream	 fInput;
	FileOutputStream	fOutput;
	File	           sFile;
	Properties	     properties;
	
	public SettingsReader(String settingsFile) throws IOException {
		this.sFileName = settingsFile;
		sFile = new File(settingsFile);
		properties = new Properties();
		if (!sFile.exists()) {
			setupSettings();
			fInput = new FileInputStream(sFile);
		} else {
			fInput = new FileInputStream(sFile);
			initSettings();
		}
	}
	
	public String get(String node, String def) {
		String ret = properties.getProperty(node);
		// System.out.println("Property " + node + " => " + ret);
		if (ret == null) {
			// System.out.println("Setting up new property => " + def);
			set(node, def);
			ret = properties.getProperty(node);
			// System.out.println("Property " + node + " => " + ret);
		}
		return ret;
	}

	public void initSettings() throws IOException {
		properties.loadFromXML(fInput);
	}

	public void setupSettings() throws IOException {
		fOutput = new FileOutputStream(sFile);
		properties.setProperty("showGuiOnCommandLine", "true");
		properties.setProperty("showDebugConsole", "true");
		properties.setProperty("version", String.valueOf(Core.version));
		properties.setProperty("lastFolder", System.getProperty("user.home"));
		properties.storeToXML(fOutput, "FabFileConverter settings file");
	}

	public void set(String node, String value) {
		properties.setProperty(node, value);
		save();
	}

	public boolean getBoolean(String node) {
		String arg = get(node, "false");
		if (arg.matches("[tT]rue")) {
			return true;
		} else {
			return false;
		}
	}

	public void save() {
		System.out.println("Saving properties...");
		try {
			fOutput = new FileOutputStream(sFile);
			properties.storeToXML(fOutput, "FabFileConverter settings file");
		} catch (Exception e) {
			System.out.println("S*** ! look :");
			if (getBoolean("showDebugConsole"))
				e.printStackTrace();
		}
	}

}
