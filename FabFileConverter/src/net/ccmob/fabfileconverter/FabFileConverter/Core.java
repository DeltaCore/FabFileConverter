package net.ccmob.fabfileconverter.FabFileConverter;

import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import net.ccmob.fabfileconverter.PluginUtils.PluginHandler;
import net.ccmob.fabfileconverter.converter.SettingsReader;
import net.ccmob.fabfileconverter.converter.gui.MainGui;
import net.ccmob.fabfileconverter.converter.reader.DrlConverter;
import net.ccmob.fabfileconverter.converter.reader.GmConverter;
import net.ccmob.fabfileconverter.converter.types.ConvertOption;
import net.ccmob.fabfileconverter.converter.types.FileEnd;
import net.ccmob.fabfileconverter.gui.GuiClass;

public class Core implements Runnable {

	public static final String	version				= "1.9";
	
	public String								showGui				= "showGuiOnCommandLine";
	public String								showDebug			= "showDebugConsole";
	GuiClass										gui;
	DrlConverter								drlconverter	= new DrlConverter();
	GmConverter									gmconverter		= new GmConverter();
	
	FileEnd											gm						= new FileEnd("gm", "ock", "^[gG][mM][1-8]$", gmconverter);
	FileEnd											g							= new FileEnd("g", "ock", "^[gG][1-8]$", gmconverter);
	FileEnd											drl						= new FileEnd("drl", "txt", "^[dL][rR][lL]$", drlconverter);
	
	ArrayList<FileEnd>					fileEnds			= new ArrayList<FileEnd>();
	MainGui											frame					= new MainGui(this, "file.is.not.selected");
	String											startArg			= "file.is.not.selected";
	public SettingsReader				settings;
	boolean											show					= false;
	public Updater							internalUpdater;
	
	public PluginHandler				pluginHandler	= new PluginHandler(this);
	
	public Core(String[] args) throws Exception { // XXX main : Working
		System.out.println("FabFileConverter V" + version + " Beta started.");
		System.out.println("Programm path : " + pluginHandler.getApplicationPath());
		for (int i = 0; i < args.length; i++) {
			System.out.println(" args[" + i + "] - " + args[i]);
		}
		settings = new SettingsReader(pluginHandler.getApplicationPath() + "settings.xml");
		checkDebugConsole();
		try {
			Thread.sleep(500);
		} catch (Exception e) {
			System.out.println("No sleeptime :(");
		}
		internalUpdater = new Updater(settings);
		registerFileEnding(gm);
		registerFileEnding(g);
		registerFileEnding(drl);
		pluginHandler.loadJars();
		String res = parseCommands(args);
		if (res.equalsIgnoreCase("proceed")) {
			// sleep(250);
			if (args.length > 0) {
				// sleep(250);
				if (settings.getBoolean(showGui)) {
					// sleep(250);
					startArg = args[0];
					frame = new MainGui(this, args[0]);
					frame.setVisible(true);
					frame.core = this;
					// sleep(1000);
					EventQueue.invokeLater(this);
					// sleep(500);
					show = true;
				} else {
					// sleep(250);
					FileEnd fend = checkFileEnd(args[0]);
					System.out.println("Fileend : " + fend.name);
					System.out.println("Converting " + args[0] + " to " + args[0].replace("." + fend.name, "." + fend.endName));
					fend.converter.readIn(args[0]);
					fend.converter.convert(args[0].replace("." + fend.name, "." + fend.endName), new ArrayList<ConvertOption>());
				}
			} else {
				frame.setVisible(true);
				frame.core = this;
				EventQueue.invokeLater(this);
			}
		}else if(res.equalsIgnoreCase("noload")){
			frame.setVisible(true);
			frame.core = this;
			EventQueue.invokeLater(this);
		}
	}

	public void registerFileEnding(FileEnd fend) { // XXX main -
		// registerFileEnding :
		// Working
		fileEnds.add(fend);
	}

	public FileEnd checkFileEnd(String fileend) { // XXX main -
		// checkFileEnd : Working
		for (FileEnd fend : fileEnds) {
			if (fend.checkFileEnd(fileend)) {
				return fend;
			}
		}
		System.out.println("failed BLUBAS");
		return null;
	}

	public boolean checkFileEndBoolean(String fileEnd) {
		for (FileEnd fend : fileEnds) {
			if (fend.checkFileEnd(fileEnd)) {
				return true;
			}
		}
		System.out.println("failed BLUBAS");
		return false;
	}

	public void run() {
		try {
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String parseCommands(String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("-update")) {
				internalUpdater.lookForUpdates();
				return "noload";
			} else if (args[0].equalsIgnoreCase("-debug")) {
				if (args.length > 1) {
					if (args[1].equalsIgnoreCase("on")) {
						settings.set(showDebug, "true");
						System.out.println("Show debug on console : " + settings.get(showDebug, "false"));
						return "end";
					} else if (args[1].equalsIgnoreCase("off")) {
						settings.set(showDebug, "false");
						System.out.println("Show debug on console : " + settings.get(showDebug, "false"));
						return "end";
					}
				}
			}
		} else {
			return "proceed";
		}
		return "proceed";
	}

	public FileEnd disableButton(FileEnd f, String name) {
		for (int i = 0; i < fileEnds.size(); i++) {
			if (f.name.equalsIgnoreCase(fileEnds.get(i).name)) {
				f.disableButtons(name);
				fileEnds.set(i, f);
			}
		}
		return f;
	}

	public void checkDebugConsole() {
		System.out.println("Show debugConsole : " + settings.getBoolean(showDebug));
		if (settings.getBoolean(showDebug)) {
			try {
				FabFileConverter.consoleGui.setVisible(true);
				System.out.println("[Plugin-Manager] Loading Jars ...");
			} catch (Exception e) {
				System.out.println("Sorry , Something went wrong with the debug console");
				JOptionPane box = new JOptionPane("Sorry , Something went wrong with the debug console");
				box.show();
			}
		}
	}

	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {
		}
	}

}
