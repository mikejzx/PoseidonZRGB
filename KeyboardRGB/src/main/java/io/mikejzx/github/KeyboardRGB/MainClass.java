
package io.mikejzx.github.KeyboardRGB;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.IOException;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesListener;
import org.hid4java.HidServicesSpecification;
import org.hid4java.ScanMode;
import org.hid4java.event.HidServicesEvent;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/*
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
				Michael Š.
		Created: 17.11.2018 (~23:18)
					:D
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
	Thanks to CalcProgrammer for his C++
	visualiser project on GitHub, (very useful):
	https://github.com/CalcProgrammer1/
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
	NOTE TO THOSE WHO USE THIS CODE:
	If you edit what goes in the packet that
	is sent to the keyboard, be very careful,
	as you could actually screw up your keyboard,
	even if the buffer is literally just zeros.
	
	If you do something strange to your
	keyboard, just unplug it, and plug it in
	again. It should refresh everything.
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
	FX TODO LIST:
	* Reactive w/ Backlight
	* 'Rain' (Vertical wave)
	* Random keys that change to random values slowly.
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
*/

public class MainClass implements NativeKeyListener, HidServicesListener
{
	public static String SOFTWARE_VERSION = "0.0.2_01-SNAPSHOT(alpha)";
	
	public static boolean kill = false;
	public static void kill() { kill = true; }
	public static KeystrokeSniffer sniffer;
	
	private static final short VENDOR_ID = 0x264a;
	private static final short PRODUCT_ID = 0x3006;
	private static final int PACKET_SIZE = 264;
	
	private static final byte POSEIDON_START = 0x07;
	private static final byte POSEIDON_LEDCMD = 0x0E;
	private static final byte POSEIDON_PROFILE = 0x01;
	private static final byte POSEIDON_CHANNEL_REDGRN = 0x01;
	private static final byte POSEIDON_CHANNEL_BLU = 0x02;
	private static final short POSEIDON_KEYSX = 23;
	private static final short POSEIDON_KEYSY = 6;
	
	private static final int[][] keyMap = {
	//   ESC NULL F1   F2   F3   F4  NULL  F5   F6   F7   F8  NULL  F9  F10  F11  F12  PRT  SCR  PAU NULL NULL NULL NULL
		{ 8,  0,  16,  24,  32,  40,   0,  48,  56,  64,  72,   0,  80,  88,  96, 104, 112, 120, 128,   0,   0,   0,   0 },
		{ 9,  17, 25,  33,  41,  49,  57,  65,  73,  81,  89,   0,  97, 105, 129,   0,  15,  31,  47,  63,  79,  95, 111 },
		{ 10, 0,  18,  26,  34,  42,   0,  50,  58,  66,  74,  82,  90,  98, 106, 114,  23,  39,  55,  71,  87, 103, 102 },
		{ 11, 0,  19,  27,  35,  43,   0,  51,  59,  67,  75,  83,  91,  99, 115,   0,   0,   0,   0,  46, 119,  78,   0 },
		{ 12, 0,  36,  44,  52,  60,   0,  68,   0,  76,  84,  92, 100, 108, 124,   0,   0,  38,   0,  54,  62,  86, 118 },
		{ 13, 21, 29,   0,   0,   0,   0,  45,   0,   0,   0,   0,  85,  93, 109, 117,  14,  22,  30,  70,   0,  94,   0 }
	};
	
	// Not yet compelte. Just for testing. For some reason windows has differnet keycodes... Can also be a bit cleaner...
	/*private static final int[][] keyMapKeycodes = {
	//   ESC  NULL F1   F2   F3   F4  NULL  F5   F6   F7   F8  NULL  F9  F10  F11  F12  PRT  SCR  PAU NULL NULL NULL NULL
		{  1,   0,   59, 60,  61,  62,   0,  63,  64,  65,  66,   0,  67, 68,  87,  88, 3639,  70, 3653,   0,   0,   0,   0 },
		//tilde 1    2    3    4    5    6    7    8   9     0 null   -    +  back      ins  home  pgup num  div   mul  -
		{ 41,   2,   3,   4,   5,   6,   7,   8,   9,  10,   11, 0,  12,  13,   14, 0, 3666,3655, 3657, 69,   53, 3639, 3658 },
		//tab NULL  q    w    e    r NULL, t    z    U    I    o    p   [    ]    \   del  end  pgdn  7nu  8nu  9nu plus
		{ 15,   0,  16,  17,  18,  19,  0, 20,  21,  22,  23,  24,  25, 26,  27,  43, 3667,3663,3665, 3655, 57416, 3657 },
		
		//{ 27, 0,  112, 113, 114,115,   0, 116, 117, 118, 119,   0, 120, 121, 122, 123,  44, 145,  19,   0,   0,   0,   0 },
	};*/
	private static int[][] keyMapKeycodes;
	
	// This contains the lerp values foreach key. 0 = start colour, 1 = end colour
	private static float[][] keyColours;
	
	// The keys going back to zero
	private static boolean[][] keysDropping;
	
	// These 32-bit integers represent the hex colour codes.
	// First 3 bytes are RGB colours respectively. Last byte is unused.
	public static int colour1 = 0xFF220000; //0x4411110;
	public static int colour2 = 0xFFFF0000; //0xFF002200;
	
	private static GUIManager gui;
	
	public static enum LEDMode {
		Backlit, ReactiveBacklit, Rain, Random
	};
	
	private static LEDMode ledMode = LEDMode.ReactiveBacklit;
	private static boolean update = false;
	
	// For developing the program (GUI, tweaks, etc...) without the keyboard connected.
	// TURN THIS OFF IN FINAL BUILDS.
	private boolean RUN_WITHOUT_DEVICE = false;
	public static boolean capsLockStays = false;
	private static HidServices services;
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		MainClass k = new MainClass();
		k.invoke(args);
	}
	
	public void invoke (String[] args) throws IOException, InterruptedException {
		System.out.println("Hello, world ! Invoked...");
		
		if (RUN_WITHOUT_DEVICE) {
			System.err.println("WARNING: RUN_WITHOUT_DEVICE IS TRUE! THIS SHOULD BE FALSE IF THE LEDS ARE TO BE SET !");
		}
		
		// Key sniffer
		sniffer = new KeystrokeSniffer();
		sniffer.Initialise(this);
		
		// Initialise GUI.
		gui = new GUIManager();
		gui.initialise();
		
		keyColours = new float[POSEIDON_KEYSX][POSEIDON_KEYSY];
		keysDropping = new boolean[POSEIDON_KEYSX][POSEIDON_KEYSY];
		
		final int VC_PIPE = 43, VC_SUPER = 3675, VC_ADD = 3662, VC_NUMLOCK = 69, VC_DIV = 53, VC_MUL = 3639, VC_MINUS = 3658, VC_QMARK = 53, VC_RSHIFT = 3638, VC_FULLSTOP = 83;
		keyMapKeycodes = new int[][] {
			{ NativeKeyEvent.VC_ESCAPE, 0, NativeKeyEvent.VC_F1, NativeKeyEvent.VC_F2, NativeKeyEvent.VC_F3,  NativeKeyEvent.VC_F4, 0, NativeKeyEvent.VC_F5,  NativeKeyEvent.VC_F6, NativeKeyEvent.VC_F7, NativeKeyEvent.VC_F8, 0, NativeKeyEvent.VC_F9, NativeKeyEvent.VC_F10, NativeKeyEvent.VC_F11, NativeKeyEvent.VC_F12, NativeKeyEvent.VC_PRINTSCREEN, NativeKeyEvent.VC_SCROLL_LOCK, NativeKeyEvent.VC_PAUSE },
			{ 41, NativeKeyEvent.VC_1,  NativeKeyEvent.VC_2, NativeKeyEvent.VC_3,  NativeKeyEvent.VC_4, NativeKeyEvent.VC_5, NativeKeyEvent.VC_6, NativeKeyEvent.VC_7, NativeKeyEvent.VC_8, NativeKeyEvent.VC_9, NativeKeyEvent.VC_0, 0, 12, 13, NativeKeyEvent.VC_BACKSPACE, 0,  NativeKeyEvent.VC_INSERT,  NativeKeyEvent.VC_HOME,  NativeKeyEvent.VC_PAGE_UP,  VC_NUMLOCK, VC_DIV, VC_MUL, VC_MINUS },
			{ NativeKeyEvent.VC_TAB, 0, NativeKeyEvent.VC_Q,NativeKeyEvent.VC_W, NativeKeyEvent.VC_E, NativeKeyEvent.VC_R, NativeKeyEvent.VC_R, NativeKeyEvent.VC_T, NativeKeyEvent.VC_Y, NativeKeyEvent.VC_U, NativeKeyEvent.VC_I, NativeKeyEvent.VC_O, NativeKeyEvent.VC_P, NativeKeyEvent.VC_OPEN_BRACKET, NativeKeyEvent.VC_CLOSE_BRACKET, VC_PIPE, NativeKeyEvent.VC_DELETE, NativeKeyEvent.VC_END, NativeKeyEvent.VC_PAGE_DOWN, NativeKeyEvent.VC_7, NativeKeyEvent.VC_8, NativeKeyEvent.VC_9, VC_ADD },
			{ NativeKeyEvent.VC_CAPS_LOCK, 0, NativeKeyEvent.VC_A, NativeKeyEvent.VC_S, NativeKeyEvent.VC_D, NativeKeyEvent.VC_F, 0, NativeKeyEvent.VC_G, NativeKeyEvent.VC_H, NativeKeyEvent.VC_J, NativeKeyEvent.VC_K, NativeKeyEvent.VC_L, NativeKeyEvent.VC_SEMICOLON, NativeKeyEvent.VC_QUOTE, NativeKeyEvent.VC_ENTER, 0, 0, 0, 0, NativeKeyEvent.VC_4, NativeKeyEvent.VC_5, NativeKeyEvent.VC_6 },
			{ NativeKeyEvent.VC_SHIFT, 0, NativeKeyEvent.VC_Z, NativeKeyEvent.VC_X, NativeKeyEvent.VC_C, NativeKeyEvent.VC_V, 0, NativeKeyEvent.VC_B, 0, NativeKeyEvent.VC_N, NativeKeyEvent.VC_M, NativeKeyEvent.VC_COMMA, NativeKeyEvent.VC_PERIOD, VC_QMARK, VC_RSHIFT, 0, 0, NativeKeyEvent.VC_UP, 0, NativeKeyEvent.VC_1, NativeKeyEvent.VC_2, NativeKeyEvent.VC_3 },
			{ NativeKeyEvent.VC_CONTROL, VC_SUPER, NativeKeyEvent.VC_ALT, 0, 0, 0, 0, NativeKeyEvent.VC_SPACE, 0, 0, 0, 0, NativeKeyEvent.VC_ALT, 0, NativeKeyEvent.VC_CONTEXT_MENU, NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_LEFT, NativeKeyEvent.VC_DOWN, NativeKeyEvent.VC_RIGHT, NativeKeyEvent.VC_0, 0, VC_FULLSTOP, NativeKeyEvent.VC_ENTER },
		};
		
		// May move argument handling into a seperate function
		if (args.length > 0) { 
			// Pass in arguments for colours.
			if (args.length == 2) {
				colour1 = Integer.parseInt(args[0]);
				colour2 = Integer.parseInt(args[1]);
			}
		}
		
		HidServicesSpecification hidServiceSpecs = new HidServicesSpecification();
    	hidServiceSpecs.setAutoShutdown(true);
    	hidServiceSpecs.setScanInterval(500);
    	hidServiceSpecs.setPauseInterval(500);
    	hidServiceSpecs.setScanMode(ScanMode.SCAN_AT_FIXED_INTERVAL_WITH_PAUSE_AFTER_WRITE);
    	services = HidManager.getHidServices(hidServiceSpecs);
    	services.addHidServicesListener(this);
    	System.out.println("HID services Now Starting...");
    	services.start();
    	HidDevice device = null;
    	
		if (!RUN_WITHOUT_DEVICE) {
			// Iterate througheach device, and find the keyboard.
			device = getDevice();
		}
		
		if (device != null || RUN_WITHOUT_DEVICE) {
			// Main loop
			mainLoop(device);
			device.close();
		}
		else {
			System.err.println("DEVICE IS NULL");
		}
		services.shutdown();
		System.out.println("Applcation termination...");
	}
	
	private HidDevice getDevice () throws IOException {
		HidDevice hidDevice = null;
    	for (HidDevice device : services.getAttachedHidDevices()) {
    		System.out.println(device);
    		if (device.getVendorId() == VENDOR_ID && device.getProductId() == PRODUCT_ID) {
    			boolean open = device.open();
    			System.err.println(open);
    			if (open) { 
    				hidDevice = device; 
    			}
    		}
    	}
    	return hidDevice;
	}
	
	private void mainLoop (HidDevice device) throws InterruptedException {
		while (kill ^ true) {
			switch (ledMode) {
				case Backlit: {
					setLEDs(device);
					update = false;
					while (!update) { Thread.sleep(1); }
				} break;
			
				case ReactiveBacklit: {
					// Actually set the LED's
					setLEDs(device);
					
					int droppingCount = 0;
					for (int x = 0; x < POSEIDON_KEYSX; x++) {
						for (int y = 0; y < keyMapKeycodes.length; y++) {
							if (keysDropping[x][y]) {
								keyColours[x][y] -= 0.1f;
								droppingCount++;
								if (keyColours[x][y] <= 0.0f) {
									keyColours[x][y] = 0.0f;
									keysDropping[x][y] = false;
								}
							}
						}
					}
					if (droppingCount == 0) {
						update = false;
					}
					
					// Sleep for 100 ms
					Thread.sleep(100);
					
					// Wait for next update.
					// This is very important, it makes the flicker less noticeable.
					// So it is only visible when keys are actually changinc colour.
					while (!update) { Thread.sleep(1); }
				} break;
				
				default: {
					setLEDs(device);
					update = false;
					while (!update) { Thread.sleep(1); }
				} break;
			}
		}
	}
	
	private void setLEDs (HidDevice device) {
		if (RUN_WITHOUT_DEVICE || device == null) {
			return;
		}
		// Initialise packets.
    	byte[] bufferRG = new byte[PACKET_SIZE];
    	byte[] bufferB = new byte[PACKET_SIZE];
    	for (int i = 0; i < PACKET_SIZE; i++) { bufferRG[i] = (byte)0x00; bufferB[i] = (byte)0x00; }
    	bufferRG[0] = POSEIDON_LEDCMD; bufferRG[1] = POSEIDON_PROFILE; bufferRG[2] = POSEIDON_CHANNEL_REDGRN;
    	bufferB[0] = POSEIDON_LEDCMD; bufferB[1] = POSEIDON_PROFILE; bufferB[2] = POSEIDON_CHANNEL_BLU;
    	// Assign colour bytes.
    	for (int x = 0; x < POSEIDON_KEYSX; x++) {
    		for (int y = 0; y < POSEIDON_KEYSY; y++) {
    			int index = keyMap[y][x] - 1;
    			if (index > 0) {
    				int colour = getColourAtKey(x, y);
    				bufferRG[index] = (byte)((colour >> 24) & 0xFF);
    				bufferRG[index + 128] = (byte)((colour >> 16) & 0xFF);
    				bufferB[index] = (byte)((colour >> 8) & 0xFF);
    			}
    		}
    	}
    	
    	// Send packets
		try {
			// Send RED-GRN buffer
			int valRG = device.sendFeatureReport(bufferRG, (byte)POSEIDON_START);
			if (valRG < 0) { System.err.println("ERROR[RG]: " + device.getLastErrorMessage()); }
			
			Thread.sleep(10); // Was 1, set to 10 to prevent weird colour thing,
			
			// Send BLU buffer
			int valB = device.sendFeatureReport(bufferB, (byte)POSEIDON_START);
	    	if (valB < 0) { System.err.println("ERROR[RG]: " + device.getLastErrorMessage()); }
		}
		catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	public static void setLEDMode (LEDMode newMode) {
		ledMode = newMode;
		setAllKeyLerpsZero();
		update = true;
	}
	
	public static void refreshLEDs () { update = true; }
	
	private int getColourAtKey (int keyx, int keyy) {
		switch (ledMode) {
			case Backlit: {
				return colour1;
			}
			case ReactiveBacklit: {	
				// This could probably be optimised quite heavily.
				float lerp = keyColours[keyx][keyy];
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
			
			default: {
				return colour1;
			}
		}
	}

	// This functions can be optimised alot. Just don't do it in a for-loop. This is temporary...
	private void setKeyLerpValueFromKeymap (int keycode, float newlerp) {
		for (int y = 0; y < keyMapKeycodes.length; y++) {
			for (int x = 0; x < keyMapKeycodes[y].length; x++) {
				if (keycode == keyMapKeycodes[y][x]) {
					if (newlerp == 0.0f) {
						keysDropping[x][y] = true;
					}
					else {
						keyColours[x][y] = newlerp;
					}
					break;
				}
			}
		}
	}
	
	public static void setAllKeyLerpsZero () {
		if (keyMapKeycodes == null || keyMapKeycodes.length == 0) { return; }
		
		for (int y = 0; y < keyMapKeycodes.length; y++) {
			for (int x = 0; x < keyMapKeycodes[y].length; x++) {
				keysDropping[x][y] = false;
				keyColours[x][y] = 0.0f;
			}
		}
	}
	
	@Override
	public void nativeKeyPressed(NativeKeyEvent arg0) {
		//System.out.println(arg0.getRawCode());
		//System.out.println(arg0.getKeyCode());
		
		int keycode = arg0.getKeyCode();
		if (ledMode == LEDMode.ReactiveBacklit) {
			setKeyLerpValueFromKeymap(keycode, 1.0f);
			update = true;
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) { 
		int keycode = arg0.getKeyCode();
		
		if (capsLockStays) {
			boolean capsOn = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
			if (capsOn) {
				setKeyLerpValueFromKeymap(NativeKeyEvent.VC_CAPS_LOCK, 1.0f);
				update = true;
				//System.out.println("caps lock on");
			}
			else {
				setKeyLerpValueFromKeymap(NativeKeyEvent.VC_CAPS_LOCK, 0.0f);
				update = true;
				//System.out.println("caps lock off");
			}
		}
		boolean allowSet = capsLockStays ? (keycode != NativeKeyEvent.VC_CAPS_LOCK) : true;
		if (allowSet) {
			setKeyLerpValueFromKeymap(keycode, 0.0f);
			if (ledMode == LEDMode.ReactiveBacklit) {
				setKeyLerpValueFromKeymap(keycode, 1.0f);
				update = true;
			}
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) { }
	
	 public void hidDeviceDetached(HidServicesEvent event) {
    	System.err.println("Device detached: " + event);
    }

    public void hidFailure(HidServicesEvent event) {
    	System.err.println("HID failure: " + event);
    }
    
    public void hidDeviceAttached(HidServicesEvent event) {
    	System.err.println("Device attached: " + event);
    }
}
