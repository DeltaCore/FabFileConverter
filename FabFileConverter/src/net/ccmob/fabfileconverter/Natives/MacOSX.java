package net.ccmob.fabfileconverter.Natives;

import javax.swing.UIManager;

public class MacOSX extends NativeHandler {

	public MacOSX(String osName) {
		super(osName);
	}

	public void setupSystemProperties() {
		System.out.println("Setting props");
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "FabFileConverter");
		System.setProperty("apple.awt.brushMetalLook", "true");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
	}

	@Override
	public void postInit() {

	}

	@Override
	public void setTitle(String name) {
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", name);
	}

}
