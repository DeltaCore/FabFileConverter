package net.ccmob.fabfileconverter.converter.types;

import java.awt.Color;

import net.ccmob.fabfileconverter.converter.reader.DrlConverter;

public class Tool {

	int	   index	= 0;

	String	size	= "";
	Color	 color;

	public Tool(int index_, float size_) {
		this.index = index_;
		this.size = String.valueOf(size_);
		this.color = getColorFromIndex();
	}

	public Tool(int index_, String size_) {
		this.index = index_;
		this.size = size_;
		this.color = getColorFromIndex();
	}

	public Tool(int index_, float size_, Color c) {
		this.index = index_;
		this.size = String.valueOf(size_);
		this.color = c;
	}

	public Tool(int index_, String size_, Color c) {
		this.index = index_;
		this.size = size_;
		this.color = c;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	private Color getColorFromIndex() {
		int i = index;
		while (i > 10) {
			i -= 10;
		}
		return DrlConverter.colors[i - 1];
	}

	@Override
	public String toString() {
		return "Tool[" + this.getSize() + ";" + this.getColor() + "]";
	}
	
}
