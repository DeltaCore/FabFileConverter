package net.ccmob.fabfileconverter.converter.types;

import java.util.ArrayList;

import javax.swing.JTextArea;

import net.ccmob.fabfileconverter.converter.gui.MainGui;

public interface ConverterInterface {

	public boolean convert(String filename, ArrayList<ConvertOption> options);

	public void readIn(String filename);

	public void createPreview(JTextArea ContentBox);

	public void createEndView(JTextArea ContentBox, ArrayList<ConvertOption> options);

	public void Gui(FileEnd f, MainGui parent);
}
