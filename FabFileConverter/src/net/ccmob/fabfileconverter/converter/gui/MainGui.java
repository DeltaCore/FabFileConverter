package net.ccmob.fabfileconverter.converter.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;

import net.ccmob.fabfileconverter.FabFileConverter.Core;
import net.ccmob.fabfileconverter.converter.types.ConvertOption;
import net.ccmob.fabfileconverter.converter.types.ConverterInterface;
import net.ccmob.fabfileconverter.converter.types.FileEnd;

public class MainGui extends JFrame {

	private static final long	serialVersionUID	= 1L;
	public ConverterInterface	activeConverter;
	public String	            filename	      = "";

	private JPanel	          contentPane;
	public JTextArea	        ContentBox	    = new JTextArea();
	JMenuItem	                mntmLoadFile	  = new JMenuItem("Load file");
	JMenuItem	                mntmConvertFile	= new JMenuItem("Convert file");
	JMenuItem	                mntmOptions	    = new JMenuItem("Options");
	JMenuItem									mntmSettings		= new JMenuItem("Settings");
	
	public Core	              core;

	public ArrayList<ConvertOption>	  options	        = new ArrayList<ConvertOption>();

	public final MainGui	    me	            = this;

	private ButtonListener	  buttonListener;

	String	                  os	            = null;

	/**
	 * Create the frame.
	 */

	public MainGui(Core c, String f) {
		super("Fabrication file converter");
		buttonListener = new ButtonListener(c);
		// setTitle("Fabrication file converter");
		this.core = c;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		mnFile.add(mntmLoadFile);
		mntmLoadFile.setActionCommand("loadfile");
		mntmLoadFile.addActionListener(buttonListener);

		mnFile.add(mntmConvertFile);
		mntmConvertFile.setActionCommand("convert");
		mntmConvertFile.addActionListener(buttonListener);

		mnFile.add(mntmOptions);
		mntmOptions.setActionCommand("options");
		mntmOptions.addActionListener(buttonListener);
		
		mnFile.add(mntmSettings);
		mntmSettings.setActionCommand("settings");
		mntmSettings.addActionListener(buttonListener);
		
		contentPane = new JPanel();
		contentPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
		    null, null));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setAlignmentY(Component.TOP_ALIGNMENT);
		contentPane.add(scrollPane);
		ContentBox.setWrapStyleWord(true);
		scrollPane.setViewportView(ContentBox);
		ContentBox.setLineWrap(true);

		this.addWindowListener(new MainGuiWindowListener(c.settings));

		if (f != "file.is.not.selected") {
			this.setVisible(true);
			System.out.println("Filename : " + f);
			FileEnd f_ = core.checkFileEnd(f);
			mntmLoadFile.setEnabled(true);
			mntmOptions.setEnabled(true);
			mntmConvertFile.setEnabled(true);
			ArrayList<String> buttons = f_.getDisabledButtons();
			if (buttons.size() > 0) {
				for (String s : buttons) {
					if (s.equalsIgnoreCase("load file")) {
						mntmLoadFile.setEnabled(false);
					} else if (s.equalsIgnoreCase("options")) {
						mntmOptions.setEnabled(false);
					} else if (s.equalsIgnoreCase("convert")) {
						mntmConvertFile.setEnabled(false);
					}
				}
			}
			f_.init();
			f_.converter.readIn(f);
			ContentBox.setText("");
			f_.converter.createPreview(ContentBox);
			filename = f;
		}
		setShortCut('O', mntmLoadFile);
		setShortCut('P', mntmOptions);
		setShortCut('S', mntmConvertFile);
		
		JMenuItem mntmSearchForUpdates = new JMenuItem("Search for updates");
		mntmSearchForUpdates.addActionListener(buttonListener);
		mntmSearchForUpdates.setActionCommand("update");
		mnFile.add(mntmSearchForUpdates);
	}

	class ButtonListener implements ActionListener {

		Core	core;

		public ButtonListener(Core c) {
			this.core = c;
		}

		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			System.out.println("Pressed button : " + cmd);
			if (cmd.equalsIgnoreCase("loadfile")) {
				JFileChooser filedialog = new JFileChooser(core.settings.get(
				    "lastFolder", System.getProperty("user.home")));
				filedialog.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File f) {
						return f.isDirectory() || core.checkFileEndBoolean(f.getName());
					}

					@Override
					public String getDescription() {
						return "Fabrication files (.gm/g(1-8) | .drl)";
					}

				});
				int state = filedialog.showOpenDialog(null);
				if (state == JFileChooser.APPROVE_OPTION) {
					filename = filedialog.getSelectedFile().getAbsolutePath();
					core.settings.set("lastFolder", filename);
					FileEnd f = core.checkFileEnd(filename);
					ArrayList<String> buttons = f.getDisabledButtons();
					mntmLoadFile.setEnabled(true);
					mntmOptions.setEnabled(true);
					mntmConvertFile.setEnabled(true);
					if (buttons.size() > 0) {
						for (String s : buttons) {
							System.out.println("S : " + s);
							if (s.equalsIgnoreCase("load file")) {
								mntmLoadFile.setEnabled(false);
							} else if (s.equalsIgnoreCase("options")) {
								mntmOptions.setEnabled(false);
							} else if (s.equalsIgnoreCase("convert")) {
								mntmConvertFile.setEnabled(false);
							}
						}
					}
					f.init();
					f.converter.readIn(filename);
					ContentBox.setText("");
					f.converter.createPreview(ContentBox);
				}
			} else if (cmd.equalsIgnoreCase("convert")) {
				FileEnd f = core.checkFileEnd(filename);
				String name = JOptionPane.showInputDialog(null, "New filename : ");
				String sep = "";
				if(System.getProperty("os.name").startsWith("Windows")){
					sep = "\\";
				}else{
					sep = "/";
				}
				int index = filename.lastIndexOf(sep) + 1;
				String fName = filename.substring(index);
				System.out.println("FNAME : " + fName);
				f.converter.convert(filename.replace(fName, name + "." + f.endName),
				    options);
			} else if (cmd.equalsIgnoreCase("options")) {
				FileEnd f = core.checkFileEnd(filename);
				f.converter.Gui(f, me);
			} else if (cmd.equalsIgnoreCase("update")) {
				core.internalUpdater.lookForUpdates();
			}else if(cmd.equalsIgnoreCase("settings")){
				Info info = new Info(this.core);
				info.setVisible(true);
			}
		}
	}

	void setShortCut(char c, JMenuItem j) {
		if (os == null) {
			os = System.getProperty("os.name");
		}
		if (os.equalsIgnoreCase("Mac OS X")) {
			j.setAccelerator(KeyStroke.getKeyStroke(Character.toUpperCase(c),
			    InputEvent.META_MASK));
		} else {
			j.setAccelerator(KeyStroke.getKeyStroke(Character.toUpperCase(c),
			    InputEvent.CTRL_MASK));
		}
	}
}
