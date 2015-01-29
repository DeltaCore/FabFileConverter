package net.ccmob.fabfileconverter.PluginUtils;

import net.ccmob.fabfileconverter.converter.types.FileEnd;

public class FabFilePlugin {

	FileEnd	fileEnd;

	public FabFilePlugin(FileEnd f) {
		this.fileEnd = f;
		PluginHandler.registerFabricationFileConverter(f);
	}

	public void disableButton(String name) {
		PluginHandler.getCore().disableButton(fileEnd, name);
	}

}
