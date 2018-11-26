package io.mikejzx.github.KeyboardRGB.LEDCtrl;

import org.jnativehook.keyboard.NativeKeyEvent;

import io.mikejzx.github.KeyboardRGB.KeyMapKey;
import io.mikejzx.github.KeyboardRGB.KeyVariant;
import io.mikejzx.github.KeyboardRGB.MainClass;
import io.mikejzx.github.KeyboardRGB.Utils;

public class LEDReactive implements ILEDController, ILEDListenableKeys {

	private int[] colours; 
	
	// This contains the lerp values foreach key. 0 = start colour, 1 = end colour
	private float[][] keyLerps;
	
	// The keys going back to zero
	private boolean[][] keysDropping;
	
	@Override
	public boolean update() {
		int droppingCount = 0;
		for (int y = 0; y < MainClass.keyMapKeycodes.length; y++) {
			for (int x = 0; x < MainClass.keyMapKeycodes[y].length; x++) {
				if (keysDropping[x][y]) {
					keyLerps[x][y] -= 0.1f;
					droppingCount++;
					if (keyLerps[x][y] <= 0.0f) {
						keyLerps[x][y] = 0.0f;
						keysDropping[x][y] = false;
					}
				}
			}
		}
		if (droppingCount == 0) {
			return false;
		}
		return true;
	}

	@Override
	public int getColourAtKey(int keyx, int keyy) {
		// This could probably be optimised quite heavily.
		float lerp = keyLerps[keyx][keyy];
		lerp *= lerp; // TODO: Make this an option <<<
		lerp = Utils.clamp(lerp, 0.0f, 1.0f);
		final int colour1 = colours[0];
		final int colour2 = colours[1];
		byte r0 = (byte)((colour1 >> 24) & 0xFF);
		byte g0 = (byte)((colour1 >> 16) & 0xFF);
		byte b0 = (byte)((colour1 >> 8) & 0xFF);
		byte r1 = (byte)((colour2 >> 24) & 0xFF);
		byte g1 = (byte)((colour2 >> 16) & 0xFF);
		byte b1 = (byte)((colour2 >> 8) & 0xFF);
		byte rl = (byte)Utils.lerp(Byte.toUnsignedInt(r0), Byte.toUnsignedInt(r1), lerp);
		byte gl = (byte)Utils.lerp(Byte.toUnsignedInt(g0), Byte.toUnsignedInt(g1), lerp);
		byte bl = (byte)Utils.lerp(Byte.toUnsignedInt(b0), Byte.toUnsignedInt(b1), lerp);
		
		// The last mask on the end is required because for some reason the blue buffer was getting values
		// greater than 8-bits o_O
		int rn = ((rl & 0xFFFFFFFF) << 24) & 0xFF000000;
		int gn = ((gl & 0xFFFFFFFF) << 16) & 0x00FF0000;
		int bn = ((bl & 0xFFFFFFFF) << 8) & 0x0000FF00;
		//System.out.println("rn: " + Utils.hex(rn) + ", gn: " + Utils.hex(gn) + ", bn: " + Utils.hex(bn));
		return (rn + gn + bn);
	}

	@Override
	public void setColours(int[] newColours) {
		keyLerps = new float[MainClass.POSEIDON_KEYSX][MainClass.POSEIDON_KEYSY];
		keysDropping = new boolean[MainClass.POSEIDON_KEYSX][MainClass.POSEIDON_KEYSY];
		colours = newColours;
	}
	
	// This functions can be optimised alot. Just don't do it in a for-loop. This is temporary...
	private void setKeyLerpValueFromKeymap (int keycode, float newlerp, int loc) {
		boolean variationalKey = false;
		KeyMapKey variational = null;
		for (int i = 0; i < MainClass.keysVariational.length; i++) {
			variational = MainClass.keysVariational[i];
			if (keycode == variational.keycode) {
				variationalKey = true;
				break;
			}
		}
		
		if (!variationalKey) {
			for (int y = 0; y < MainClass.keyMapKeycodes.length; y++) {
				for (int x = 0; x < MainClass.keyMapKeycodes[y].length; x++) {
					if (keycode == MainClass.keyMapKeycodes[y][x]) {
						if (newlerp == 0.0f) {
							keysDropping[x][y] = true;
						}
						else {
							keyLerps[x][y] = newlerp;
						}
						break;
					}
				}
			}
		}
		else {
			// Variational key. Set specific key.
			int x = 0, y = 0;
			KeyVariant[] variants = variational.variants;
			for (int i = 0; i < variants.length; i++) {
				KeyVariant v = variants[i];
				if (v.loc == loc) {
					x = v.x;
					y = v.y;
					//System.out.println("KEY: " + v.brief);
					break;
				}
			}
			
			if (newlerp == 0.0f) {
				keysDropping[x][y] = true;
			}
			else {
				keyLerps[x][y] = newlerp;
			}
		}
	}
	
	public void setAllKeyLerpsZero () {
		if (!MainClass.initialised) { return; }
		
		for (int y = 0; y < MainClass.keyMapKeycodes.length; y++) {
			for (int x = 0; x < MainClass.keyMapKeycodes[y].length; x++) {
				keysDropping[x][y] = false;
				keyLerps[x][y] = 0.0f;
			}
		}
	}

	@Override
	public void keyPress(NativeKeyEvent arg0) {
		int keycode = arg0.getKeyCode();
		setKeyLerpValueFromKeymap(keycode, 1.0f, arg0.getKeyLocation());
		MainClass.refreshLEDs();
	}

	@Override
	public void keyRelease(NativeKeyEvent arg0) {
		int keycode = arg0.getKeyCode();
		
		boolean isCap = keycode == NativeKeyEvent.VC_CAPS_LOCK;
		if (isCap) {
			MainClass.capsOn ^= true; // XOR caps value.
		}
		
		if (MainClass.capsLockStays) {
			if (MainClass.capsOn) {
				setKeyLerpValueFromKeymap(NativeKeyEvent.VC_CAPS_LOCK, 1.0f, 1);
				MainClass.refreshLEDs();
			}
			else {
				setKeyLerpValueFromKeymap(NativeKeyEvent.VC_CAPS_LOCK, 0.0f, 1);
				MainClass.refreshLEDs();
			}
		}
		boolean allowSet = MainClass.capsLockStays ? !isCap : true;
		if (allowSet) {
			setKeyLerpValueFromKeymap(keycode, 0.0f, arg0.getKeyLocation());
			setKeyLerpValueFromKeymap(keycode, 1.0f, arg0.getKeyLocation());
			MainClass.refreshLEDs();
		}
		MainClass.refreshLEDs();
	}
}
