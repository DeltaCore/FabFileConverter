package net.ccmob.alpha.fabfileconverter;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import net.ccmob.alpha.fabfileconverter.config.Config;
import net.ccmob.alpha.fabfileconverter.types.FabricationFileConverter;
import net.ccmob.alpha.fabfileconverter.types.LuaFabricationType;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

public class CoreGui extends JFrame implements ActionListener {

	JMenuBar menuBar = new JMenuBar();
	JMenu mnFile = new JMenu("File");
	JMenuItem mntmLoadFile = new JMenuItem("Load file");
	JMenuItem mntmExportFile = new JMenuItem("Export file");
	JMenuItem mntmShowPreview = new JMenuItem("Show preview");
	JMenuItem mntmSearchForUpdates = new JMenuItem("Search for updates");
	static JTextArea txtrPreviewTextfield = new JTextArea();
	JScrollPane scrollPane = new JScrollPane();
	private Config config = new Config("config.cfg");

	String currentFile = "";

	public static ArrayList<FabricationFileConverter> fabFileConverter = new ArrayList<FabricationFileConverter>();

	/**
	 * 
	 * Lua support:
	 * 
	 */

	Globals globals = JsePlatform.standardGlobals();

	public CoreGui(String[] commandArgs) {
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
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 0,
				SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 0,
				SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, 0,
				SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, 0,
				SpringLayout.EAST, getContentPane());

		getContentPane().add(scrollPane);
		txtrPreviewTextfield.setText("");

		scrollPane.setViewportView(txtrPreviewTextfield);
		
		Lua.loadModule(this);
		
		System.out.println("Done.");
		this.setVisible(true);
		if(commandArgs.length == 1){
			String filename = commandArgs[0];
			System.out.println(filename);
			this.currentFile = new File(filename).getAbsolutePath();
			this.getConfig().setValue("lastFolderpath", currentFile);
			this.getConfig().save();
			FabricationFileConverter f = CoreGui
					.checkFileEnd(currentFile);
			if (f != null) {
				f.read(currentFile);
				CoreGui.txtrPreviewTextfield.setText("");
				f.preview();
			} else {
				CoreGui.txtrPreviewTextfield
						.setText("Could not find any converter for this file type.");
			}
		}
	}

	public void addConverter(FabricationFileConverter c) {
		if (!fabFileConverter.contains(c)) {
			fabFileConverter.add(c);
		}
	}

	public static boolean checkFileEndBoolean(String fileEnd) {
		for (FabricationFileConverter c : fabFileConverter) {
			if (c.checkFileEnd(fileEnd)) {
				return true;
			}
		}
		System.out.println("failed - lols :/");
		return false;
	}

	public static FabricationFileConverter checkFileEnd(String fileEnd) { // XXX
																			// main
																			// -
		// checkFileEnd : Working
		for (FabricationFileConverter c : fabFileConverter) {
			if (c.checkFileEnd(fileEnd)) {
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
		switch (e.getActionCommand()) {
		case "loadFile": {
			if (fabFileConverter.size() > 0) {
				this.getConfig().addDefault("lastFolderpath",
						System.getProperty("user.home"));
				this.getConfig().save();

				FileDialog filedialog = new FileDialog(this,
						"Select your fabrication file", FileDialog.LOAD);
				filedialog.setDirectory(this.getConfig().getValue(
						"lastFolderpath"));
				String files = "";
				for (int i = 0; i < fabFileConverter.size(); i++) {
					if (i - 1 == fabFileConverter.size())
						files += "*." + fabFileConverter.get(i).getName();
					else
						files += "*." + fabFileConverter.get(i).getName()
								+ " | ";

				}
				filedialog.setFile(files);
				filedialog.setVisible(true);
				if (filedialog.getFile() == null)
					System.out.println("You cancelled the choice");
				else {
					String filename = filedialog.getDirectory()
							+ (!filedialog.getDirectory().endsWith(
									File.separator) ? File.separator
									: "") + filedialog.getFile();
					System.out.println(filename);
					this.currentFile = new File(filename).getAbsolutePath();
					this.getConfig().setValue("lastFolderpath", currentFile);
					this.getConfig().save();
					FabricationFileConverter f = CoreGui
							.checkFileEnd(currentFile);
					if (f != null) {
						f.read(currentFile);
						CoreGui.txtrPreviewTextfield.setText("");
						f.preview();
					} else {
						CoreGui.txtrPreviewTextfield
								.setText("Could not find any converter for this file type.");
					}
				}

				/*
				 * JFileChooser filedialog = new
				 * JFileChooser(this.getConfig().getValue("lastFolderpath"));
				 * filedialog.setFileFilter(new FileFilter() {
				 * 
				 * @Override public boolean accept(File f) { return
				 * f.isDirectory() || CoreGui.checkFileEndBoolean(f.getName());
				 * }
				 * 
				 * @Override public String getDescription() { return
				 * "Fabrication files (.gm/g(1-8) | .drl)"; }
				 * 
				 * }); int state = filedialog.showOpenDialog(null); if (state ==
				 * JFileChooser.APPROVE_OPTION) { this.currentFile =
				 * filedialog.getSelectedFile().getAbsolutePath();
				 * this.getConfig().setValue("lastFolderpath", currentFile);
				 * this.getConfig().save(); FabricationFileConverter f =
				 * CoreGui.checkFileEnd(currentFile); if(f != null){
				 * f.read(currentFile);
				 * CoreGui.txtrPreviewTextfield.setText(""); f.preview(); }else{
				 * CoreGui.txtrPreviewTextfield.setText(
				 * "Could not find any converter for this file type."); } }
				 */
			} else {
				System.err
						.println("Please check your installation. No converters found.");
			}
			break;
		}
		case "exportFile": {
			FabricationFileConverter f = CoreGui.checkFileEnd(currentFile);
			if (f != null) {
				f.convert(this.currentFile.substring(0,
						this.currentFile.lastIndexOf("."))
						+ "." + f.getConvertedFileEnding());
			} else {
				CoreGui.txtrPreviewTextfield
						.setText("Could not find any converter for this file type.");
			}
			break;
		}
		case "show": {
			FabricationFileConverter f = CoreGui.checkFileEnd(currentFile);
			if (f != null) {
				f.show();
			} else {
				CoreGui.txtrPreviewTextfield
						.setText("Could not find any converter for this file type.");
			}
			break;
		}
		default:
			break;
		}
	}

	/**
	 * @param menuBar
	 *            the menuBar to set
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
	 * @param mnFile
	 *            the mnFile to set
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
	 * @param mntmLoadFile
	 *            the mntmLoadFile to set
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
	 * @param mntmExportFile
	 *            the mntmExportFile to set
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
	 * @param mntmShowPreview
	 *            the mntmShowPreview to set
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
	 * @param mntmSearchForUpdates
	 *            the mntmSearchForUpdates to set
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
	 * @param txtrPreviewTextfield
	 *            the txtrPreviewTextfield to set
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
	 * @param scrollPane
	 *            the scrollPane to set
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
	 * @param currentFile
	 *            the currentFile to set
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
	 * @param fabFileConverter
	 *            the fabFileConverter to set
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
	 * @param globals
	 *            the globals to set
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

	/**
	 * @return the config
	 */
	public Config getConfig() {
		return config;
	}

	/**
	 * @param config
	 *            the config to set
	 */
	public void setConfig(Config config) {
		this.config = config;
	}
}
