package net.ccmob.fabfileconverter.converter.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.ccmob.fabfileconverter.FabFileConverter.Core;

public class Info extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel					contentPane;
	private ButtonListener	listener;

	JCheckBox								chckbxDebug				= new JCheckBox("Debugmode");
	JCheckBox								chckbxShowGuiWhen	= new JCheckBox("Show gui when converting file");

	JButton									btnSave						= new JButton("Save");
	JButton									btnSaveAndExit		= new JButton("Save and exit");
	JButton									btnCancel					= new JButton("Cancel");

	public Info(Core core) {
		listener = new ButtonListener(core, this);
		setTitle("Settings");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 362, 171);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		chckbxDebug.setBounds(6, 7, 114, 35);
		chckbxDebug.setActionCommand("debugmode");
		chckbxDebug.addActionListener(listener);
		chckbxDebug.setSelected(core.settings.getBoolean(core.showDebug));
		contentPane.add(chckbxDebug);

		chckbxShowGuiWhen.setBounds(6, 45, 206, 23);
		chckbxShowGuiWhen.setActionCommand("debugoncmd");
		chckbxShowGuiWhen.addActionListener(listener);
		chckbxShowGuiWhen.setSelected(core.settings.getBoolean(core.showGui));
		contentPane.add(chckbxShowGuiWhen);

		JLabel lblFabfileconverter = new JLabel("FabFileConverter VX.X");
		lblFabfileconverter.setBounds(6, 75, 121, 14);
		contentPane.add(lblFabfileconverter);

		JLabel lblcMarcelBenning = new JLabel("(C) Marcel Benning");
		lblcMarcelBenning.setBounds(6, 100, 121, 14);
		contentPane.add(lblcMarcelBenning);

		btnSave.setBounds(222, 13, 114, 23);
		btnSave.setActionCommand("save");
		btnSave.addActionListener(listener);
		contentPane.add(btnSave);

		btnSaveAndExit.setBounds(222, 45, 114, 23);
		btnSaveAndExit.setActionCommand("save_e");
		btnSaveAndExit.addActionListener(listener);
		contentPane.add(btnSaveAndExit);
		
		btnCancel.setBounds(222, 75, 114, 23);
		btnCancel.setActionCommand("cancel");
		btnCancel.addActionListener(listener);
		contentPane.add(btnCancel);
	}

	class ButtonListener implements ActionListener {

		private String	cmd	= "";
		private Core		core;
		private Info		info;

		public ButtonListener(Core core, Info info) {
			this.setCore(core);
			this.setInfo(info);
		}

		public void actionPerformed(ActionEvent e) {
			this.setCmd(e.getActionCommand());
			if (isCmd("debugmode")) {
				if (this.getInfo().chckbxDebug.isSelected()) {
					this.getCore().settings.set(this.getCore().showDebug, "true");
				} else {
					this.getCore().settings.set(this.getCore().showDebug, "false");
				}
			} else if (isCmd("debugoncmd")) {
				if (this.getInfo().chckbxShowGuiWhen.isSelected()) {
					this.getCore().settings.set(this.getCore().showGui, "true");
				} else {
					this.getCore().settings.set(this.getCore().showGui, "false");
				}
			} else if (isCmd("save")) {
				this.getCore().settings.save();
			} else if (isCmd("save_e")) {
				this.getCore().settings.save();
				this.getInfo().dispose();
			} else if (isCmd("cancel")) {
				this.getInfo().dispose();
			}
		}

		public String getCmd() {
			return cmd;
		}

		public void setCmd(String cmd) {
			this.cmd = cmd;
		}

		public boolean isCmd(String val) {
			return this.getCmd().equalsIgnoreCase(val);
		}

		public Core getCore() {
			return core;
		}

		public void setCore(Core core) {
			this.core = core;
		}

		public Info getInfo() {
			return info;
		}

		public void setInfo(Info info) {
			this.info = info;
		}

	}

}
