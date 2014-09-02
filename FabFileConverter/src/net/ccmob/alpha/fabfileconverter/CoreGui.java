package net.ccmob.alpha.fabfileconverter;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileFilter;

import net.ccmob.alpha.fabfileconverter.types.FabricationFileConverter;
import net.ccmob.alpha.fabfileconverter.types.LuaFabricationType;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

public class CoreGui extends JFrame implements ActionListener{

	JMenuBar menuBar = new JMenuBar();
	JMenu mnFile = new JMenu("File");
	JMenuItem mntmLoadFile = new JMenuItem("Load file");
	JMenuItem mntmExportFile = new JMenuItem("Export file");
	JMenuItem mntmShowPreview = new JMenuItem("Show preview");
	JMenuItem mntmSearchForUpdates = new JMenuItem("Search for updates");
	static JTextArea txtrPreviewTextfield = new JTextArea();
	JScrollPane scrollPane = new JScrollPane();
	
	String currentFile = "";
	
	public static ArrayList<FabricationFileConverter> fabFileConverter = new ArrayList<FabricationFileConverter>();
	
	/**
	 * 
	 * Lua support:
	 * 
	 */
	
	private Globals globals = JsePlatform.standardGlobals();
	
	public CoreGui() {
		setTitle("Fabrication file converter");
		this.setSize(500, 300);
		this.setPreferredSize(new Dimension(500, 300));
		setJMenuBar(menuBar);

		menuBar.add(mnFile);
		
		mntmLoadFile.setActionCommand("loadFile");
		mntmLoadFile.addActionListener(this);
		mnFile.add(mntmLoadFile);

		mntmExportFile.setActionCommand("exportFile");
		mntmExportFile.addActionListener(this);
		mnFile.add(mntmExportFile);

		mntmShowPreview.setActionCommand("preview");
		mntmShowPreview.addActionListener(this);
		mnFile.add(mntmShowPreview);
		
		mnFile.add(mntmSearchForUpdates);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane,
				10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10,
				SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane,
				-10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane,
				-10, SpringLayout.EAST, getContentPane());
		
		getContentPane().add(scrollPane);
		txtrPreviewTextfield.setText("");
		
		scrollPane.setViewportView(txtrPreviewTextfield);
		System.out.println("Loading lua converters ...");
		globals.set("SYS_createConverter", new ThreeArgFunction(){
			@Override
			public LuaValue call(LuaValue name, LuaValue regex, LuaValue newEnding) {
				addConverter(new LuaFabricationType(name.toString(), regex.toString(), newEnding.toString(), globals));
				return null;
			}
		});
		globals.set("SYS_clearTextArea", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				txtrPreviewTextfield.setText("");
				return null;
			}
		});
		globals.set("SYS_textAreaAddLine", new OneArgFunction(){
			@Override
			public LuaValue call(LuaValue line) {
				txtrPreviewTextfield.append(line.toString() + String.format("%n"));
				return null;
			}
			
		});
		globals.loadfile("res/main.lua").call();
		
		System.out.println("Done.");
		for(FabricationFileConverter c : fabFileConverter){
			System.out.println(c);
		}
		this.setVisible(true);
	}
	
	public void addConverter(FabricationFileConverter c){
		if(!fabFileConverter.contains(c)){
			fabFileConverter.add(c);
		}
	}

	public static boolean checkFileEndBoolean(String fileEnd) {
		for (FabricationFileConverter c : fabFileConverter) {
			if(c.checkFileEnd(fileEnd)){
				return true;
			}
		}
		System.out.println("failed - lols :/");
		return false;
	}
	
	public static FabricationFileConverter checkFileEnd(String fileEnd) { // XXX main -
		// checkFileEnd : Working
		for (FabricationFileConverter c : fabFileConverter) {
			if(c.checkFileEnd(fileEnd)){
				return c;
			}
		}
		System.out.println("failed BLUBAS");
		return null;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6914900843410741176L;

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
			case "loadFile":{
				if(fabFileConverter.size() > 0){
					
					JFileChooser filedialog = new JFileChooser(System.getProperty("user.home"));
						filedialog.setFileFilter(new FileFilter() {

							@Override
							public boolean accept(File f) {
								return f.isDirectory() || CoreGui.checkFileEndBoolean(f.getName());
							}

							@Override
							public String getDescription() {
								return "Fabrication files (.gm/g(1-8) | .drl)";
							}

						});
						int state = filedialog.showOpenDialog(null);
						if (state == JFileChooser.APPROVE_OPTION) {
							this.currentFile = filedialog.getSelectedFile().getAbsolutePath();
							FabricationFileConverter f = CoreGui.checkFileEnd(currentFile);
							if(f != null){
								f.read(currentFile);
								CoreGui.txtrPreviewTextfield.setText("");
								f.peview();
							}else{
								CoreGui.txtrPreviewTextfield.setText("Could not find any converter for this file type.");
							}
						}
				}else{
					System.err.println("Please check your installation. No converters found.");
				}
				break;
			}
			case "exportFile":{
				FabricationFileConverter f = CoreGui.checkFileEnd(currentFile);
				if(f != null){
					f.convert(this.currentFile.substring(0, this.currentFile.lastIndexOf(".")) + "." + f.getConvertedFileEnding());
				}else{
					CoreGui.txtrPreviewTextfield.setText("Could not find any converter for this file type.");
				}
				break;
			}
			default:
				break;
		}
	}
}
