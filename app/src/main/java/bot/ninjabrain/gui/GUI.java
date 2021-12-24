package bot.ninjabrain.gui;

import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import bot.Main;
import bot.ninjabrain.calculator.Throw;
import bot.ninjabrain.calculator.Triangulator;
import bot.ninjabrain.io.VersionURL;
import bot.ninjabrain.util.Profiler;
import bot.ninjabrain.calculator.TriangulationResult;
import bot.ninjabrain.gui.components.CalibrationPanel;
import bot.ninjabrain.gui.components.EnderEyePanel;
import bot.ninjabrain.gui.components.MainButtonPanel;
import bot.ninjabrain.gui.components.MainTextArea;
import bot.ninjabrain.gui.components.NinjabrainBotFrame;
import bot.ninjabrain.gui.components.ThemedComponent;
import bot.sharpie.calculator.BlindTraveler;
import bot.sharpie.calculator.DivineTraveler;
import bot.sharpie.calculator.Hitboxer;

/**
 * Main class for the user interface.
 */
public class GUI {
	
	private MainTextArea mainTextArea;
	private MainButtonPanel mainButtonPanel;
	private EnderEyePanel enderEyePanel;
	
	public NinjabrainBotFrame frame;
	private OptionsFrame optionsFrame;
	private NotificationsFrame notificationsFrame;
	private CalibrationPanel calibrationPanel;
	
	private Font font;
	private Font fontLight;
	public Theme theme;
	public SizePreference size;
	private ArrayList<ThemedComponent> themedComponents;
	
	public Timer autoResetTimer;
	private static int autoResetDelay = 15 * 60 * 1000;
	
	public static final int MAX_THROWS = 10;
	private boolean isBlindTravelMode;
	private long divineTravelTime;
	private boolean isDivineTravelMode;
	private int storedDivine;
	private Triangulator triangulator;
	private Hitboxer hitboxer;
	private BlindTraveler blindTraveler;
	private DivineTraveler divineTraveler;
	private ArrayList<Throw> eyeThrows;
	private ArrayList<Throw> eyeThrowsLast;

	public GUI() {
		theme = Theme.get(Main.preferences.theme.get());
		size = SizePreference.get(Main.preferences.size.get());
		Locale.setDefault(Locale.US);
		themedComponents = new ArrayList<ThemedComponent>();
		triangulator = new Triangulator();
		hitboxer = new Hitboxer();
		hitboxer.setTriangulator(triangulator);
		blindTraveler = new BlindTraveler();
		divineTraveler = new DivineTraveler();
		storedDivine = -1;
		eyeThrows = new ArrayList<Throw>();
		eyeThrowsLast = new ArrayList<Throw>();
		
		Profiler.start("Create frame");
		frame = new NinjabrainBotFrame(this);
		notificationsFrame = frame.getNotificationsFrame();
		
		// Load fonts
		Profiler.stopAndStart("Load fonts");
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, Main.getResourceAsStream("OpenSans-Regular.ttf"));
			fontLight = Font.createFont(Font.TRUETYPE_FONT, Main.getResourceAsStream("OpenSans-Light.ttf"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(font);
			ge.registerFont(fontLight);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Set application icon
		Profiler.stopAndStart("Set app icon");
		URL iconURL = Main.getResource("icon.png");
		ImageIcon img = new ImageIcon(iconURL);
		frame.setIconImage(img.getImage());

		Profiler.stopAndStart("Create gui components");
		// Main text
		Profiler.stopAndStart("Create main text area");
		mainTextArea = new MainTextArea(this);
		frame.add(mainTextArea);
		
		// "Throws" text
		Profiler.stopAndStart("Create main button area");
		mainButtonPanel = new MainButtonPanel(this);
		frame.add(mainButtonPanel);

		// Throw panels
		Profiler.stopAndStart("Create throw panels");
		enderEyePanel = new EnderEyePanel(this);
		frame.add(enderEyePanel);
		
		// Settings window
		Profiler.stopAndStart("Create settings window");
		optionsFrame = new OptionsFrame(this);
		calibrationPanel = optionsFrame.getCalibrationPanel();
		Profiler.stop();
		
		Profiler.stopAndStart("Update fonts and colors");
		updateFontsAndColors();
		Profiler.stopAndStart("Update bounds");
		updateBounds();
		checkIfOffScreen(frame);
		Profiler.stopAndStart("Set visible");
		frame.setVisible(true);
		Profiler.stopAndStart("Set translucency");
		setTranslucent(Main.preferences.translucent.get());
		
		// Auto reset timer
		autoResetTimer = new Timer(autoResetDelay, p -> {
			resetThrows();
			autoResetTimer.stop();
		});
	}
	
	public void setTranslucent(boolean t) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		if (gd.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT))
			frame.setOpacity(t ? 0.75f : 1.0f);
	}
	
	public void setAlwaysOnTop(boolean b) {
		frame.setAlwaysOnTop(b);
		optionsFrame.setAlwaysOnTop(b);
		notificationsFrame.setAlwaysOnTop(b);
	}
	
	public void setNotificationsEnabled(boolean b) {
		frame.getNotificationsButton().setVisible(b && frame.getNotificationsButton().hasURL());
	}
	
	public void updateTheme() {
		theme = Theme.get(Main.preferences.theme.get());
		updateFontsAndColors();
	}
	
	public void updateSizePreference() {
		size = SizePreference.get(Main.preferences.size.get());
		updateFontsAndColors();
		updateBounds();
	}

	public void setNetherCoordsEnabled(boolean b) {
		mainTextArea.netherLabel.setVisible(b);
	}

	public void setAdvancedOptionsEnabled(boolean b) {
		optionsFrame.setAdvancedOptionsEnabled(b);
	}

	public void setAngleErrorsEnabled(boolean b) {
		enderEyePanel.setAngleErrorsEnabled(b);
		updateBounds();
	}
	
	public Font fontSize(float size, boolean light) {
		return light ? fontLight.deriveFont(Font.BOLD, size) : font.deriveFont(Font.BOLD, size);
	}
	
	public void registerThemedComponent(ThemedComponent c) {
		themedComponents.add(c);
	}

	private void updateBounds() {
		for (ThemedComponent tc : themedComponents) {
			tc.updateSize(this);
		}
		updateFontsAndColors();
		frame.updateBounds(this);
		optionsFrame.updateBounds(this);
		notificationsFrame.updateBounds(this);
		frame.setSize(size.WIDTH, frame.getPreferredSize().height);
		frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), size.WINDOW_ROUNDING, size.WINDOW_ROUNDING));
	}
	
	private void updateFontsAndColors() {
		// Color and font
		frame.getContentPane().setBackground(theme.COLOR_NEUTRAL);
		frame.setBackground(theme.COLOR_NEUTRAL);
		optionsFrame.updateFontsAndColors();
		notificationsFrame.updateFontsAndColors();
		for (ThemedComponent tc : themedComponents) {
			tc.updateColors(this);
			tc.updateSize(this);
		}
	}
	
	private FontRenderContext frc = new FontRenderContext(null, true, false);
	public int getTextWidth(String text, Font font) {
		return (int) font.getStringBounds(text, frc).getWidth();
	}
	
	public void toggleOptionsWindow() {
		if (optionsFrame.isVisible()) {
			optionsFrame.close();
		} else {
			optionsFrame.setVisible(true);
			Rectangle bounds = frame.getBounds();
			optionsFrame.setLocation(bounds.x + 40, bounds.y + 30);
		}
	}
	
	public void toggleMinimized() {
		frame.toggleMinimized();
	}

	public void doBlind() {
		isBlindTravelMode = true;
	}

	public void setDivine(boolean value) {
		if (Main.preferences.hotkeyFinishEnteringDivine.getCode() == Main.preferences.hotkeyStartEnteringDivine.getCode()) {
			if (System.currentTimeMillis() - divineTravelTime > 200) {
				// only listen for changes
				if (value == isDivineTravelMode) {
					return;
				}
			} else {
				// ignore changes
				if (value != isDivineTravelMode) {
					return;
				}
			}
			divineTravelTime = System.currentTimeMillis();
		}
		isDivineTravelMode = value;
		System.out.println("divine travel mode is " + isDivineTravelMode);
	}

	public void updateStoredDivine(int i) {
		if (!isDivineTravelMode) {
			return;
		}
		if (storedDivine == 1) {
			storedDivine = 10;
		} else if (storedDivine == -1) {
			storedDivine = 0;
		}
		storedDivine += i;
		System.out.println("stored divine updated to " + storedDivine);
	}

	public void resetThrows() {
		isBlindTravelMode = false;
		storedDivine = -1;
		isDivineTravelMode = false;
		if (eyeThrows.size() > 0) {
			ArrayList<Throw> temp = eyeThrowsLast;
			eyeThrowsLast = eyeThrows;
			eyeThrows = temp;
			eyeThrows.clear();
			onThrowsUpdated();
		}
	}
	
	public void undo() {
		ArrayList<Throw> temp = eyeThrowsLast;
		eyeThrowsLast = eyeThrows;
		eyeThrows = temp;
		onThrowsUpdated();
	}
	
	public void removeThrow(Throw t) {
		if (eyeThrows.contains(t)) {
			saveThrowsForUndo();
			eyeThrows.remove(t);
			onThrowsUpdated();
		}
	}
	
	private void processClipboardUpdate(String clipboard) {
		Throw t = Throw.parseF3C(clipboard);
		System.out.println("clipboard updating stored divine " + storedDivine);
		if (isBlindTravelMode) {
			eyeThrows.add(t);
			TriangulationResult result = blindTraveler.triangulate(eyeThrows);
			mainTextArea.setResult(result);
			eyeThrows.clear();
			isBlindTravelMode = false;
		}
		else if (storedDivine != -1) {
			eyeThrows.add(t);
			TriangulationResult result = divineTraveler.triangulate(eyeThrows, storedDivine);
			mainTextArea.setResult(result);
			eyeThrows.clear();
			storedDivine = -1;
		}
		else if (!calibrationPanel.isCalibrating()) {
			int i = eyeThrows.size();
			if (t != null && i < MAX_THROWS && shouldAddThrow(t)) {
				saveThrowsForUndo();
				eyeThrows.add(t);
				enderEyePanel.setThrow(i, t);
				onThrowsUpdated();
			}
		} else {
			if (t != null && shouldAddThrow(t)) {
				try {
					calibrationPanel.add(t);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void changeLastAngle(double delta) {
		if (!calibrationPanel.isCalibrating()) {
			int i = eyeThrows.size() - 1;
			if (i == -1)
				return;
			Throw last = eyeThrows.get(i);
			Throw t = new Throw(last.x, last.z, last.alpha + delta, last.correction + delta);
			saveThrowsForUndo();
			eyeThrows.remove(last);
			eyeThrows.add(t);
			enderEyePanel.setThrow(i, t);
			onThrowsUpdated();
		} else {
			calibrationPanel.changeLastAngle(delta);
		}
	}
	
	private void setUpdateURL(VersionURL url) {
		frame.setURL(url);
	}
	
	/**
	 * Returns true if the newly inputed throw t should be added.
	 */
	private boolean shouldAddThrow(Throw t) {
		return true;
	}
	
	public void recalculateStronghold() {
		onThrowsUpdated();
	}
	
	private void onThrowsUpdated() {
		TriangulationResult result = null;
		double[] errors = null;
		if (eyeThrows.size() >= 1) {
			result = hitboxer.triangulate(eyeThrows);
			if (result.success) {
				errors = result.getAngleErrors(eyeThrows);
			}
		} 
		mainTextArea.setResult(result);
		enderEyePanel.setErrors(errors);
		// Update throw panels
		enderEyePanel.setThrows(eyeThrows);
		// Update auto reset timer
		if (Main.preferences.autoReset.get()) {
			autoResetTimer.restart();
		}
		// Update bounds
		updateBounds();
		mainTextArea.updateColors(this);
	}

	public void onClipboardUpdated(String newClipboard) {
		SwingUtilities.invokeLater(() -> processClipboardUpdate(newClipboard));
	}
	
	public void onNewUpdateAvailable(VersionURL url) {
		SwingUtilities.invokeLater(() -> setUpdateURL(url));
	}
	
	private void saveThrowsForUndo() {
		eyeThrowsLast.clear();
		for (int i = 0; i < eyeThrows.size(); i++) {
			eyeThrowsLast.add(eyeThrows.get(i));
		}
	}
	
	public Triangulator getTriangulator() {
		return this.triangulator;
	}

	private void checkIfOffScreen(JFrame frame) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice lstGDs[] = ge.getScreenDevices();
		for (GraphicsDevice gd : lstGDs) {
			if (gd.getDefaultConfiguration().getBounds().contains(frame.getBounds()))
				return;
		}
		frame.setLocation(100, 100);
	}

}
