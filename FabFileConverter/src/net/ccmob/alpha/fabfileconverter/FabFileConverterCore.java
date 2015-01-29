package net.ccmob.alpha.fabfileconverter;

import net.ccmob.alpha.fabfileconverter.natives.MacOSX;
import net.ccmob.alpha.fabfileconverter.natives.NativeHandler;

public class FabFileConverterCore {

	public static CoreGui coreGuiInstance;
	
	public static void main(String[] args) {
		natives();
		coreGuiInstance = new CoreGui(args);
	}
	
	public static final NativeHandler[]	nativeHandler	= new NativeHandler[] { new MacOSX("OS X") };
	
	private static void natives(){
		String osName = System.getProperty("os.name");
		for (int i = 0; i < nativeHandler.length; i++) {
			if (osName.contains(nativeHandler[i].getOsName())) {
				System.out.println(nativeHandler[i].getOsName() + " deteced. Running Native handler for this system.");
				nativeHandler[i].setupSystemProperties();
			}
		}
	}
	
}
