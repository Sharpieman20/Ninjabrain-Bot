package bot.ninjabrain.gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import bot.Main;
import bot.ninjabrain.io.VersionURL;
import bot.ninjabrain.gui.components.FlatButton;
import bot.ninjabrain.gui.components.ThemedFrame;
import bot.ninjabrain.gui.components.ThemedLabel;
import bot.ninjabrain.gui.components.TitleBarButton;

public class NotificationsFrame extends ThemedFrame {	
	
	private static final long serialVersionUID = 1767257205939839293L;
	
	static final int PADDING = 6;
	
	GUI gui;
	VersionURL url;
	
	JPanel mainPanel;
	ThemedLabel label;

	public NotificationsFrame(GUI gui) {
		super(gui, "New version available!");
		this.gui = gui;
		titlebarPanel.addButton(getExitButton());
		mainPanel = new JPanel();
		mainPanel.setOpaque(false);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(new EmptyBorder(PADDING - 3, PADDING, PADDING, PADDING));
		add(mainPanel);
		label = new ThemedLabel(gui, "");
		label.setVerticalAlignment(SwingConstants.TOP);
		mainPanel.add(label);
		mainPanel.add(Box.createVerticalStrut(PADDING));
		FlatButton downloadButton = new FlatButton(gui, "Download .jar");
		downloadButton.addActionListener(p -> openURL());
		mainPanel.add(downloadButton);
		mainPanel.add(Box.createVerticalStrut(PADDING));
		FlatButton changelogButton = new FlatButton(gui, "Open changelog (in browser)");
		changelogButton.addActionListener(p -> openReleasePage());
		mainPanel.add(changelogButton);
	}
	
	@Override
	public void updateBounds(GUI gui) {
		super.updateBounds(gui);
		label.setMaximumSize(new Dimension(gui.size.WIDTH, Integer.MAX_VALUE));
		setSize(gui.size.WIDTH * 7/8, gui.size.WIDTH * 5/8);
		setShape(new RoundRectangle2D.Double(0, 0, gui.size.WIDTH * 7/8, gui.size.WIDTH * 5/8, gui.size.WINDOW_ROUNDING, gui.size.WINDOW_ROUNDING));
	}
	
	public void updateFontsAndColors() {
		getContentPane().setBackground(gui.theme.COLOR_NEUTRAL);
		setBackground(gui.theme.COLOR_NEUTRAL);
	}
	
	private FlatButton getExitButton() {
		URL iconURL = Main.getResource("exit_icon.png");
		ImageIcon img = new ImageIcon(iconURL);
		FlatButton button = new TitleBarButton(gui, img) {
			private static final long serialVersionUID = 4380111129291481489L;
			@Override
			public Color getHoverColor(Theme theme) {
				return theme.COLOR_EXIT_BUTTON_HOVER;
			}
		};
		button.addActionListener(p -> setVisible(false));
		return button;
	}

	private void openURL() {
		try {
			Desktop.getDesktop().browse(new URL(url.url).toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void openReleasePage() {
		try {
			Desktop.getDesktop().browse(new URL(url.html_url).toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setURL(VersionURL url) {
		this.url = url;
		label.setText("<html>Version " + url.tag + " is available.  After downloading the new jar you can delete this jar (your settings will automatically transfer). This notification can be disabled in the settings menu.</html>");
		updateBounds(gui);
	}

	public Object getURL() {
		return url;
	}
	
}
