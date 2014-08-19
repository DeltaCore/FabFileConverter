package net.ccmob.fabfileconverter.converter.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;

import net.ccmob.fabfileconverter.converter.reader.GmConverter;
import net.ccmob.fabfileconverter.converter.types.GmCoord;
import net.ccmob.fabfileconverter.converter.types.GmState;
import net.ccmob.fabfileconverter.converter.types.Tool;

public class MillPreview extends JPanel {

	private static final long	serialVersionUID	= 1L;

	GmConverter								converter;

	float											scale							= 0.0F;

	String										format						= "";

	final float								margin						= 100;

	float											sX								= 0, sY = 0, bX = 0, bY = 0;
	boolean										calc							= false;
	Font											font							= new Font("Arial", Font.PLAIN, 18);

	int												x									= 0, y = 0, lx = 0, ly = 0;
	int												size							= 0;

	Tool											tool							= null;

	Graphics2D								g2								= null;

	public MillPreview(GmConverter c) {
		this.setBackground(Color.black);
		this.setConverter(c);
	}

	@Override
	public void paint(Graphics g) {
		g2 = (Graphics2D) g;
		for (int i = 0; i < this.getConverter().getFiles().size(); i++) {
			for (int t = 0; t < this.getConverter().getFiles().get(i).getTools().size(); t++) {
				for (int p = 0; p < this.getConverter().getFiles().get(i).getCoords().size(); p++) {
					if (this.getConverter().getFiles().get(i).getTools().get(t) != null) {
						if (this.getConverter().getFiles().get(i).getCoords().get(p).getToolIndex() == this.getConverter().getFiles().get(i).getTools().get(t).getIndex()) {
							tool = this.getConverter().getFiles().get(i).getTools().get(t);
							x = this.getConverter().getFiles().get(i).getCoords().get(p).getX();
							y = this.getConverter().getFiles().get(i).getCoords().get(p).getY();
							size = Integer.parseInt(tool.getSize().replace(".0", ""));
							x *= scale;
							y *= scale;
							size *= scale;
							g2.setColor(Color.blue);
							g2.fillArc(x - (size / 2), y - (size / 2), size, size, 0, 360);
						}
					}
				}
			}
		}
		for (int i = 0; i < this.getConverter().getFiles().size(); i++) {
			for (int t = 0; t < this.getConverter().getFiles().get(i).getTools().size(); t++) {
				for (int p = 0; p < this.getConverter().getFiles().get(i).getCoords().size(); p++) {
					if (this.getConverter().getFiles().get(i).getTools().get(t) != null) {
						if (this.getConverter().getFiles().get(i).getCoords().get(p).getToolIndex() == this.getConverter().getFiles().get(i).getTools().get(t).getIndex()) {
							x = this.getConverter().getFiles().get(i).getCoords().get(p).getX();
							y = this.getConverter().getFiles().get(i).getCoords().get(p).getY();
							tool = this.getConverter().getFiles().get(i).getTools().get(t);
							size = (int) (Integer.parseInt(tool.getSize().replace(".0", "")) * scale);
							x = (int) (x * scale);
							y = (int) (y * scale);
							if (this.getConverter().getFiles().get(i).getCoords().get(p).getState().equals(GmState.PD)) {
								g2.setColor(Color.blue);
								g2.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
							} else if (this.getConverter().getFiles().get(i).getCoords().get(p).getState().equals(GmState.PU)) {
								g2.setColor(Color.red);
								g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
							} else {
								g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
								g2.setColor(Color.white);
							}
							g2.drawLine(x, y, lx, ly);
							lx = x;
							ly = y;
						}
					}
				}
			}
		}
	}

	public GmConverter getConverter() {
		return converter;
	}

	public void setConverter(GmConverter converter) {
		this.converter = converter;
	}

	public void calcOneTime() {
		calc = true;
		boolean flag = false;
		float size = 0.0F;
		for (int f = 0; f < this.getConverter().getFiles().size(); f++) {
			for (int i = 0; i < this.getConverter().getFiles().get(f).getCoords().size(); i++) {
				if (this.getConverter().getFiles().get(f).getCoords().get(i) != null) {
					GmCoord p = this.getConverter().getFiles().get(f).getCoords().get(i);
					if (p != null) {
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
