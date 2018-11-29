package io.mikejzx.github.KeyboardRGB;

import java.util.prefs.Preferences;

public class PrefsManager {
	
	public static int prefStartMinimised = 0;
	public static int prefCapsSustain = 0;
	
	public static final String PREFSTR_STARTMIN = "startMinimised";
	public static final String PREFSTR_CAPSUSTAIN = "capsSustain";
	
	// TODO: SAVE LED MODE PREF HERE
	
	private static Preferences prefs;
	
	public static void initialise () {
		prefs = Preferences.userNodeForPackage(MainClass.class);
		
		// First check if all values exist. If not, set them.
		int got;
		got = prefs.getInt(PREFSTR_STARTMIN, 0);
		if (got == 0) { prefs.putInt(PREFSTR_STARTMIN, 0); }
		prefStartMinimised = got;
		
		got = prefs.getInt(PREFSTR_CAPSUSTAIN, 0);
		if (got == 0) { prefs.putInt(PREFSTR_CAPSUSTAIN, 0); }
		prefCapsSustain = got;
	}
	
	// TODO: Make a common function when more prefs are added.
	public static void setPref_startMinimised (int newVal) {
		prefStartMinimised = newVal;
		prefs.putInt(PREFSTR_STARTMIN, newVal);
		System.out.println("startMinimised set to " + newVal);
	}
	
	public static void setPref_capsSustain (int newVal) {
		prefCapsSustain = newVal;
		prefs.putInt(PREFSTR_CAPSUSTAIN, newVal);
		System.out.println("capsSustain set to " + newVal);
	}
}
