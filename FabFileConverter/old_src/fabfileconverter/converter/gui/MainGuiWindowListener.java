package net.ccmob.fabfileconverter.converter.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import net.ccmob.fabfileconverter.converter.SettingsReader;

public class MainGuiWindowListener implements WindowListener {

	SettingsReader	settings;

	public MainGuiWindowListener(SettingsReader settings) {
		this.settings = settings;
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
		try{settings.save();}catch(Exception e_){}
	}

	public void windowClosing(WindowEvent e) {
		try{settings.save();}catch(Exception e_){}
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

}
