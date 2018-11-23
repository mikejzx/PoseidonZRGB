package io.mikejzx.github.KeyboardRGB;

import java.util.ArrayList;
import java.util.List;

/*
 * Only keys that use this class are keys that have multiple variations,
 * e.g: LSHIFT and RSHIFT.
*/

public class KeyMapKey {
	
	// Points on the keyboard that represent this key.
	public KeyVariant[] variants;
	public int keycode = 0;
	
	public KeyMapKey (int code, KeyVariant[] vars) {
		this.variants = vars;
		this.keycode = code;
	}
}
