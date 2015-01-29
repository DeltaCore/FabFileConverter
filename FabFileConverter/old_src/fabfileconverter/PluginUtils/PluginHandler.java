package net.ccmob.fabfileconverter.PluginUtils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import net.ccmob.fabfileconverter.FabFileConverter.Core;
import net.ccmob.fabfileconverter.converter.types.FileEnd;

public class PluginHandler {

	static Core	core;

	public PluginHandler(Core c) {
		core = c;
	}

	public void loadJars() {
		//String pathSeperator = System.getProperty("file.separator");
		File folder = new File(getApplicationPath() + "plugins");
		if (!folder.exists())
			folder.mkdir();
		for (File f : folder.listFiles()) {
			System.out.println("Working at file : " + f.getAbsolutePath());
			if (f.getName().endsWith(".jar") && (!f.isDirectory()))
				loadJarFile(f);
		}
	}

	public void loadJarFile(File f) {
		try {
			URL url = new URL(f.getAbsolutePath());
			URL[] urls = new URL[] { url };
			URLClassLoader cLoader = new URLClassLoader(urls);
			Class<?> c = cLoader.loadClass("FabFilePlugin.MainClass");
			cLoader.close();
			c.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getApplicationPath() {
		String sep = File.separator;
		String osName = System.getProperty("os.name");
		//String path = System.getProperty("file.separator");
		String programPath = System.getenv("ProgramFiles");
		if (osName.equalsIgnoreCase("Mac OS X")) {
			File f = new File(sep + "Applications" + sep + "FabFileConverter" + sep);
			if (!f.exists())
				f.mkdir();
			return "/Applications/FabFileConverter/";
		} else if (osName.contains("Windows")) {
			File p = new File(programPath + sep + "FabFileConverter" + sep);
			if(!p.exists()) p.mkdir();
			return programPath + sep + "FabFileConverter" + sep;
		}
		return "";
	}

	public static void registerFabricationFileConverter(FileEnd f) {
		core.registerFileEnding(f);
		System.out.println("New fileend : " + f.name);
	}

	public static Core getCore() {
		return core;
	}

}
