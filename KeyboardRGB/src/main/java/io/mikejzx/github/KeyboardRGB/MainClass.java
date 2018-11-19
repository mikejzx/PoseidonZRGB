
package io.mikejzx.github.KeyboardRGB;

import java.io.IOException;

import com.codeminders.hidapi.ClassPathLibraryLoader;
import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDDeviceInfo;
import com.codeminders.hidapi.HIDManager;

/*
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
				Michael Š.
		Created: 17.11.2018 (~23:18)
					:D
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
	Thanks to CalcProgrammer for his C++
	visualiser project on GitHub, (very useful):
	https://github.com/CalcProgrammer1/
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
*/

public class MainClass 
{
	private static final short VENDOR_ID = 0x264a;
	private static final short PRODUCT_ID = 0x3006;
	private static final int DEVICE_INTERFACE = 1;
	private static final int PACKET_SIZE = 264;
	
	private static final byte POSEIDON_START = 0x07;
	private static final byte POSEIDON_LEDCMD = 0x0E;
	private static final byte POSEIDON_PROFILE = 0x01;
	private static final byte POSEIDON_CHANNEL_REDGRN = 0x01;
	private static final byte POSEIDON_CHANNEL_BLU = 0x02;
	
	private static final int[][] keyMap = {
	//   ESC NULL F1   F2   F3   F4  NULL  F5   F6   F7   F8  NULL  F9  F10  F11  F12  PRT  SCR  PAU NULL NULL NULL NULL
		{ 8,  0,  16,  24,  32,  40,   0,  48,  56,  64,  72,   0,  80,  88,  96, 104, 112, 120, 128,   0,   0,   0,   0 },
		{ 9,  17, 25,  33,  41,  49,  57,  65,  73,  81,  89,   0,  97, 105, 129,   0,  15,  31,  47,  63,  79,  95, 111 },
		{ 10, 0,  18,  26,  34,  42,   0,  50,  58,  66,  74,  82,  90,  98, 106, 114,  23,  39,  55,  71,  87, 103, 102 },
		{ 11, 0,  19,  27,  35,  43,   0,  51,  59,  67,  75,  83,  91,  99, 115,   0,   0,   0,   0,  46, 119,  78,   0 },
		{ 12, 0,  36,  44,  52,  60,   0,  68,   0,  76,  84,  92, 100, 108, 124,   0,   0,  38,   0,  54,  62,  86, 118 },
		{ 13, 21, 29,   0,   0,   0,   0,  45,   0,   0,   0,   0,  85,  93, 109, 117,  14,  22,  30,  70,   0,  94,   0 }
	};
	
	public static void main2(String[] args) throws IOException {
		MainClass k = new MainClass();
		k.Invoke(args);
	}
	
	public void Invoke (String[] args) throws IOException {
		System.out.println("Hello, world ! Invoked...");
		
        ClassPathLibraryLoader.loadNativeHIDLibrary();
		final HIDManager manager = HIDManager.getInstance();
		//HIDDevice device = manager.openById(VENDOR_ID, PRODUCT_ID, null);
		HIDDevice device = null;
		HIDDeviceInfo[] devices = manager.listDevices();
		for (int i = 0; i < devices.length; i++) {
			HIDDeviceInfo d = devices[i];
			if (d.getInterface_number() == DEVICE_INTERFACE &&
				d.getProduct_id() == PRODUCT_ID && d.getVendor_id() == VENDOR_ID) {
				device = d.open();
				if (device == null) {
					continue;
				}
				else { 
					System.out.println("Found device... breaking");
					break; 
				}
			}
		}
		if (device != null) {
			device.disableBlocking();
			
			int i = 0;
			while (i < 1) {
				i++;
				SetLEDs(device);
				try { Thread.sleep(1); } 
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
	
	private void SetLEDs (HIDDevice device) {
		// Initialise packets.
    	byte[] bufferRG = new byte[PACKET_SIZE];
    	byte[] bufferB = new byte[PACKET_SIZE];
    	for (int i = 0; i < PACKET_SIZE; i++) { bufferRG[i] = (byte)0x00; bufferB[i] = (byte)0x00; }
    	bufferRG[0] = POSEIDON_START;
    	bufferRG[1] = POSEIDON_LEDCMD;
    	bufferRG[2] = POSEIDON_PROFILE;
    	bufferRG[3] = POSEIDON_CHANNEL_REDGRN;
    	bufferB[0] = POSEIDON_START;
    	bufferB[1] = POSEIDON_LEDCMD;
    	bufferB[2] = POSEIDON_PROFILE;
    	bufferB[3] = POSEIDON_CHANNEL_BLU;
    	for (int x = 0; x < 23; x++) {
    		for (int y = 0; y < 6; y++) {
    			int index = keyMap[y][x];
    			if (index != 0) {
    				bufferRG[index] = (byte)0xFF;
    				bufferRG[index + 128] = (byte)0x88;
    				bufferB[index] = (byte)0xFF;
    			}
    		}
    	}
    	
    	// Send packets
		try { 
			byte[] buffer = new byte[PACKET_SIZE];
			for (int i = 0; i < buffer.length; i++) { buffer[i] = (byte)0x00; }
			
			//device.write(bufferRG);
			device.sendFeatureReport(buffer);
			Thread.sleep(1);
			//device.write(bufferB);
			device.sendFeatureReport(buffer);
		} 
		catch (IOException e) { e.printStackTrace(); }
		catch (InterruptedException e) { e.printStackTrace(); }
	}
}
