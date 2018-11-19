package io.mikejzx.github.KeyboardRGB;

import java.io.IOException;

import com.codeminders.hidapi.ClassPathLibraryLoader;
import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDDeviceInfo;
import com.codeminders.hidapi.HIDManager;

public class Test {

	private static final short VENDOR_ID = 0x264a;
	private static final short PRODUCT_ID = 0x3006;
	private static final int DEVICE_INTERFACE = 1;
	private static final int PACKET_SIZE = 264;
	
	public static void maintest(String[] args) throws IOException { Test t = new Test(); t.Invoke(); }
	
	public void Invoke () throws IOException {
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
		
		// Device is found prior to this...
		
		if (device != null) {
			device.disableBlocking();
			
			// Initialise the buffer, and send it. PACKET_SIZE is 264
			byte[] buffer = new byte[PACKET_SIZE];
			for (int i = 0; i < PACKET_SIZE; i++) { buffer[i] = (byte)0x00; }
			
			// These bytes are required for it to actually change the LED's.
			buffer[0] = 0x07;
	    	buffer[1] = 0x0E;
	    	buffer[2] = 0x01;
	    	buffer[3] = 0x01;
			
			try {
				device.sendFeatureReport(buffer);
			}
			catch (IOException e) { e.printStackTrace(); }
			
			device.close();
		}
		else {
			System.err.println("DEVICE IS NULL");
		}
		manager.release();
	}
}
