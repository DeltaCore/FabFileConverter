package net.ccmob.alpha.fabfileconverter;

import net.ccmob.fabfileconverter.FabFileConverter.FabFileConverter;

public class FabFileConverterCore {

	public static CoreGui coreGuiInstance;
	
	public static void main(String[] args) {
		natives();
		coreGuiInstance = new CoreGui();
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
	
}
