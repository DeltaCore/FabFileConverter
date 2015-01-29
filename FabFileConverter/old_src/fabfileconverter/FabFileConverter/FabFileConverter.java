package net.ccmob.fabfileconverter.FabFileConverter;

import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JOptionPane;

import net.ccmob.fabfileconverter.Natives.MacOSX;
import net.ccmob.fabfileconverter.Natives.NativeHandler;
import net.ccmob.fabfileconverter.converter.gui.ConsoleGui;

public class FabFileConverter {

	static Core	       core;
	public static final NativeHandler[]	nativeHandler	= new NativeHandler[] { new MacOSX("OS X") };

	static ConsoleGui	 consoleGui	   = new ConsoleGui();
	static PrintStream	debugConsole	= new PrintStream(new DebugConsole(
	                                     consoleGui.txtrLog));

	public static void main(String[] args) { // XXX main : Working
		natives();
		System.setOut(debugConsole);
		System.setErr(debugConsole);
		try {
			core = new Core(args);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Something went wrong o.O ! : \n" + e.getMessage() + "\nIs this application executed as an admin ?");
			System.out
			    .println("Oh ! Something went wrong ... Just restart the application !");
			e.printStackTrace();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Something went wrong o.O ! : \n" + e.getMessage() + "\nIs this application executed as an admin ?");
			System.out
			    .println("Oh ! Something went wrong ... Just restart the application !");
			e.printStackTrace();
		}
	}
	
	private static void natives(){
		String osName = System.getProperty("os.name");
		for (int i = 0; i < FabFileConverter.nativeHandler.length; i++) {
			if (osName.contains(FabFileConverter.nativeHandler[i].getOsName())) {
				System.out.println(FabFileConverter.nativeHandler[i].getOsName() + " deteced. Running Native handler for this system.");
				FabFileConverter.nativeHandler[i].setupSystemProperties();
			}
		}
	}
	
	public static Core getCore() {
		return core;
	}
}
