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
import net.ccmob.fabfileconverter.converter.gui.DrlOptionsGui;
import net.ccmob.fabfileconverter.converter.gui.MainGui;
import net.ccmob.fabfileconverter.converter.types.ConvertOption;
import net.ccmob.fabfileconverter.converter.types.FileEnd;
import net.ccmob.fabfileconverter.converter.types.Point;
import net.ccmob.fabfileconverter.converter.types.Tool;

public class DrlConverter extends FabricationTypeConverter {

	public DrlConverter() {
		super("DrlConverter");
	}

	String					x_y_regex		= "^[xX]([0-9]{1,})[yY]([0-9]{1,})$";
	String					x_regex			= "^[xX]([0-9]{1,})$";
	String					y_regex			= "^[yY]([0-9]{1,})$";
	String					tool_regex		= "^[tT]([0-9]{1,})[cC]([0-9].[0-9]{1,})$";
	String					tool_use_regex	= "^[tT]([0-9]{1,})$";

	public static Color[]	colors			= { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.CYAN, Color.YELLOW, Color.WHITE, Color.MAGENTA,
			Color.PINK, Color.LIGHT_GRAY	};

	public ArrayList<Point>			points;
	public Tool[]			tools_init;
	public Tool[]			tools;

	String					format			= "unknown";

	boolean					fmat			= false;
	int						fmat_int		= 1000;

	int						fileLength		= 0;

	public ArrayList<ConvertOption> options			= new ArrayList<ConvertOption>();

	@Override
	public boolean convert(String filename, ArrayList<ConvertOption> options) {
		System.out.println("Writing file : " + filename + String.format("%n") + "	 Merge tools : " + options.contains(ConvertOption.Merge_tools));
		if (options.contains(ConvertOption.Merge_tools)) {
			try {
				File f = new File(filename);
				String nLine = String.format("%n");
				FileWriter writer = new FileWriter(f);
				writer.write("M72" + nLine);
				writer.write("M48" + nLine);
				int i = 2;
				boolean flag = false;
				String fTool = "";
				for (Tool t : tools_init) {
					if (t != null) {
						if (!flag) {
							flag = true;
							String toolnumber = "";
							if (t.getIndex() < 10) {
								toolnumber = "0" + String.valueOf(t.getIndex());
							} else {
								toolnumber = String.valueOf(t.getIndex());
							}
							// System.out.println("Toolnumber : " + toolnumber);
							String size = t.getSize();
							// System.out.println("Size of tool : " + size);
							String cmd = "T" + toolnumber + "C" + size;
							fTool = "T" + toolnumber;
							// System.out.println("Write line : " + cmd);
							writer.write(cmd + nLine);
						}
						i++;
					}
				}
				int c = i;
				writer.write("%" + nLine);
				writer.write(fTool + nLine);
				for (i = c; i < fileLength; i++) {
					if (points.get(i) != null) {
						Point p = points.get(i);
						if (p.getX() == -1) {
							writer.write("Y" + checkNullsPoint(p.getY(), 2) + nLine);
							// System.out.println("Writing point : Y = " +
							// p.getY());
						} else if (p.getY() == -1) {
							writer.write("X" + checkNullsPoint(p.getX(), 2) + nLine);
							// System.out.println("Writing point : X = " +
							// p.getX());
						} else {
							writer.write("X" + checkNullsPoint(p.getX(), 2) + "Y" + checkNullsPoint(p.getY(), 2) + nLine);
							// System.out.println("Writing point : X = " +
							// p.getX() + " Y = "
							// + p.getY());
						}
					}
				}
				writer.write("M30" + nLine);
				writer.close();
				System.out.println("Done.");
				return true;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Es ist ein fehler w�hrend des schreibens der Datei aufgetreten.");
				e.printStackTrace();
			}
		} else {
			try {
				File f = new File(filename);
				String nLine = String.format("%n");
				FileWriter writer = new FileWriter(f);
				writer.write("M72" + nLine);
				writer.write("M48" + nLine);
				int i = 2;
				for (Tool t : tools_init) {
					if (t != null) {
						String toolnumber = "";
						if (t.getIndex() < 10) {
							toolnumber = "0" + String.valueOf(t.getIndex());
						} else {
							toolnumber = String.valueOf(t.getIndex());
						}
						// System.out.println("Toolnumber : " + toolnumber);
						String size = t.getSize();
						// System.out.println("Size of tool : " + size);
						String cmd = "T" + toolnumber + "C" + size;
						// System.out.println("Write line : " + cmd);
						writer.write(cmd + nLine);
						i++;
					}
				}
				int c = i;
				writer.write("%" + nLine);
				for (i = c; i < fileLength; i++) {
					if (tools[i] != null) {
						writer.write("T" + checkNulls(tools[i].getIndex(), 2) + nLine);
					} else if (points.get(i) != null) {
						Point p = points.get(i);
						if (p.getX() == -1) {
							writer.write("Y" + checkNullsPoint(p.getY(), 2) + nLine);
							// System.out.println("Writing point : Y = " +
							// p.getY());
						} else if (p.getY() == -1) {
							writer.write("X" + checkNullsPoint(p.getX(), 2) + nLine);
							// System.out.println("Writing point : X = " +
							// p.getX());
						} else {
							writer.write("X" + checkNullsPoint(p.getX(), 2) + "Y" + checkNullsPoint(p.getY(), 2) + nLine);
							// System.out.println("Writing point : X = " +
							// p.getX() + " Y = "
							// + p.getY());
						}
					}
				}
				writer.write("M30" + nLine);
				writer.close();
				System.out.println("Done.");
				return true;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Es ist ein fehler w�hrend des schreibens der Datei aufgetreten.");
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void readIn(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
			int i = 0;
			while (reader.readLine() != null)
				i++;
			fileLength = i;
			System.out.println("FILELENGTH : " + i);
			points = new ArrayList<Point>();
			tools = new Tool[i];
			tools_init = new Tool[i];
			float lastSize = 0.0F;
			int lastX = 0;
			int lastY = 0;
			String line = "";
			i = 0;
			for (i = 0; i < fileLength; i++) {
				points.add(null);
				tools_init[i] = null;
				tools[i] = null;
			}
			i = 0;
			reader = new BufferedReader(new FileReader(new File(filename)));
			while ((line = reader.readLine()) != null) {
				if (line.matches(x_regex)) {
					Pattern pattern = Pattern.compile(x_regex);
					Matcher matcher = pattern.matcher(line);
					if (matcher.matches()) {
						int x = Integer.parseInt(matcher.group(1));
						lastX = x;
						points.set(i, new Point(x,lastY, lastSize));
						System.out.println("Found x: " + x + " in : " + line);
					}
				} else if (line.matches(y_regex)) {
					Pattern pattern = Pattern.compile(y_regex);
					Matcher matcher = pattern.matcher(line);
					if (matcher.matches()) {
						int y = Integer.parseInt(matcher.group(1));
						lastY = y;
						points.set(i, new Point(lastX,y, lastSize));
						System.out.println("Found y: " + y + " in : " + line);
					}
				} else if (line.matches(x_y_regex)) {
					Pattern pattern = Pattern.compile(x_y_regex);
					Matcher matcher = pattern.matcher(line);
					if (matcher.matches()) {
						int x = Integer.parseInt(matcher.group(1));
						int y = Integer.parseInt(matcher.group(2));
						lastX = x;
						lastY = y;
						points.set(i, new Point(x,y, lastSize));
						System.out.println("Found x: " + x + " y: " + y + " in : " + line);
					}
				} else if (line.matches(tool_use_regex)) {
					Pattern pattern = Pattern.compile(tool_use_regex);
					Matcher matcher = pattern.matcher(line);
					if (matcher.matches()) {
						int _i = Integer.parseInt(matcher.group(1));
						for (int tc = 0; tc < fileLength; tc++) {
							if (tools_init[tc] != null) {
								Tool t = tools_init[tc];
								if (t.getIndex() == _i) {
									lastSize = Float.valueOf(t.getSize());
								}
							}
						}
						tools[i] = new Tool(_i, lastSize);
						System.out.println("Found Tool usage no." + _i);
					}
				} else if (line.matches(tool_regex)) {
					Pattern pattern = Pattern.compile(tool_regex);
					Matcher matcher = pattern.matcher(line);
					if (matcher.matches()) {
						int _i = Integer.parseInt(matcher.group(1));
						float _size = Float.parseFloat(matcher.group(2));
						tools_init[i] = new Tool(_i, matcher.group(2));
						_size *= 1000;
						System.out.println("Found Tool no." + _i + " with size : " + _size + format);
					}
				} else if (line.startsWith("INCH,LZ")) {
					format = "mil";
					System.out.println("Measurement system : INCH");
				} else if (line.startsWith("INCH,TZ")) {
					format = "mil";
					System.out.println("Measurement system : INCH");
				} else if (line.startsWith("FMAT,2")) {
					fmat = true;
				}
				i++;
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Die Datei wurde nicht gefunden !");
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Es ist ein fehler w�hrend des schreibens der Datei aufgetreten.");
			e.printStackTrace();
		}
	}

	public String checkNulls(int index, int length) {
		// System.out.println("getting length ...");
		int add = length - String.valueOf(index).length();
		String ret = "";
		for (int i = 0; i < add; i++) {
			ret += "0";
			// System.out.println("Adding null !");
		}
		ret += String.valueOf(index);
		// System.out.println("return : " + ret);
		return ret;
	}

	public String checkNullsPoint(int index, int nulls) {
		String ret = "";
		for (int i = 0; i < nulls; i++) {
			ret += "0";
		}
		return ret + String.valueOf(index);
	}

	@Override
	public void createPreview(JTextArea ContentBox) {
		String nLine = String.format("%n");
		ContentBox.setText("");
		boolean toolsInitFlag = false;
		boolean pointsFlag = false;
		for (int i = 0; i < fileLength; i++) {
			if (tools_init[i] != null) {
				if (!toolsInitFlag) {
					toolsInitFlag = true;
					ContentBox.append("Defined tools : " + nLine);
					ContentBox.append(nLine);
				}
				Tool t = tools_init[i];
				String toolnumber = "";
				if (t.getIndex() < 10) {
					toolnumber = "0" + String.valueOf(t.getIndex());
				} else {
					toolnumber = String.valueOf(t.getIndex());
				}
				float size = Float.parseFloat(t.getSize()) * 1000;
				ContentBox.append("T" + toolnumber + " , size : " + size + format + nLine);
			} else if (points.get(i) != null) {
				if (!pointsFlag) {
					pointsFlag = true;
					ContentBox.append(nLine);
					ContentBox.append("Drill points : " + nLine);
					ContentBox.append(nLine);
				}
				Point p = points.get(i);
				if (p.getX() == -1) {
					ContentBox.append("Y" + checkNullsPoint(p.getY(), 2) + nLine);
				} else if (p.getY() == -1) {
					ContentBox.append("X" + checkNullsPoint(p.getX(), 2) + nLine);
				} else {
					ContentBox.append("X" + checkNullsPoint(p.getX(), 2) + "Y" + checkNullsPoint(p.getY(), 2) + nLine);
				}
			}
		}
	}

	@Override
	public void Gui(FileEnd f, MainGui p) {
		final FileEnd fEnd = f;
		final MainGui parent = p;
		final DrlConverter me = this;
		EventQueue.invokeLater(new Runnable() {
			DrlOptionsGui	gui;

			public void run() {
				gui = new DrlOptionsGui(fEnd, parent, me);
				gui.setVisible(true);
			}

		});
	}

	@Override
	public void createEndView(JTextArea ContentBox, ArrayList<ConvertOption> options) {
//		String nLine = String.format("%n");
//		if (options.contains(ConvertOption.Merge_tools)) {
//
//			ContentBox.setText("");
//			ContentBox.append("M72" + nLine);
//			ContentBox.append("M48" + nLine);
//			String fTool = "";
//			for (Tool t : tools_init) {
//				if (t != null) {
//					String toolnumber = "";
//					if (t.getIndex() < 10) {
//						toolnumber = "0" + String.valueOf(t.getIndex());
//					} else {
//						toolnumber = String.valueOf(t.getIndex());
//					}
//					fTool = "T" + toolnumber;
//					ContentBox.append("T" + toolnumber + "C" + t.getSize() + nLine);
//					break;
//				}
//			}
//			ContentBox.append("%" + nLine + fTool + nLine);
//			for (Point p : points) {
//				if (p != null) {
//					if (p.getX() == -1) {
//						ContentBox.append("Y" + checkNullsPoint(p.getY(), 2) + nLine);
//					} else if (p.getY() == -1) {
//						ContentBox.append("X" + checkNullsPoint(p.getX(), 2) + nLine);
//					} else {
//						ContentBox.append("X" + checkNullsPoint(p.getX(), 2) + "Y" + checkNullsPoint(p.getY(), 2) + nLine);
//					}
//				}
//			}
//		} else {
//			ContentBox.setText("");
//			ContentBox.append("M72" + nLine);
//			ContentBox.append("M48" + nLine);
//			int i = 2;
//			for (Tool t : tools_init) {
//				if (t != null) {
//					String toolnumber = "";
//					if (t.getIndex() < 10) {
//						toolnumber = "0" + String.valueOf(t.getIndex());
//					} else {
//						toolnumber = String.valueOf(t.getIndex());
//					}
//					String size = t.getSize();
//					String cmd = "T" + toolnumber + "C" + size;
//					ContentBox.append(cmd + nLine);
//					i++;
//				}
//			}
//			ContentBox.append("%" + nLine);
//			for (i = i; i < fileLength; i++) {
//				if (tools[i] != null) {
//					ContentBox.append("T" + checkNulls(tools[i].getIndex(), 2) + nLine);
//				} else if (points.get(i) != null) {
//					Point p = points.get(i);
//					if (p.getX() == -1) {
//						ContentBox.append("Y" + checkNullsPoint(p.getY(), 2) + nLine);
//						System.out.println("Writing point : Y = " + p.getY());
//					} else if (p.getY() == -1) {
//						ContentBox.append("X" + checkNullsPoint(p.getX(), 2) + nLine);
//						System.out.println("Writing point : X = " + p.getX());
//					} else {
//						ContentBox.append("X" + checkNullsPoint(p.getX(), 2) + "Y" + checkNullsPoint(p.getY(), 2) + nLine);
//						System.out.println("Writing point : X = " + p.getX() + " Y = " + p.getY());
//					}
//				}
//			}
//			ContentBox.append("M30" + nLine);
//		}
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	@Override
	public void clearSettings() {
		points = new ArrayList<Point>();
		tools_init = new Tool[30];
		tools = new Tool[30];
		format			= "unknown";
		fmat			= false;
		fmat_int		= 1000;
		fileLength		= 0;
	}
	
	public FabricationTypeConverter create(){
		return new DrlConverter();
	}
	
}
