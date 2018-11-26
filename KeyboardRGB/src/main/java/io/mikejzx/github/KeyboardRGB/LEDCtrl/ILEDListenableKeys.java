package io.mikejzx.github.KeyboardRGB.LEDCtrl;

import org.jnativehook.keyboard.NativeKeyEvent;

public interface ILEDListenableKeys {
	void keyPress(NativeKeyEvent arg0);
	void keyRelease(NativeKeyEvent arg0);
}
