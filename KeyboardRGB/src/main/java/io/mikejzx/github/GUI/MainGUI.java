package io.mikejzx.github.GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import io.mikejzx.github.KeyboardRGB.MainClass;
import io.mikejzx.github.KeyboardRGB.MainClass.LEDMode;

public class MainGUI implements ChangeListener {

	public static JButton btnApply;
	
	private JFrame frmMainFrame;
	
	private static Map<JRadioButton, Integer> tabIndices;
	private static Map<Integer, MainClass.LEDMode> tabMode;
	private static JTabbedPane tabs_ledmode;
	
	private static int selectedTab;
	private static JLabel lblConnection;
	private static final String CONNECTION_FALS_STR = "<html>Connection Status: <font color=red>Not connected</font></html>";
	private static final String CONNECTION_TRUE_STR = "<html>Connection Status: <font color=green>O.K !</font></html>";
	
	private static final Color COLOUR_TRANS =  new Color(0.0f, 0.0f, 0.0f, 0.0f);
	
	public void initialise() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGUI window = new MainGUI();
					frmMainFrame.setLocationByPlatform(true);
					window.frmMainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainGUI() throws URISyntaxException {
		init();
	}

	private void init() throws URISyntaxException {
		frmMainFrame = new JFrame();
		frmMainFrame.setTitle("Mike's Poseidon Z RGB Controller");
		frmMainFrame.setResizable(false);
		frmMainFrame.setBounds(100, 100, 600, 450);
		frmMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Override height.
		//tabs_ledmode.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() { @Override protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) { return 20; } } );
		ButtonGroup group = new ButtonGroup();
		LEDMode[] modes = LEDMode.values();
		tabIndices = new HashMap<JRadioButton, Integer>();
		tabMode = new HashMap<Integer, LEDMode>();
		tabs_ledmode = new JTabbedPane(JTabbedPane.TOP);
		
		JPanel panel_backl = new JPanel();
		JPanel panel_react = new JPanel();
		JPanel panel_waveh = new JPanel();
		JPanel panel_wavev = new JPanel();
		JPanel[] tabPanels = new JPanel[] {
			panel_backl,
			panel_react,
			panel_waveh,
			panel_wavev,
		};
		
		tabs_ledmode.addTab("New tab", null, panel_backl, null);
		tabs_ledmode.addTab("New tab", null, panel_react, null);
		tabs_ledmode.addTab("New tab", null, panel_waveh, null);
		tabs_ledmode.addTab("New tab", null, panel_wavev, null);
		
		tabs_ledmode.addChangeListener(this);
		
		JPanel panel_bottom = new JPanel();
		
		JLabel lblVersion = new JLabel("Version: " + MainClass.SOFTWARE_VERSION);
		lblVersion.setHorizontalAlignment(SwingConstants.LEFT);
		lblConnection = new JLabel("<html>Connection Status: Loading...</html>");
		
		btnApply = new JButton("Apply");
		btnApply.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnApply.setEnabled(false);
		btnApply.setToolTipText("Apply the LED changes.");
		btnApply.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					int x = 0;
					int idx = tabs_ledmode.getSelectedIndex();
					Enumeration<AbstractButton> e = group.getElements();
					for (Enumeration<AbstractButton> i = e; e.hasMoreElements();) {
						JRadioButton src = (JRadioButton)i.nextElement();
						if (x == idx) {
							src.setSelected(true);
							MainGUI.changeTab(src);
						}
						x++;
					}
					btnApply.setEnabled(false);
					applyLEDMode();
				}
			});
		GroupLayout groupLayout = new GroupLayout(frmMainFrame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(6)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(panel_bottom, GroupLayout.PREFERRED_SIZE, 582, GroupLayout.PREFERRED_SIZE)
						.addComponent(tabs_ledmode, GroupLayout.PREFERRED_SIZE, 582, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(6)
					.addComponent(tabs_ledmode, GroupLayout.PREFERRED_SIZE, 303, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(panel_bottom, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		JLabel lblDevelopedByMichael = new JLabel("Developed by Michael");
		
		// Open website link.
		// SRC:: https://stackoverflow.com/questions/527719/how-to-add-hyperlink-in-jlabel#527877
		// Thanks to McDowell
		final String BTN_LINK = "https://mikejzx.github.io";
		final String BTN_DISP = "https://mikejzx.github.io";
		JButton btnWebsite = new JButton(BTN_DISP);
		final URI uri = new URI(BTN_LINK);
		class OpenUrlAction implements ActionListener {
			@Override public void actionPerformed (ActionEvent e) { openUrl (uri); }
		}
		btnWebsite.setText("<html><font color=\"#000099\"><u>" + BTN_DISP + "</u></font></html>");
		btnWebsite.setBorderPainted(false);
		btnWebsite.setOpaque(false);
		btnWebsite.setBackground(COLOUR_TRANS);
		btnWebsite.setToolTipText("The developer's website.\nClick to follow link...");
		btnWebsite.addActionListener(new OpenUrlAction());
		btnWebsite.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnWebsite.setBorder(BorderFactory.createCompoundBorder(
		        BorderFactory.createLineBorder(Color.CYAN, 5), 
		        BorderFactory.createEmptyBorder(-5, -10, -5, -10)));
		
		GroupLayout gl_panel_bottom = new GroupLayout(panel_bottom);
		gl_panel_bottom.setHorizontalGroup(
			gl_panel_bottom.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_bottom.createSequentialGroup()
					.addGroup(gl_panel_bottom.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_bottom.createSequentialGroup()
							.addGap(260)
							.addComponent(btnApply))
						.addGroup(gl_panel_bottom.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblConnection))
						.addGroup(gl_panel_bottom.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnWebsite, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
							.addGap(39)
							.addComponent(lblVersion)
							.addGap(44)
							.addComponent(lblDevelopedByMichael)))
					.addContainerGap())
		);
		gl_panel_bottom.setVerticalGroup(
			gl_panel_bottom.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_bottom.createSequentialGroup()
					.addComponent(btnApply)
					.addPreferredGap(ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
					.addComponent(lblConnection)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_bottom.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnWebsite)
						.addComponent(lblVersion)
						.addComponent(lblDevelopedByMichael))
					.addContainerGap())
		);
		panel_bottom.setLayout(gl_panel_bottom);
		frmMainFrame.getContentPane().setLayout(groupLayout);

		for (int i = 0; i < tabPanels.length; i++) {
			//if (!modes[i].getImplemented()) { continue; }
			LEDMode mode = modes[i];
			tabMode.put(i, mode);
			JRadioButton tabRadio = new JRadioButton("");
			tabRadio.setBackground(COLOUR_TRANS);
			tabRadio.setOpaque(false);
			tabRadio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (arg0.getSource() == tabRadio) {
						MainGUI.changeTab((JRadioButton)tabRadio);
					}
					applyLEDMode();
				}
			});
			JPanel tabLabel = new JPanel();
			JLabel lab = new JLabel(mode.name());
			if (!mode.getImplemented()) {
				tabs_ledmode.setEnabledAt(i, false);
				lab.setForeground(new Color(172, 172, 172));
				tabRadio.setEnabled(false);
			}
			tabLabel.setOpaque(false);
			tabLabel.add(lab);
			tabLabel.add(tabRadio);
			tabs_ledmode.setTabComponentAt(i, tabLabel);
			tabIndices.put(tabRadio, i);
			group.add(tabRadio);
			if (i == 0) { tabRadio.setSelected(true); }
		}
	}

	public static void changeTab(JRadioButton src) {
		int idx = tabIndices.get(src);
		tabs_ledmode.setSelectedIndex(idx);
		selectedTab = idx;
		btnApply.setEnabled(false);
	}

	// On tab change
	@Override
	public void stateChanged(ChangeEvent arg0) {
		if (selectedTab != tabs_ledmode.getSelectedIndex()) {
			btnApply.setEnabled(true);
		}
		else {
			btnApply.setEnabled(false);
		}
	}
	
	private void applyLEDMode() {
		LEDMode mode = tabMode.get(tabs_ledmode.getSelectedIndex());
		MainClass.setLEDMode(mode);
	}
	
	public static void setConectionStatus (boolean connected) {
		String str = connected ? CONNECTION_TRUE_STR : CONNECTION_FALS_STR;
		lblConnection.setText(str);
	}
	
	private static void openUrl(URI uri) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(uri);
			} 
			catch (IOException e) {  
				e.printStackTrace();
			}
		} 
		else { 
			System.err.println("Browser not supproted.");
		}
	}
}
