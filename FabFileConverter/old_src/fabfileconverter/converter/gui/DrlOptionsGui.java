package net.ccmob.fabfileconverter.converter.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import net.ccmob.fabfileconverter.converter.reader.DrlConverter;
import net.ccmob.fabfileconverter.converter.types.ConvertOption;
import net.ccmob.fabfileconverter.converter.types.FileEnd;

public class DrlOptionsGui extends JFrame {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private JPanel	          contentPane;
	JTextArea	                Contentbox	     = new JTextArea();
	JLabel	                  scaleValue;
	private ArrayList<ConvertOption> options = new ArrayList<ConvertOption>();
	private ArrayList<ConvertOption> activeOptions = new ArrayList<ConvertOption>();

	DrlGuiView	              gui;
	GuiHandler	              guiHandler;
	double	                  hole_scale	     = 1.0F;
	int	                      xOrigin	         = 0;
	int	                      yOrigin	         = 0;

	/**
	 * Create the frame.
	 * 
	 * @param c
	 */

	public void startHoleView(DrlConverter c, String f) {
		guiHandler = new GuiHandler(gui, c, f);
		gui = guiHandler.gui;
	}

	public DrlOptionsGui(FileEnd f, MainGui par, DrlConverter c) {
		super();
		this.options.add(ConvertOption.Merge_tools);
		File file = new File(par.filename);
		setTitle(file.getName() + " Options");
		setBounds(100, 100, 595, 302);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
			

		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);
		for(ConvertOption o : this.options){
			JMenuItem i = new JMenuItem(o.toString().replace("_", " "));
			i.setActionCommand("option_" + o.toString().toLowerCase().replace(" ", "_"));
			mnOptions.add(i);
		}			

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		scaleValue = new JLabel();
		scaleValue.setText(" : " + hole_scale + " ");

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		Contentbox.setText("ContentBox");

		scrollPane.setViewportView(Contentbox);
		f.converter.createEndView(Contentbox, this.activeOptions);
		startHoleView(c, c.getFormat());
	}

	class GuiHandler {
		public DrlGuiView	gui;

		public GuiHandler(DrlGuiView gui, DrlConverter c, String format) {
			this.gui = gui;
			this.gui = new DrlGuiView(c, format);
			this.gui.setVisible(true);
		}
	}

	class ButtonListener implements ActionListener {

		private DrlOptionsGui gui;
		
		public ButtonListener(DrlOptionsGui gui) {
			this.gui = gui;
		}
		
		public void actionPerformed(ActionEvent e) {
			//String cmd = e.getActionCommand();
			for(ConvertOption o : this.gui.options){
				if(e.equals("option_" + o.toString().toLowerCase().replace(" ", "_"))){
					if(this.gui.activeOptions.contains(o))
						this.gui.activeOptions.remove(o);
					else
						this.gui.activeOptions.add(o);
				}
			}
		}

	}

}
