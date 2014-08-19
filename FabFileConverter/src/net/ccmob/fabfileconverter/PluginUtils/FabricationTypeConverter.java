package net.ccmob.fabfileconverter.PluginUtils;

import java.util.ArrayList;

import javax.swing.JTextArea;

import net.ccmob.fabfileconverter.converter.gui.MainGui;
import net.ccmob.fabfileconverter.converter.types.ConvertOption;
import net.ccmob.fabfileconverter.converter.types.FileEnd;

public abstract class FabricationTypeConverter {

	String	name	= "";

	public FabricationTypeConverter(String name) {
		this.name = name;
		System.out.println("New FabricationTypeConverter : " + name);
	}

	public abstract boolean convert(String filename, ArrayList<ConvertOption> optionss);

	public abstract void readIn(String filename);

	public abstract void createPreview(JTextArea ContentBox) ;

	public abstract void createEndView(JTextArea ContentBox, ArrayList<ConvertOption> options);

	public abstract void Gui(FileEnd f, MainGui parent);

	public abstract void clearSettings();
	
	public abstract FabricationTypeConverter create();
	
}
