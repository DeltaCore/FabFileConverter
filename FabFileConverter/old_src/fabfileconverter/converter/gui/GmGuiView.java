package net.ccmob.fabfileconverter.converter.gui;

import java.awt.Color;

import javax.swing.JFrame;

import net.ccmob.fabfileconverter.converter.reader.GmConverter;

public class GmGuiView extends JFrame{
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public GmGuiView(GmConverter c){
		super("Gm mill view");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBackground(Color.BLACK);
		setBounds(100, 100, 604, 388);
		MillPreview preview = new MillPreview(c);
		setContentPane(preview);
		preview.repaint();
	}
	
}
