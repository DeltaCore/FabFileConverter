package net.ccmob.fabfileconverter.converter.types;

import java.util.ArrayList;

public class GmState {

	public static final GmState	     PD	         = new GmState(1, "PD");
	public static final GmState	     PU	         = new GmState(2, "PU");
	public static ArrayList<GmState>	states	   = new ArrayList<GmState>();
	private int	                     identifier	 = 0;
	private String	                 sIdentifier	= "";

	public static GmState getByIdentiefier(int id) {
		for (int i = 0; i < states.size(); i++) {
			if (states.get(i).getIdentifier() == id) {
				return states.get(i);
			}
		}
		return null;
	}

	public GmState(int identifier, String sId) {
		this.setIdentifier(identifier);
		this.setsIdentifier(sId);
	}

	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	public String getsIdentifier() {
		return sIdentifier;
	}

	public void setsIdentifier(String sIdentifier) {
		this.sIdentifier = sIdentifier;
	}

	@Override
	public String toString() {
		return "[gmState] " + this.getsIdentifier();
	}

}
