
package io.mikejzx.github.KeyboardRGB;

import java.io.IOException;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.codeminders.hidapi.ClassPathLibraryLoader;
import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDDeviceInfo;
import com.codeminders.hidapi.HIDManager;

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
	even if the packet is literally just zeros.
	
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

public class MainClass implements NativeKeyListener
{
	public static boolean kill = false;
	public static void kill() { kill = true; }
	public static KeystrokeSniffer sniffer;
	
	private static final short VENDOR_ID = 0x264a;
	private static final short PRODUCT_ID = 0x3006;
	private static final int DEVICE_INTERFACE = 1;
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
	private static int colourStart = 0xFF220000; //0x4411110;
	private static int colourEnd = 0xFFFF0000; //0xFF002200;
	
	private static GUIManager gui;
	/*MICHAEL SEKC IS AWESOMEMMM*/
	public static void main(String[] args) throws IOException, InterruptedException {
		MainClass k = new MainClass();
		k.invoke(args);
	}
	
	public void invoke (String[] args) throws IOException, InterruptedException {
		System.out.println("Hello, world ! Invoked...");
		
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
				colourStart = Integer.parseInt(args[0]);
				colourEnd = Integer.parseInt(args[1]);
			}
		}
		
		// Load HID library.
        ClassPathLibraryLoader.loadNativeHIDLibrary();
		final HIDManager manager = HIDManager.getInstance();
		HIDDevice device = null;
		HIDDeviceInfo[] devices = manager.listDevices();
		
		// Iterate througheach device, and find the keyboard.
		for (int i = 0; i < devices.length; i++) {
			HIDDeviceInfo d = devices[i];
			if (d.getInterface_number() == DEVICE_INTERFACE &&
				d.getProduct_id() == PRODUCT_ID && d.getVendor_id() == VENDOR_ID) {
				device = d.open();
				if (device == null) { continue; }
				else { 
					System.out.println("Found device... breaking");
					break; 
				}
			}
		}
		if (device != null) {
			// This makes the device recieve packet immediately.
			device.disableBlocking();
			
			// Main loop
			while (kill ^ true) {
				// Actually set the LED's
				setLEDs(device);
				
				for (int x = 0; x < POSEIDON_KEYSX; x++) {
					for (int y = 0; y < keyMapKeycodes.length; y++) {
						if (keysDropping[x][y]) {
							keyColours[x][y] -= 0.1f;
							if (keyColours[x][y] <= 0.0f) {
								keyColours[x][y] = 0.0f;
								keysDropping[x][y] = false;
							}
						}
					}
				}
				// Sleep for 100 ms
				try { Thread.sleep(100); } 
				catch (InterruptedException e) { e.printStackTrace(); }
			}
			
			device.close();
		}
		else {
			System.err.println("DEVICE IS NULL");
		}
		manager.release();
		System.out.println("Applcation termination...");
	}
	
	private void setLEDs (HIDDevice device) {
		// Initialise packets.
    	byte[] bufferRG = new byte[PACKET_SIZE];
    	byte[] bufferB = new byte[PACKET_SIZE];
    	for (int i = 0; i < PACKET_SIZE; i++) { bufferRG[i] = (byte)0x00; bufferB[i] = (byte)0x00; }
    	bufferRG[0] = POSEIDON_START; bufferRG[1] = POSEIDON_LEDCMD; bufferRG[2] = POSEIDON_PROFILE; bufferRG[3] = POSEIDON_CHANNEL_REDGRN;
    	bufferB[0] = POSEIDON_START; bufferB[1] = POSEIDON_LEDCMD; bufferB[2] = POSEIDON_PROFILE; bufferB[3] = POSEIDON_CHANNEL_BLU;
    	// Assign colour bytes.
    	for (int x = 0; x < POSEIDON_KEYSX; x++) {
    		for (int y = 0; y < POSEIDON_KEYSY; y++) {
    			int index = keyMap[y][x];
    			if (index != 0) {
    				int colour = getColourAtKey(x, y);
    				bufferRG[index] = (byte)((colour >> 24) & 0xFF);
    				bufferRG[index + 128] = (byte)((colour >> 16) & 0xFF);
    				bufferB[index] = (byte)((colour >> 8) & 0xFF);
    			}
    		}
    	}
    	
    	// Send packets
		try { 
			device.sendFeatureReport(bufferRG);
			Thread.sleep(10); // Was 1, set to 10 to prevent weird colour thing,
			device.sendFeatureReport(bufferB);
		} 
		catch (IOException e) { e.printStackTrace(); }
		catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	// TODO: Change this based on effect-type.
	private int getColourAtKey (int keyx, int keyy) {
		// This could probably be optimised quite heavily.
		float lerp = keyColours[keyx][keyy];
		byte r0 = (byte)((colourStart >> 24) & 0xFF);
		byte g0 = (byte)((colourStart >> 16) & 0xFF);
		byte b0 = (byte)((colourStart >> 8) & 0xFF);
		byte r1 = (byte)((colourEnd >> 24) & 0xFF);
		byte g1 = (byte)((colourEnd >> 16) & 0xFF);
		byte b1 = (byte)((colourEnd >> 8) & 0xFF);
		byte rl = (byte)Utils.lerp(Byte.toUnsignedInt(r0), Byte.toUnsignedInt(r1), lerp);
		byte gl = (byte)Utils.lerp(Byte.toUnsignedInt(g0), Byte.toUnsignedInt(g1), lerp);
		byte bl = (byte)Utils.lerp(Byte.toUnsignedInt(b0), Byte.toUnsignedInt(b1), lerp);
		int rn = (rl & 0xFFFFFF) << 24;
		int gn = (gl & 0xFFFFFF) << 16;
		int bn = (bl & 0xFFFFFF) << 8;
		return (rn + gn + bn);
		//return (int)Math.round(r0 * lerp);
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
	
	@Override
	public void nativeKeyPressed(NativeKeyEvent arg0) {
		//System.out.println(arg0.getRawCode());
		//System.out.println(arg0.getKeyCode());
		
		int keycode = arg0.getKeyCode();
		setKeyLerpValueFromKeymap(keycode, 1.0f);
	}

	// May do a slow-lerp to 0
	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) { 
		int keycode = arg0.getKeyCode();
		setKeyLerpValueFromKeymap(keycode, 0.0f);
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) { }
}
