package net.ccmob.alpha.fabfileconverter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class LuaFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3705508903679074075L;

	private LuaValue renderFunction;
	private LuaValue resizeFunction;
	private String moduleName;
	private Graphics2D graphics;

	private int x = 0, y = 0, r = 0, g = 0, b = 0;

	public LuaFrame(LuaValue render, LuaValue resize, String moduleName,
			String windowName) {
		super(windowName);
		this.renderFunction = FabFileConverterCore.coreGuiInstance.getGlobals()
				.get(render.tostring());
		this.resizeFunction = FabFileConverterCore.coreGuiInstance.getGlobals()
				.get(resize.toString());
		this.moduleName = moduleName;
		this.setSize(new Dimension(400, 300));
		this.setPreferredSize(new Dimension(400, 300));
		FabFileConverterCore.coreGuiInstance.getGlobals().set(
				moduleName + "_frame_height", 300);
		FabFileConverterCore.coreGuiInstance.getGlobals().set(
				moduleName + "_frame_width", 400);
		this.setContentPane(new GraphicsFrame(this));
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		loadGR2D(FabFileConverterCore.coreGuiInstance.getGlobals());
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		FabFileConverterCore.coreGuiInstance.getGlobals().set(
				moduleName + "_frame_height", height);
		FabFileConverterCore.coreGuiInstance.getGlobals().set(
				moduleName + "_frame_width", width);
		if (resizeFunction != null) {
			System.out.println("Resize");
			resizeFunction.call(LuaValue.valueOf(width),
					LuaValue.valueOf(height));
		}
		super.setBounds(x, y, width, height);
	}

	private class GraphicsFrame extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5122447720622434450L;

		private LuaFrame frame;

		public GraphicsFrame(LuaFrame frame) {
			this.frame = frame;
		}

		@Override
		protected void paintComponent(Graphics g) {
			this.frame.graphics = (Graphics2D) g;
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (this.frame.renderFunction != null) {
				this.frame.renderFunction.call();
			}
		}

		@Override
		public void setBounds(int x, int y, int width, int height) {
			FabFileConverterCore.coreGuiInstance.getGlobals().set(
					moduleName + "_frame_height", height);
			FabFileConverterCore.coreGuiInstance.getGlobals().set(
					moduleName + "_frame_width", width);
			if (resizeFunction != null) {
				System.out.println("Resize");
				resizeFunction.call(LuaValue.valueOf(width),
						LuaValue.valueOf(height));
			}
			super.setBounds(x, y, width, height);
		}
		
	}

	public void loadGR2D(Globals g) {
		g.set(this.moduleName + "_setXY", new setXY(this));
		g.set(this.moduleName + "_setRGB", new setRGB(this));
		g.set(this.moduleName + "_drawArc", new drawArc(this));
		g.set(this.moduleName + "_drawLine", new drawLine(this));
		g.set(this.moduleName + "_drawRect", new drawRect(this));
		g.set(this.moduleName + "_placeText", new placeText(this));
		g.set(this.moduleName + "_frame_setVisible", new frameSetVisible(this));
		g.set(this.moduleName + "_frame_update", new frameUpdate(this));
		System.out
				.println("Loaded LuaFrame functions for module " + moduleName);
	}

	private class setXY extends TwoArgFunction {

		private LuaFrame frame;

		public setXY(LuaFrame c) {
			this.frame = c;
		}

		@Override
		public LuaValue call(LuaValue x, LuaValue y) {
			frame.x = x.checkint();
			frame.y = y.checkint();
			return null;
		}

	}

	private class setRGB extends ThreeArgFunction {
		private LuaFrame frame;

		public setRGB(LuaFrame c) {
			this.frame = c;
		}

		@Override
		public LuaValue call(LuaValue r, LuaValue g, LuaValue b) {
			frame.r = r.checkint();
			frame.g = g.checkint();
			frame.b = b.checkint();
			return null;
		}
	}

	private class drawArc extends OneArgFunction {

		private LuaFrame frame;

		public drawArc(LuaFrame c) {
			this.frame = c;
		}

		@Override
		public LuaValue call(LuaValue rad) {
			frame.graphics.setColor(new Color(frame.r, frame.g, frame.b));
			frame.graphics.fillArc(frame.x, frame.y, rad.checkint(),
					rad.checkint(), 0, 360);
			return null;
		}

	}

	private class drawLine extends TwoArgFunction {
		private LuaFrame frame;

		public drawLine(LuaFrame c) {
			this.frame = c;
		}

		@Override
		public LuaValue call(LuaValue x2, LuaValue y2) {
			frame.graphics.setColor(new Color(frame.r, frame.g, frame.b));
			frame.graphics.drawLine(frame.x, frame.y, x2.checkint(),
					y2.checkint());
			return null;
		}

	}

	private class placeText extends ThreeArgFunction {

		private LuaFrame frame;

		public placeText(LuaFrame c) {
			this.frame = c;
		}

		@Override
		public LuaValue call(LuaValue text, LuaValue textSize, LuaValue fontName) {
			frame.graphics.setFont(new Font(fontName.toString(), Font.PLAIN,
					textSize.checkint()));
			frame.graphics.setColor(new Color(frame.r, frame.g, frame.b));
			frame.graphics.drawString(text.toString(), frame.x, frame.y);
			return null;
		}

	}

	private class drawRect extends TwoArgFunction {

		private LuaFrame frame;

		public drawRect(LuaFrame c) {
			this.frame = c;
		}

		@Override
		public LuaValue call(LuaValue w, LuaValue h) {
			frame.graphics.setColor(new Color(frame.r, frame.g, frame.b));
			frame.graphics.fillRect(frame.x, frame.y, w.checkint(),
					h.checkint());
			return null;
		}

	}

	private class frameSetVisible extends OneArgFunction {

		private LuaFrame frame;

		public frameSetVisible(LuaFrame c) {
			this.frame = c;
		}

		@Override
		public LuaValue call(LuaValue val) {
			frame.setVisible(val.checkboolean());
			return null;
		}

	}

	private class frameUpdate extends ZeroArgFunction {

		private LuaFrame frame;

		public frameUpdate(LuaFrame c) {
			this.frame = c;
		}

		@Override
		public LuaValue call() {
			frame.repaint();
			return null;
		}

	}

}
