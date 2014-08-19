package net.ccmob.fabfileconverter.converter.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import net.ccmob.fabfileconverter.converter.reader.DrlConverter;
import net.ccmob.fabfileconverter.converter.types.Point;
import net.ccmob.fabfileconverter.converter.types.Tool;

public class DrillPreview extends JPanel {

	private static final long	serialVersionUID	= 1L;
	DrlConverter	            c;
	float	                    scale	           = 0.0F;

	String	                  format	         = "";

	final float	              margin	         = 10;

	float	                    sX	             = 0, sY = 0, bX = 0, bY = 0;
	boolean	                  calc	           = false;
	Font	                    font	           = new Font("Arial", Font.PLAIN, 18);

	int lastX = 0, lastY = 0;
	
	public DrillPreview(DrlConverter c, String format) {
		super();
		this.c = c;
		this.format = format;
	}
	
	public void calcOneTime() {
		calc = true;
		boolean flag = false;
		float size = 0.0F;
		for (int i = 0; i < this.c.points.size(); i++) {
			if (c.tools[i] != null) {
				Tool t = c.tools[i];
				size = Float.valueOf(t.getSize());
				size *= 1000;
				System.out.println("Tool color : " + t.getColor().toString());
			} else if (c.points.get(i) != null) {
				Point p = c.points.get(i);
				if (p != null) {
					System.out.println("Hole at : " + p.getX() + " : " + p.getY());
					if (!flag) {
						flag = true;
						sX = p.getX();
						sY = p.getY();
					}

					if (p.getX() > bX) {
						bX = p.getX();
					} else if (p.getX() < sX) {
						sX = p.getX();
					}
					if (p.getY() > bY) {
						bY = p.getY();
					} else if (p.getY() < sY) {
						sY = p.getY();
					}

					float tX = p.getX() + (size);
					if (tX > bX) {
						bX = tX + margin;
					}
					float tY = p.getY() + (size);
					if (tY > bY) {
						bY = tY + margin;
					}

				}
			}
		}

		bX += margin;
		bY += margin;
	}

	public void calculcate() {
		Rectangle r = this.getBounds();
		float width = (float) r.getWidth();
		float height = (float) r.getHeight();
		// System.out.println("Width : " + width + " Height : " + height);

		float xScale = width / (bX + sX);
		float yScale = height / (bY + sY);

		scale = 0.0F;

		if (xScale > yScale) {
			scale = yScale;
		} else if (yScale > xScale) {
			scale = xScale;
		} else if (xScale == yScale) {
			scale = xScale;
		}
		// System.out.println("Biggest X " + bX + " Biggest Y : " + bY);
		// System.out.println("Smalest X : " + sX + " Smalest Y : " + sY);
		// System.out.println("Scale ; x : " + xScale + " y : " + yScale);
		// System.out.println("Scale : " + scale);
	}

	private void render(Graphics2D g){
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.setBackground(Color.black);
		Rectangle r = this.getBounds();
		int lastText = (int) (r.getHeight() - margin);
		if (scale != 0.0F) {
			float size = 0.0F;
			Tool lastTool = null;
			for (int i = 0; i < this.c.points.size(); i++) {
				if (c.tools[i] != null) {
					Tool t = c.tools[i];
					size = Float.valueOf(t.getSize());
					size *= 1000;
					lastTool = t;
					g.setColor(t.getColor());
					g.setFont(font);
					float s = Float.valueOf(t.getSize()) * 1000;
					int width = (int) r.getWidth();
					String text = "Tool no." + t.getIndex() + " - size : "
					    + String.valueOf(s) + format;
					int y = lastText;
					Rectangle2D rect = g.getFontMetrics().getStringBounds(text, g);
					int x = (int) width - (int) margin - rect.getBounds().width;
					g.drawString(text, x, y);
					lastText -= (rect.getBounds().height + margin);
				} else if (c.points.get(i) != null) {
					Point p = c.points.get(i);
					if (p != null) {
						int x = p.getX();
						int y = p.getY();
						x *= scale;
						y *= scale;
						g.setColor(lastTool.getColor());
						float s = size;
						s *= scale;
						// g.drawArc(x, y, (int) s, (int) s, 0, 360);
						g.fillArc(x, y, (int) s, (int) s, 0, 360);
						g.setColor(Color.white);
						g.drawLine(lastX + (int)(s/2), lastY + (int)(s/2),x + (int)(s/2), y + (int)(s/2));
						lastX = x;
						lastY = y;
					}
				}
			}
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.render((Graphics2D) g);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		super.setBounds(x, y, width, height);
		if (!calc) {
			calcOneTime();
		}
		calculcate();
	}

}