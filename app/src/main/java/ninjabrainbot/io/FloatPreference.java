package ninjabrainbot.io;

import java.util.prefs.Preferences;

import ninjabrainbot.gui.GUI;

public class FloatPreference {

	Preferences pref;

	String key;
	float value, max, min;

	public FloatPreference(String key, float defaultValue, float minValue, float maxValue, Preferences pref) {
		this.pref = pref;
		this.key = key;
		value = pref.getFloat(key, defaultValue);
		this.min = minValue;
		this.max = maxValue;
		if (value > max)
			value = max;
		if (value < min)
			value = min;
	}

	public float get() {
		return value;
	}

	public float max() {
		return max;
	}

	public float min() {
		return min;
	}

	public void set(float value) {
		if (value > max)
			value = max;
		if (value < min)
			value = min;
		this.value = value;
		pref.putFloat(key, value);
	}

	public void onChangedByUser(GUI gui) { }

}