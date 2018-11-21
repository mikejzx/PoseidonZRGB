
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
	
	// Not yet compelte. Just for testing. For some reason windows has differnet keycodes...
	private static final int[][] keyMapKeycodes = {
	//   ESC  NULL F1   F2   F3   F4  NULL  F5   F6   F7   F8  NULL  F9  F10  F11  F12  PRT  SCR  PAU NULL NULL NULL NULL
		{ 1,   0,   59, 60,  61,  62,   0,  63,  64,  65,  66,   0,  67, 68,  87,  88, 3639,  70, 3653,   0,   0,   0,   0 },
		{ 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,  0,   0,   0,    0,   0,    0,   0,   0,   0,   0 },
		//tab NULL  q    w    e    r    t    z    i    u    o    p  NULL  [    ]    \   del  end  pgdn  7nu  8nu  9nu plus
		{15,   0,  16,  17,  18,  19,  20,  21,  23,  22,  24,  25,   0, 26,  27,  43, 3667,3663,    9,   8,   9,  10,3662 },
		
		//{ 27, 0,  112, 113, 114,115,   0, 116, 117, 118, 119,   0, 120, 121, 122, 123,  44, 145,  19,   0,   0,   0,   0 },
	};
	
	// This contains the lerp values foreach key. 0 = start colour, 1 = end colour
	private static float[][] keyColours;
	
	// The keys going back to zero
	private static boolean[][] keysDropping;
	
	// These 32-bit integers represent the hex colour codes.
	// First 3 bytes are RGB colours respectively. Last byte is unused.
	private static int colourStart = 0x2288880;
	private static int colourEnd = 0x00FFFF00;
	
	private static GUIManager gui;
	
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
		
		//if (true) { return; }
		
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
			//device.disableBlocking();
			
			// Main loop
			//int i = 0;
			//final int len = 1000;
			while (kill ^ true) {
				//i++;
				
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
			//device.write(bufferRG);
			device.sendFeatureReport(bufferRG);
			Thread.sleep(1);
			//device.write(bufferB);
			device.sendFeatureReport(bufferB);
		} 
		catch (IOException e) { e.printStackTrace(); }
		catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	// TODO: Change this based on effect-type.
	private int getColourAtKey (int keyx, int keyy) {
		//return Utils.lerp(colourStart, colourEnd, keyColours[keyx][keyy]);
		
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
		for (int x = 0; x < POSEIDON_KEYSX - 1; x++) {
			for (int y = 0; y < keyMapKeycodes.length - 1; y++) {
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
		System.out.println(arg0.getKeyCode());
		
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
