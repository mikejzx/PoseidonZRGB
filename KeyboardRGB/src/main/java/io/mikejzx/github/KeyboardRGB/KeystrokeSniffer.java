package io.mikejzx.github.KeyboardRGB;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/*
	Used to capture keystrokes to change LED's.
	'Sniffer' is probably the wrong name for this xD
 */

public class KeystrokeSniffer implements NativeKeyListener {

	private boolean initialised = false;
	private MainClass mainClass = null;
	
	public void Initialise (MainClass m) {
		if (initialised) { return; }
		initialised = true;
		
		// Disable logging spam
		LogManager.getLogManager().reset();
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		
		try { GlobalScreen.registerNativeHook(); } 
		catch (NativeHookException e) { e.printStackTrace(); }
		
		// Bind listener
		mainClass = m;
		GlobalScreen.addNativeKeyListener(mainClass);
	}
	
	public void Deinitialise () {
		if (!initialised) { return; }
		initialised = false;
		try { GlobalScreen.unregisterNativeHook(); } 
		catch (NativeHookException e) { e.printStackTrace(); }
		
		// Bind listener
		if (mainClass != null) {
			GlobalScreen.removeNativeKeyListener(mainClass);
		}
	}
	
	@Override
	public void nativeKeyPressed(NativeKeyEvent arg0) { }
	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) { }
	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) { }
}
