package net.ccmob.fabfileconverter.converter.reader;

import java.awt.Color;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import net.ccmob.fabfileconverter.PluginUtils.FabricationTypeConverter;
import net.ccmob.fabfileconverter.converter.gui.GmGuiView;
import net.ccmob.fabfileconverter.converter.gui.MainGui;
import net.ccmob.fabfileconverter.converter.types.ConvertOption;
import net.ccmob.fabfileconverter.converter.types.FileEnd;
import net.ccmob.fabfileconverter.converter.types.GmCoord;
import net.ccmob.fabfileconverter.converter.types.GmState;
import net.ccmob.fabfileconverter.converter.types.Tool;

public class GmConverter extends FabricationTypeConverter {

	private ArrayList<gmFile>	files		= new ArrayList<gmFile>();
	private Runnable					gui			= null;
	private GmGuiView					guiView	= null;

	public GmConverter() {
		super("G(m)Converter");
	}

	@Override
	public void readIn(String filename) {
		System.out.println("Path : " + filename);
		String sep = File.separator;
		File path = new File(filename.substring(0, filename.lastIndexOf(sep)));
		String prjName = filename.substring(filename.lastIndexOf('/') + 1, filename.lastIndexOf('.'));
		System.out.println("Project name : " + prjName);
		System.out.println("Searching for other project components ...");
		int start = Integer.parseInt(filename.substring(filename.lastIndexOf('.') + 3));
		System.out.println("Startindex for files to search for : " + start);
		for (File f : path.listFiles()) {
			for (int i = start; i < 17; i++) {
				if (f.getAbsolutePath().endsWith(prjName + ".gm" + i) || f.getAbsolutePath().endsWith(prjName + ".Gm" + i) || f.getAbsolutePath().endsWith(prjName + ".gM" + i)
						|| f.getAbsolutePath().endsWith(prjName + ".GM" + i)) {
					System.out.println("Another Layer found : " + f.getName());
					files.add(new gmFile(f.getAbsolutePath()));
				}
			}
		}
	}

	@Override
	public void createPreview(JTextArea ContentBox) {
		ContentBox.append("Files found : " + this.getFiles().size() + "\n");
		for (int i = 0; i < files.size(); i++) {
			ContentBox.append("File : " + this.getFiles().get(i).getFilename() + "\n\nTools : \n");
			for (int t = 0; t < this.getFiles().get(i).getTools().size(); t++) {
				ContentBox.append("  " + this.getFiles().get(i).getTools().get(t).toString() + "\n");
			}
		}
		for (int i = 0; i < files.size(); i++) {
			ContentBox.append("\nFile : " + this.getFiles().get(i).getFilename() + "\n\n");
			for (int t = 0; t < this.getFiles().get(i).getTools().size(); t++) {
				ContentBox.append("\nMillpoints for Tool : " + this.getFiles().get(i).getTools().get(t).getIndex() + "\n");
				for (int p = 0; p < this.getFiles().get(i).getCoords().size(); p++) {
					if (this.getFiles().get(i).getCoords().get(p).getToolIndex() == this.getFiles().get(i).getTools().get(t).getIndex()) {
						ContentBox.append("    " + this.getFiles().get(i).getCoords().get(p).toString() + "\n");
					}
				}
			}
		}
	}

	@Override
	public void Gui(FileEnd f, MainGui p) {
		if (this.getGuiView() != null) {
			this.getGuiView().dispose();
		} else {
			this.setGuiView(new GmGuiView((GmConverter) f.converter));
		}
		final GmGuiView g = this.getGuiView();
		this.setGui(new Runnable() {
			public void run() {
				g.setVisible(true);
			}
		});
		EventQueue.invokeLater(this.getGui());
	}
	
	@Override
	public boolean convert(String filename, ArrayList<ConvertOption> options) {
		try {
			int toolindex = 0;
			File f = new File(filename);
			String nLine = String.format("%n");
			FileWriter writer = new FileWriter(f);
			writer.write("PA;" + nLine);
			String id = "";
			int x = 0;
			int y = 0;
			for (int _f = 0; _f < this.getFiles().size(); _f++) {
				for (int t = 0; t < this.getFiles().get(_f).getTools().size(); t++) {
					toolindex++;
					writer.write(/*"PU;" + String.format("%n") + */"SP" + toolindex + ";" + nLine);
					for (int p = 0; p < this.getFiles().get(_f).getCoords().size(); p++) {
						if (this.getFiles().get(_f).getCoords().get(p).getToolIndex() == this.getFiles().get(_f).getTools().get(t).getIndex()) {
							id = this.getFiles().get(_f).getCoords().get(p).getState().getsIdentifier();
							x = this.getFiles().get(_f).getCoords().get(p).getX();
							y = this.getFiles().get(_f).getCoords().get(p).getY();
							writer.write(id + "" + x + "," + y);
							if (!(p + 1 >= (this.getFiles().get(_f).getCoords().size()))) {
								if (!id.equals(this.getFiles().get(_f).getCoords().get(p + 1).getState().getsIdentifier())) {
									if(p == this.getFiles().get(_f).getCoords().size() - 1){
										writer.write(";" + this.getFiles().get(_f).getCoords().get(p + 1).getState().getsIdentifier() + ";PU;");
									}else{
										writer.write(";" + this.getFiles().get(_f).getCoords().get(p + 1).getState().getsIdentifier() + ";");
									}
								}
							}
							writer.write(nLine);
						}
					}
				}
			}
			writer.write("PU;");
			writer.close();
			System.out.println("Done.");
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Es ist ein fehler wahrend des schreibens der Datei aufgetreten.");
			e.printStackTrace();
		}

		return true;
	}

	public class gmFile {

		private String			filename		= "";
		ArrayList<GmCoord>	coords			= new ArrayList<GmCoord>();
		ArrayList<Tool>			tools				= new ArrayList<Tool>();

		private String			rXYd				= "[xX]([-0-9]{1,})[yY]([-0-9]{1,})[dD][0]([1-2])[*]";
		private String			rXd					= "[xX]([-0-9]{1,})[dD][0]([1-2])[*]";
		private String			rYd					= "[yY]([-0-9]{1,})[dD][0]([1-2])[*]";

		private String			rXY					= "[xX]([-0-9]{1,})[yY]([-0-9]{1,})[*]";
		private String			rX					= "[xX]([-0-9]{1,})[*]";
		private String			rY					= "[yY]([-0-9]{1,})[*]";

		private String			rTool				= "%ADD([0-9]{1,})C,([0-9.]{1,})[*][%]";
		private String			rD					= "[Dd][0]([1-2])[*]";
		private String			rToolSelect	= "[G][5][4][D]([0-9]{1,})[*]";

		private Pattern			pXYd				= Pattern.compile(rXYd);
		private Pattern			pXd					= Pattern.compile(rXd);
		private Pattern			pYd					= Pattern.compile(rYd);

		private Pattern			pXY					= Pattern.compile(rXY);
		private Pattern			pX					= Pattern.compile(rX);
		private Pattern			pY					= Pattern.compile(rY);

		private Pattern			pTool				= Pattern.compile(rTool);
		private Pattern			pD					= Pattern.compile(rD);
		private Pattern			pToolSelect	= Pattern.compile(rToolSelect);

		private Matcher			mXYd				= pXYd.matcher("");
		private Matcher			mXd					= pXd.matcher("");
		private Matcher			mYd					= pYd.matcher("");

		private Matcher			mXY					= pXY.matcher("");
		private Matcher			mX					= pX.matcher("");
		private Matcher			mY					= pY.matcher("");

		private Matcher			mTool				= pTool.matcher("");
		private Matcher			mD					= pD.matcher("");
		private Matcher			mToolSelect	= pToolSelect.matcher("");

		private void updateMatcher(String line) {
			mXYd = pXYd.matcher(line);
			mXd = pXd.matcher(line);
			mYd = pYd.matcher(line);
			mXY = pXY.matcher(line);
			mX = pX.matcher(line);
			mY = pY.matcher(line);
			mTool = pTool.matcher(line);
			mD = pD.matcher(line);
			mToolSelect = pToolSelect.matcher(line);
		}

		public gmFile(String filename) {
			String line = "";
			this.setFilename(filename);
			try {
				BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
				GmCoord lastCoord = null;
				GmCoord tmp = null;
				GmState lastState = null;
				int activeTool = 0;
				int x = 0;
				int y = 0;
				System.out.println("Reading file ... (" + filename + ")");
				//System.out.println("Content :");
				boolean add = false;
				while ((line = reader.readLine()) != null) {
					updateMatcher(line);
					//System.out.print(line);
					if (mXYd.matches()) {
						x = Integer.parseInt(mXYd.group(1));
						y = Integer.parseInt(mXYd.group(2));
						if (Integer.parseInt(mXYd.group(3)) == 1) {
							tmp = new GmCoord(x, y, GmState.PD, activeTool);
							//System.out.println(" - X:" + x + " Y:" + y + " State:" + GmState.PD.toString() + " Tool : " + activeTool);
							lastState = GmState.PD;
						} else if (Integer.parseInt(mXYd.group(3)) == 2) {
							tmp = new GmCoord(x, y, GmState.PU, activeTool);
							//System.out.println(" - X:" + x + " Y:" + y + " State:" + GmState.PU.toString() + " Tool : " + activeTool);
						}
						add = true;
					} else if (mXd.matches()) {
						x = Integer.parseInt(mXd.group(1));
						if (Integer.parseInt(mXd.group(2)) == 1) {
							tmp = new GmCoord(x, lastCoord.getY(), GmState.PD, activeTool);
							//System.out.println(" - X:" + x + " Y:" + lastCoord.getY() + " State:" + GmState.PD.toString() + " Tool : " + activeTool);
							lastState = GmState.PD;
						} else if (Integer.parseInt(mXd.group(2)) == 2) {
							tmp = new GmCoord(x, lastCoord.getY(), GmState.PU, activeTool);
							//System.out.println(" - X:" + x + " Y:" + lastCoord.getY() + " State:" + GmState.PU.toString() + " Tool : " + activeTool);
						}
						add = true;
					} else if (mYd.matches()) {
						y = Integer.parseInt(mYd.group(1));
						if (Integer.parseInt(mYd.group(2)) == 1) {
							tmp = new GmCoord(x, y, GmState.PD, activeTool);
							//System.out.println(" - X:" + lastCoord.getX() + " Y:" + y + " State:" + GmState.PD.toString() + " Tool : " + activeTool);
							lastState = GmState.PD;
						} else if (Integer.parseInt(mYd.group(2)) == 2) {
							tmp = new GmCoord(x, y, GmState.PU, activeTool);
							//System.out.println(" - X:" + lastCoord.getX() + " Y:" + y + " State:" + GmState.PU.toString() + " Tool : " + activeTool);
						}
						add = true;
					} else if (mXY.matches()) {
						x = Integer.parseInt(mXY.group(1));
						y = Integer.parseInt(mXY.group(2));
						if (lastState.getIdentifier() == 1) {
							tmp = new GmCoord(x, y, GmState.PD, activeTool);
							//System.out.println(" - X:" + x + " Y:" + y + " State:" + GmState.PD.toString() + " Tool : " + activeTool);
							lastState = GmState.PD;
						} else if (lastState.getIdentifier() == 2) {
							tmp = new GmCoord(x, y, GmState.PU, activeTool);
							//System.out.println(" - X:" + x + " Y:" + y + " State:" + GmState.PU.toString() + " Tool : " + activeTool);
						}
						add = true;
					} else if (mX.matches()) {
						x = Integer.parseInt(mX.group(1));
						if (lastState.getIdentifier() == 1) {
							tmp = new GmCoord(x, lastCoord.getY(), GmState.PD, activeTool);
							//System.out.println(" - X:" + x + " Y:" + lastCoord.getY() + " State:" + GmState.PD.toString() + " Tool : " + activeTool);
							lastState = GmState.PD;
						} else if (lastState.getIdentifier() == 1) {
							tmp = new GmCoord(x, lastCoord.getY(), GmState.PU, activeTool);
							//System.out.println(" - X:" + x + " Y:" + lastCoord.getY() + " State:" + GmState.PU.toString() + " Tool : " + activeTool);
						}
						add = true;
					} else if (mY.matches()) {
						y = Integer.parseInt(mY.group(1));
						if (lastState.getIdentifier() == 1) {
							tmp = new GmCoord(x, y, GmState.PD, activeTool);
							//System.out.println(" - X:" + lastCoord.getX() + " Y:" + y + " State:" + GmState.PD.toString() + " Tool : " + activeTool);
							lastState = GmState.PD;
						} else if (lastState.getIdentifier() == 2) {
							tmp = new GmCoord(x, y, GmState.PU, activeTool);
							//System.out.println(" - X:" + lastCoord.getX() + " Y:" + y + " State:" + GmState.PU.toString() + " Tool : " + activeTool);
						}
						add = true;
					} else if (mTool.matches()) {
						String size = mTool.group(2);
						int index = Integer.parseInt(mTool.group(1)) - 10;
						float s = Float.parseFloat(size) * 1000;
						Tool t = new Tool(index, s, Color.blue);
						//System.out.println(" - New Tool[" + index + "] with size " + s + "mil");
						this.getTools().add(t);
					} else if (mD.matches()) {
						if (Integer.parseInt(mD.group(1)) == 1) {
							lastState = GmState.PD;
						} else if (Integer.parseInt(mD.group(1)) == 2) {
							lastState = GmState.PU;
						}
						//System.out.println("- new state : " + lastState.toString());
					} else if (mToolSelect.matches()) {
						int nTool = Integer.parseInt(mToolSelect.group(1)) - 10;
						for (int i = 0; i < this.getTools().size(); i++) {
							if (this.getTools().get(i).getIndex() == nTool) {
								activeTool = this.getTools().get(i).getIndex();
							}
						}
						//System.out.println(" - Tool selected - " + nTool);
					} else {
						//System.out.println();
					}
					if (add) {
						lastCoord = tmp;
						this.getCoords().add(tmp);
						add = false;
					}
				}
				reader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("  - Tools found: " + this.getTools().size());
			System.out.println("  - Points found: " + this.getCoords().size());
			System.out.println();
		}

		public ArrayList<GmCoord> getCoords() {
			return coords;
		}

		public void setCoords(ArrayList<GmCoord> coords) {
			this.coords = coords;
		}

		public String getrXYd() {
			return rXYd;
		}

		public void setrXYd(String rXYd) {
			this.rXYd = rXYd;
		}

		public String getrXd() {
			return rXd;
		}

		public void setrXd(String rXd) {
			this.rXd = rXd;
		}

		public String getrYd() {
			return rYd;
		}

		public void setrYd(String rYd) {
			this.rYd = rYd;
		}

		public String getrXY() {
			return rXY;
		}

		public void setrXY(String rXY) {
			this.rXY = rXY;
		}

		public String getrX() {
			return rX;
		}

		public void setrX(String rX) {
			this.rX = rX;
		}

		public String getrY() {
			return rY;
		}

		public void setrY(String rY) {
			this.rY = rY;
		}

		public Pattern getpXYd() {
			return pXYd;
		}

		public void setpXYd(Pattern pXYd) {
			this.pXYd = pXYd;
		}

		public Pattern getpXd() {
			return pXd;
		}

		public void setpXd(Pattern pXd) {
			this.pXd = pXd;
		}

		public Pattern getpYd() {
			return pYd;
		}

		public void setpYd(Pattern pYd) {
			this.pYd = pYd;
		}

		public Pattern getpXY() {
			return pXY;
		}

		public void setpXY(Pattern pXY) {
			this.pXY = pXY;
		}

		public Pattern getpX() {
			return pX;
		}

		public void setpX(Pattern pX) {
			this.pX = pX;
		}

		public Pattern getpY() {
			return pY;
		}

		public void setpY(Pattern pY) {
			this.pY = pY;
		}

		public Matcher getmXYd() {
			return mXYd;
		}

		public void setmXYd(Matcher mXYd) {
			this.mXYd = mXYd;
		}

		public Matcher getmXd() {
			return mXd;
		}

		public void setmXd(Matcher mXd) {
			this.mXd = mXd;
		}

		public Matcher getmYd() {
			return mYd;
		}

		public void setmYd(Matcher mYd) {
			this.mYd = mYd;
		}

		public Matcher getmXY() {
			return mXY;
		}

		public void setmXY(Matcher mXY) {
			this.mXY = mXY;
		}

		public Matcher getmX() {
			return mX;
		}

		public void setmX(Matcher mX) {
			this.mX = mX;
		}

		public Matcher getmY() {
			return mY;
		}

		public void setmY(Matcher mY) {
			this.mY = mY;
		}

		public String getrTool() {
			return rTool;
		}

		public void setrTool(String rTool) {
			this.rTool = rTool;
		}

		public String getrD() {
			return rD;
		}

		public void setrD(String rD) {
			this.rD = rD;
		}

		public Pattern getpTool() {
			return pTool;
		}

		public void setpTool(Pattern pTool) {
			this.pTool = pTool;
		}

		public Pattern getpD() {
			return pD;
		}

		public void setpD(Pattern pD) {
			this.pD = pD;
		}

		public Matcher getmTool() {
			return mTool;
		}

		public void setmTool(Matcher mTool) {
			this.mTool = mTool;
		}

		public Matcher getmD() {
			return mD;
		}

		public void setmD(Matcher mD) {
			this.mD = mD;
		}

		public ArrayList<Tool> getTools() {
			return tools;
		}

		public void setTools(ArrayList<Tool> tools) {
			this.tools = tools;
		}

		public String getrToolSelect() {
			return rToolSelect;
		}

		public void setrToolSelect(String rToolSelect) {
			this.rToolSelect = rToolSelect;
		}

		public Pattern getpToolSelect() {
			return pToolSelect;
		}

		public void setpToolSelect(Pattern pToolSelect) {
			this.pToolSelect = pToolSelect;
		}

		public Matcher getmToolSelect() {
			return mToolSelect;
		}

		public void setmToolSelect(Matcher mToolSelect) {
			this.mToolSelect = mToolSelect;
		}

		public String getFilename() {
			return filename;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}
	}

	public ArrayList<gmFile> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<gmFile> files) {
		this.files = files;
	}

	@Override
	public void createEndView(JTextArea ContentBox, ArrayList<ConvertOption> options) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearSettings() {
		this.setFiles(new ArrayList<gmFile>());
	}

	public FabricationTypeConverter create() {
		return new GmConverter();
	}

	public Runnable getGui() {
		return gui;
	}

	public void setGui(Runnable gui) {
		this.gui = gui;
	}

	public GmGuiView getGuiView() {
		return guiView;
	}

	public void setGuiView(GmGuiView guiView) {
		this.guiView = guiView;
	}

}
