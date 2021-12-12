package bot.ninjabrain.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import bot.Main;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import bot.ninjabrain.gui.components.CalibrationPanel;
import bot.ninjabrain.gui.components.CustomCheckbox;
import bot.ninjabrain.gui.components.DecimalTextField;
import bot.ninjabrain.gui.components.Divider;
import bot.ninjabrain.gui.components.FlatButton;
import bot.ninjabrain.gui.components.RadioButtonGroup;
import bot.ninjabrain.gui.components.ThemedFrame;
import bot.ninjabrain.gui.components.ThemedLabel;
import bot.ninjabrain.gui.components.ThemedPanel;
import bot.ninjabrain.gui.components.TitleBarButton;
import bot.ninjabrain.io.BooleanPreference;
import bot.ninjabrain.io.FloatPreference;
import bot.ninjabrain.io.HotkeyPreference;
import bot.ninjabrain.io.KeyboardListener;
import bot.ninjabrain.io.MultipleChoicePreference;

public class OptionsFrame extends ThemedFrame {

	private static final long serialVersionUID = 8033865173874423916L;

	private GUI gui;
	private JPanel settingsPanel;
	private CalibrationPanel calibrationPanel;
	private FlatButton exitButton;
	private TextboxPanel sigma;
	private JPanel mainPanel; // Panel containing all non-advanced options
	private JPanel advPanel; // Panel containing all advanced options

	static final int WINDOW_WIDTH = 560;
	static final int COLUMN_WIDTH = WINDOW_WIDTH/2;
	static final int PADDING = 6;
	
	private static final String TITLE_TEXT = "Settings";

	public OptionsFrame(GUI gui) {
		super(gui, TITLE_TEXT);
		this.gui = gui;
		
		// Windows that can be swapped between
		settingsPanel = new JPanel();
		settingsPanel.setOpaque(false);
		settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
		add(settingsPanel);
		calibrationPanel = new CalibrationPanel(gui, this);
		add(calibrationPanel);
		
		// Title bar
		exitButton = getExitButton();
		titlebarPanel.addButton(exitButton);
		titlebarPanel.setFocusable(true);
		
		// Main panel
		mainPanel = new JPanel();
		mainPanel.setOpaque(false);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(new EmptyBorder(PADDING - 3, PADDING, PADDING, PADDING));
		settingsPanel.add(mainPanel);
		JPanel columns = new JPanel();
		columns.setOpaque(false);
		columns.setLayout(new GridLayout(1, 2));
		JPanel column1 = new JPanel();
		JPanel column2 = new JPanel();
		column1.setOpaque(false);
		column2.setOpaque(false);
		columns.add(column1);
		columns.add(column2);
		column1.setLayout(new BoxLayout(column1, BoxLayout.Y_AXIS));
		column2.setLayout(new BoxLayout(column2, BoxLayout.Y_AXIS));
		mainPanel.add(columns);
		
		// Column 1
		column1.add(new CheckboxPanel(gui, "Show nether coordinates", Main.preferences.showNetherCoords));
		column1.add(new CheckboxPanel(gui, "Auto reset when idle for 15 minutes", Main.preferences.autoReset));
		column1.add(new CheckboxPanel(gui, "Always on top", Main.preferences.alwaysOnTop));
		column1.add(new CheckboxPanel(gui, "Translucent window", Main.preferences.translucent));
		column1.add(new CheckboxPanel(gui, "Notify when a new version is available", Main.preferences.checkForUpdates));
		
		// Column 2
		column2.add(Box.createVerticalStrut(10));
		column2.add(new RadioButtonPanel(gui, "Display stronghold location using", Main.preferences.strongholdDisplayType));
		column2.add(new RadioButtonPanel(gui, "Theme", Main.preferences.theme));
		column2.add(new RadioButtonPanel(gui, "Window size", Main.preferences.size));
		column2.add(Box.createGlue());
		column2.add(Box.createGlue());
		column2.add(Box.createGlue());
		
		// Advanced panel
		mainPanel.add(Box.createVerticalStrut(PADDING));
		mainPanel.add(new Divider(gui));
		mainPanel.add(Box.createVerticalStrut(PADDING));
		mainPanel.add(new CheckboxPanel(gui, "Show advanced options", Main.preferences.showAdvancedOptions));
		advPanel = new JPanel();
		advPanel.setOpaque(false);
		advPanel.setLayout(new GridLayout(1, 2, PADDING, 0));
		advPanel.setBorder(new EmptyBorder(0, PADDING, PADDING, PADDING));
		advPanel.setVisible(Main.preferences.showAdvancedOptions.get());
		JPanel ac1 = new JPanel();
		JPanel ac2 = new JPanel();
		ac1.setOpaque(false);
		ac2.setOpaque(false);
		advPanel.add(ac1);
		advPanel.add(ac2);
		ac1.setLayout(new BoxLayout(ac1, BoxLayout.Y_AXIS));
		ac2.setLayout(new BoxLayout(ac2, BoxLayout.Y_AXIS));
		settingsPanel.add(advPanel);
		
		sigma = new TextboxPanel(gui, "Standard deviation: ", Main.preferences.sigma);
		ac1.add(sigma);
		JButton calibrateButton = new FlatButton(gui, "Calibrate standard deviation") {
			private static final long serialVersionUID = -673676238214760361L;
			@Override
			public int getTextSize(SizePreference p) {
				return p.TEXT_SIZE_SMALL;
			}
		};
		calibrateButton.addActionListener(p -> startCalibrating());
		calibrateButton.setAlignmentX(0.5f);
		ac1.add(calibrateButton);
		ac1.add(new CheckboxPanel(gui, "Show angle errors", Main.preferences.showAngleErrors));
		ac1.add(new CheckboxPanel(gui, "Use advanced stronghold statistics", Main.preferences.useAdvStatistics));
		ac1.add(Box.createGlue());
		if (KeyboardListener.registered) {
			ThemedLabel labelShortcuts = new ThemedLabel(gui, "Keyboard shortcuts", false);
			labelShortcuts.setAlignmentX(0.5f);
			ac2.add(labelShortcuts);
			ac2.add(new Divider(gui));
			ac2.add(Box.createVerticalStrut(4));
			ac2.add(new HotkeyPanel(gui, "+0.01 to last angle", Main.preferences.hotkeyIncrement));
			ac2.add(Box.createVerticalStrut(4));
			ac2.add(new HotkeyPanel(gui, "-0.01 to last angle", Main.preferences.hotkeyDecrement));
			ac2.add(Box.createVerticalStrut(4));
			ac2.add(new HotkeyPanel(gui, "Reset", Main.preferences.hotkeyReset));
			ac2.add(Box.createVerticalStrut(4));
			ac2.add(new HotkeyPanel(gui, "Undo", Main.preferences.hotkeyUndo));
			ac2.add(Box.createVerticalStrut(4));
			ac2.add(new HotkeyPanel(gui, "Hide/show window", Main.preferences.hotkeyMinimize));
			ac2.add(Box.createVerticalStrut(4));
			ac2.add(new HotkeyPanel(gui, "Blind travel mode", Main.preferences.hotkeyBlind));
			ac2.add(Box.createVerticalStrut(4));
			ac2.add(new HotkeyPanel(gui, "In-game chat hotkey (for divine)", Main.preferences.hotkeyStartEnteringDivine));
		}
	}
	
	private void startCalibrating() {
		calibrationPanel.startCalibrating();
		settingsPanel.setVisible(false);
		titlebarPanel.setVisible(false);
		calibrationPanel.setVisible(true);
		if (KeyboardListener.registered) {
			KeyboardListener.instance.cancelConsumer();
		}
	}
	
	public void stopCalibrating() {
		settingsPanel.setVisible(true);
		titlebarPanel.setVisible(true);
		calibrationPanel.setVisible(false);
		calibrationPanel.cancel();
		sigma.updateValue();
	}
	
	public void updateBounds(GUI gui) {
		super.updateBounds(gui);
		pack();
		setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), gui.size.WINDOW_ROUNDING, gui.size.WINDOW_ROUNDING));
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
		button.addActionListener(p -> close());
		return button;
	}
	
	public void close() {
		setVisible(false);
		stopCalibrating();
		if (KeyboardListener.registered) {
			KeyboardListener.instance.cancelConsumer();
		}
	}
	
	public void setAdvancedOptionsEnabled(boolean b) {
		advPanel.setVisible(b);
		updateBounds(gui);
	}

	public CalibrationPanel getCalibrationPanel() {
		return calibrationPanel;
	}
	
}

class CheckboxPanel extends ThemedPanel {

	private static final long serialVersionUID = -7054967229481740724L;
	
	JLabel descLabel;
	CustomCheckbox checkbox;
	BooleanPreference preference;
	
	public CheckboxPanel(GUI gui, String description, BooleanPreference preference) {
		super(gui);
		this.preference = preference;
		setLayout(new FlowLayout(FlowLayout.LEFT));
		descLabel = new ThemedLabel(gui, "<html>"+ description +"</html>") {
			private static final long serialVersionUID = 2113195400239083116L;
			@Override
			public int getTextSize(SizePreference p) {
				return p.TEXT_SIZE_SMALL;
			}
		};
		checkbox = new CustomCheckbox(preference.get()) {
			private static final long serialVersionUID = 1507233642665292025L;
			@Override
			public void onChanged(boolean ticked) {
				preference.set(ticked);
				preference.onChangedByUser(gui);
			}
		};
		add(checkbox, BorderLayout.LINE_START);
		add(descLabel, BorderLayout.CENTER);
		setOpaque(false);
	}
	
}

class TextboxPanel extends ThemedPanel {

	private static final long serialVersionUID = -7054967229481740724L;
	
	JLabel descLabel;
	DecimalTextField textfield;
	FloatPreference preference;
	
	public TextboxPanel(GUI gui, String description, FloatPreference preference) {
		super(gui);
		this.preference = preference;
		setLayout(new FlowLayout(FlowLayout.LEFT));
		descLabel = new ThemedLabel(gui, "<html>"+ description +"</html>") {
			private static final long serialVersionUID = 2113195400239083116L;
			@Override
			public int getTextSize(SizePreference p) {
				return p.TEXT_SIZE_SMALL;
			}
		};
		textfield = new DecimalTextField(gui, preference.get(), preference.min(), preference.max()) {
			private static final long serialVersionUID = -1357640224921308648L;
			@Override
			public void onChanged(double newSigma) {
				preference.set((float) newSigma);
				preference.onChangedByUser(gui);
			}
		};
		
		Dimension size = textfield.getPreferredSize();
		size.width = 60;
		textfield.setPreferredSize(size);
		add(Box.createHorizontalStrut(0));
		add(descLabel);
		add(textfield);
		setOpaque(false);
	}
	
	public void updateValue() {
		textfield.setValue((double) preference.get());
	}
	
}

class RadioButtonPanel extends ThemedPanel {

	private static final long serialVersionUID = -7054967229481740724L;
	
	JLabel descLabel;
	RadioButtonGroup radioButtomGroup;
	MultipleChoicePreference preference;
	
	public RadioButtonPanel(GUI gui, String description, MultipleChoicePreference preference) {
		super(gui);
		this.preference = preference;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		descLabel = new ThemedLabel(gui, "<html>"+ description +"</html>") {
			private static final long serialVersionUID = 2113195400239083116L;
			@Override
			public int getTextSize(SizePreference p) {
				return p.TEXT_SIZE_SMALL;
			}
		};
		radioButtomGroup = new RadioButtonGroup(gui, preference.getChoices(), preference.get()) {
			private static final long serialVersionUID = -1357640224921308648L;
			@Override
			public void onChanged(String newValue) {
				preference.set(newValue);
				preference.onChangedByUser(gui);
			}
		};
		descLabel.setAlignmentX(0);
		radioButtomGroup.setAlignmentX(0);
		add(descLabel);
		add(radioButtomGroup);
		setOpaque(false);
	}
	
}

class HotkeyPanel extends ThemedPanel {

	private static final long serialVersionUID = -7054967229481740724L;
	
	JLabel descLabel;
	FlatButton button;
	HotkeyPreference preference;
	boolean editing = false;
	
	public HotkeyPanel(GUI gui, String description, HotkeyPreference preference) {
		super(gui);
		this.preference = preference;
		setLayout(new GridLayout(1, 2, 10, 0));
		descLabel = new ThemedLabel(gui, "<html>"+ description +"</html>") {
			private static final long serialVersionUID = -658733822961822860L;
			@Override
			public int getTextSize(SizePreference p) {
				return p.TEXT_SIZE_SMALL;
			}
		};
		descLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		button = new FlatButton(gui, getKeyText()) {
			private static final long serialVersionUID = 1865599754734492942L;
			@Override
			public int getTextSize(SizePreference p) {
				return p.TEXT_SIZE_SMALL;
			}
		};
		button.addActionListener(p -> clicked());
		Dimension size = button.getPreferredSize();
		size.width = OptionsFrame.WINDOW_WIDTH / 4;
		button.setPreferredSize(size);
		add(descLabel);
		add(button);
		setOpaque(false);
	}
	
	private void clicked() {
		if (!editing) {
			editing = true;
			button.setText("...");
			KeyboardListener.instance.setConsumer((code, modifier) -> {
				if (code == -1) {
					// Canceled, dont change anything
				} else if (code == KeyEvent.VK_ESCAPE) {
					preference.setCode(-1);
					preference.setModifier(-1);
				} else {
					preference.setCode(code);
					preference.setModifier(modifier);
				}
				String s = getKeyText();
				SwingUtilities.invokeLater(() -> {
					button.setText(s);
					editing = false;
				});
			});
		}
	}
	
	private String getKeyText() {
		if (preference.getCode() == -1)
			return "Not in use";
		String k = KeyEvent.getKeyText(preference.getCode());
		if (k.startsWith("Unknown")) {
			k = k.substring(17);
		}
		if (preference.getModifier() == 0) {
			return k;
		} else {
			return NativeKeyEvent.getModifiersText(preference.getModifier()) + "+" + k;
		}
	}
	
}

