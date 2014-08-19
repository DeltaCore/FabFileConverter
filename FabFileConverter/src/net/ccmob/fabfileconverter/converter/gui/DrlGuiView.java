package net.ccmob.fabfileconverter.converter.gui;

import java.awt.Color;

import javax.swing.JFrame;

import net.ccmob.fabfileconverter.converter.reader.DrlConverter;

public class DrlGuiView extends JFrame {

	/**
	 * Create the frame.
	 * 
	 * @param yOrigin
	 * @param xOrigin
	 */

	public DrlGuiView(DrlConverter c, String f) {
		super("Drill hole view");
		setBackground(Color.BLACK);
		setBounds(100, 100, 604, 388);
		DrillPreview preview = new DrillPreview(c, f);
		setContentPane(preview);
	}
}
