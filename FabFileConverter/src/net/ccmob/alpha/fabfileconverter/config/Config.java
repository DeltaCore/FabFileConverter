package net.ccmob.alpha.fabfileconverter.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Marcel Benning
 * @date 27.07.2014
 *
 */

public class Config {

	private File configFile;
	private ArrayList<String> configFileLines;
	private Pattern pattern;
	private Matcher matcher;
	
	public Config(String s){
		this(new File(s));
	}

	public Config(File f) {
		this.setConfigFile(f);
		if (!this.getConfigFile().exists()) {
			FileWriter writer;
			try {
				writer = new FileWriter(getConfigFile());
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.setConfigFileLines(new ArrayList<String>());
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					this.getConfigFile()));
			String line = "";
			while ((line = reader.readLine()) != null)
				this.getConfigFileLines().add(line);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return the configFile
	 */
	public File getConfigFile() {
		return configFile;
	}

	/**
	 * @param configFile
	 *            the configFile to set
	 */
	private void setConfigFile(File configFile) {
		this.configFile = configFile;
	}

	public void addDefault(String node, String value) {
		boolean flag = false;
		this.pattern = Pattern
				.compile("^<node=\"([-_a-zA-Z,._\\/0-9]{1,})\">([-a-zA-Z,._\\/0-9\\s]{1,})<\\/node>");
		String tmp = "";
		for (String line : this.getConfigFileLines()) {
			this.matcher = this.pattern.matcher(line);
			if (this.matcher.matches()) {
				tmp = this.matcher.group(1);
				if (tmp.equals(node)) {
					flag = true;
				}
			}
		}
		if (!flag) {
			this.getConfigFileLines().add(
					"<node=\"" + node + "\">" + value + "</node>");
		}
	}

	public void setValue(String node, String value) {
		boolean flag = false;
		this.pattern = Pattern
				.compile("^<node=\"([-_a-zA-Z,._\\/0-9]{1,})\">([-a-zA-Z,._\\/0-9\\s]{1,})<\\/node>");
		String tmp = "";
		String line = "";
		for (int i = 0; i < this.getConfigFileLines().size(); i++) {
			line = this.getConfigFileLines().get(i);
			this.matcher = this.pattern.matcher(line);
			if (this.matcher.matches()) {
				tmp = this.matcher.group(1);
				if (tmp.equals(node)) {
					flag = true;
					this.getConfigFileLines().set(i,
							"<node=\"" + node + "\">" + value + "</node>");
				}
			}
		}
		if (!flag) {
			this.getConfigFileLines().add(
					"<node=\"" + node + "\">" + value + "</node>");
		}
	}

	public String getValue(String node) {
		String val = null;
		this.pattern = Pattern
				.compile("^<node=\"([-a-zA-Z,._\\/0-9]{1,})\">([-a-zA-Z,._\\/0-9\\s]{1,})<\\/node>");
		for (String line : this.getConfigFileLines()) {
			this.matcher = this.pattern.matcher(line);
			if (this.matcher.matches()) {
				String n = this.matcher.group(1);
				if (n.equals(node)) {
					val = this.matcher.group(2);
				}
			}
		}
		return val;
	}

	/**
	 * @return the configFileLines
	 */
	private ArrayList<String> getConfigFileLines() {
		return configFileLines;
	}

	/**
	 * @param configFileLines
	 *            the configFileLines to set
	 */
	private void setConfigFileLines(ArrayList<String> configFileLines) {
		this.configFileLines = configFileLines;
	}

	public void save() {
		try {
			FileWriter writer = new FileWriter(this.getConfigFile());
			for (String line : this.getConfigFileLines()) {
				writer.write(line + String.format("%n"));
			}
			writer.close();
		} catch (IOException e) {
			System.err.println("Config file (" + this.getConfigFile().getAbsolutePath() + ") was unable to save cause of this exception : ");
			e.printStackTrace();
		}
	}

}
