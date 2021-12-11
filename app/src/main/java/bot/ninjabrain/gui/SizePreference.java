package bot.ninjabrain.gui;

import java.util.HashMap;

public abstract class SizePreference {

	public String name;
	public int TEXT_SIZE_TITLE_LARGE;
	public int TEXT_SIZE_TITLE_SMALL;
	public int TEXT_SIZE_MEDIUM;
	public int TEXT_SIZE_SMALL;
	public int TEXT_SIZE_TINY;
	public int PADDING;
	public int PADDING_THIN;
	public int PADDING_TITLE;
	public int WIDTH;
	public int WINDOW_ROUNDING;

	public static final HashMap<String, SizePreference> SIZES = new HashMap<String, SizePreference>();
	public static final SizePreference REGULAR = new RegularSize();
	public static final SizePreference LARGE = new LargeSize();

	public static SizePreference get(String name) {
		return SIZES.getOrDefault(name, REGULAR);
	}
	
	public SizePreference(String name) {
		this.name = name;
		SIZES.put(name, this);
	}
	
}

class RegularSize extends SizePreference {
	public RegularSize() {
		super("Small");
		TEXT_SIZE_TITLE_LARGE = 15;
		TEXT_SIZE_TITLE_SMALL = 12;
		TEXT_SIZE_MEDIUM = 14;
		TEXT_SIZE_SMALL = 12;
		TEXT_SIZE_TINY = 9;
		PADDING = 6;
		PADDING_THIN = 2;
		PADDING_TITLE = 6;
		WIDTH = 320;
		WINDOW_ROUNDING = 7;
	}
}

class LargeSize extends SizePreference {
	public LargeSize() {
		super("Large");
		TEXT_SIZE_TITLE_LARGE = 15;
		TEXT_SIZE_TITLE_SMALL = 12;
		TEXT_SIZE_MEDIUM = 16;
		TEXT_SIZE_SMALL = 14;
		TEXT_SIZE_TINY = 11;
		PADDING = 7;
		PADDING_THIN = 2;
		PADDING_TITLE = 6;
		WIDTH = 380;
		WINDOW_ROUNDING = 7;
	}
}
