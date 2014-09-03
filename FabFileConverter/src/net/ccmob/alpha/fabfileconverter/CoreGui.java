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

		mntmShowPreview.setActionCommand("show");
		mntmShowPreview.addActionListener(this);
		mnFile.add(mntmShowPreview);
		
		mnFile.add(mntmSearchForUpdates);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane,
				0, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 0,
				SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane,
				0, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane,
				0, SpringLayout.EAST, getContentPane());
		
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
		globals.set("SYS_textAreaScrollTop", new ZeroArgFunction() {
			
			@Override
			public LuaValue call() {
				txtrPreviewTextfield.select(0, 0);
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
								f.preview();
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
			case "show":{
				FabricationFileConverter f = CoreGui.checkFileEnd(currentFile);
				if(f != null){
					f.show();
				}else{
					CoreGui.txtrPreviewTextfield.setText("Could not find any converter for this file type.");
				}
				break;
			}
			default:
				break;
		}
	}

	/**
	 * @param menuBar the menuBar to set
	 */
	public void setMenuBar(JMenuBar menuBar) {
		this.menuBar = menuBar;
	}

	/**
	 * @return the mnFile
	 */
	public JMenu getMnFile() {
		return mnFile;
	}

	/**
	 * @param mnFile the mnFile to set
	 */
	public void setMnFile(JMenu mnFile) {
		this.mnFile = mnFile;
	}

	/**
	 * @return the mntmLoadFile
	 */
	public JMenuItem getMntmLoadFile() {
		return mntmLoadFile;
	}

	/**
	 * @param mntmLoadFile the mntmLoadFile to set
	 */
	public void setMntmLoadFile(JMenuItem mntmLoadFile) {
		this.mntmLoadFile = mntmLoadFile;
	}

	/**
	 * @return the mntmExportFile
	 */
	public JMenuItem getMntmExportFile() {
		return mntmExportFile;
	}

	/**
	 * @param mntmExportFile the mntmExportFile to set
	 */
	public void setMntmExportFile(JMenuItem mntmExportFile) {
		this.mntmExportFile = mntmExportFile;
	}

	/**
	 * @return the mntmShowPreview
	 */
	public JMenuItem getMntmShowPreview() {
		return mntmShowPreview;
	}

	/**
	 * @param mntmShowPreview the mntmShowPreview to set
	 */
	public void setMntmShowPreview(JMenuItem mntmShowPreview) {
		this.mntmShowPreview = mntmShowPreview;
	}

	/**
	 * @return the mntmSearchForUpdates
	 */
	public JMenuItem getMntmSearchForUpdates() {
		return mntmSearchForUpdates;
	}

	/**
	 * @param mntmSearchForUpdates the mntmSearchForUpdates to set
	 */
	public void setMntmSearchForUpdates(JMenuItem mntmSearchForUpdates) {
		this.mntmSearchForUpdates = mntmSearchForUpdates;
	}

	/**
	 * @return the txtrPreviewTextfield
	 */
	public static JTextArea getTxtrPreviewTextfield() {
		return txtrPreviewTextfield;
	}

	/**
	 * @param txtrPreviewTextfield the txtrPreviewTextfield to set
	 */
	public static void setTxtrPreviewTextfield(JTextArea txtrPreviewTextfield) {
		CoreGui.txtrPreviewTextfield = txtrPreviewTextfield;
	}

	/**
	 * @return the scrollPane
	 */
	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	/**
	 * @param scrollPane the scrollPane to set
	 */
	public void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}

	/**
	 * @return the currentFile
	 */
	public String getCurrentFile() {
		return currentFile;
	}

	/**
	 * @param currentFile the currentFile to set
	 */
	public void setCurrentFile(String currentFile) {
		this.currentFile = currentFile;
	}

	/**
	 * @return the fabFileConverter
	 */
	public static ArrayList<FabricationFileConverter> getFabFileConverter() {
		return fabFileConverter;
	}

	/**
	 * @param fabFileConverter the fabFileConverter to set
	 */
	public static void setFabFileConverter(
			ArrayList<FabricationFileConverter> fabFileConverter) {
		CoreGui.fabFileConverter = fabFileConverter;
	}

	/**
	 * @return the globals
	 */
	public Globals getGlobals() {
		return globals;
	}

	/**
	 * @param globals the globals to set
	 */
	public void setGlobals(Globals globals) {
		this.globals = globals;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
