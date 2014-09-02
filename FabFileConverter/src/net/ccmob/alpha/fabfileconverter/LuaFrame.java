package net.ccmob.alpha.fabfileconverter;

import java.awt.Graphics;

import javax.swing.JPanel;

import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaFrame extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3705508903679074075L;
	private Globals globals;
	
	public LuaFrame(String scriptName) {
		globals = JsePlatform.standardGlobals();
		globals.loadfile("res/nativelib.lua").call();
		globals.loadfile("res/" + scriptName).call();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
	}
	
	public void loadGR2D(Globals g){
		
	}
	
}
