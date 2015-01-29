package net.ccmob.fabfileconverter.converter.types;

public class Point { // XXX Point Class File : Working

	int	  x;
	int	  y	   = 0;
	float	size	= 0.0F;

	public Point(int x_, int y_, float s) {
		this.x = x_;
		this.y = y_;
		this.size = s;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

}
