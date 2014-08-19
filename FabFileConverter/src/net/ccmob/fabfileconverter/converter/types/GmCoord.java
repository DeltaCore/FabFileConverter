package net.ccmob.fabfileconverter.converter.types;

public class GmCoord {

	private int			x					= 0;
	private int			y					= 0;
	private GmState	state			= null;
	private int			toolIndex	= 0;

	public GmCoord(int x, int y, GmState state, int toolindex) {
		this.setX(x);
		this.setY(y);
		this.setState(state);
		this.setToolIndex(toolindex);
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

	public GmState getState() {
		return state;
	}

	public void setState(GmState state) {
		this.state = state;
	}

	public int getToolIndex() {
		return toolIndex;
	}

	public void setToolIndex(int toolIndex) {
		this.toolIndex = toolIndex;
	}

	@Override
	public String toString() {
		return "[GmCoord[X: " + this.getX() + "][Y: " + this.getY() + "];";
	}
	
}
