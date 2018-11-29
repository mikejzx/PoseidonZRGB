
package io.mikejzx.github.KeyboardRGB;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesListener;
import org.hid4java.HidServicesSpecification;
import org.hid4java.ScanMode;
import org.hid4java.event.HidServicesEvent;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import io.mikejzx.github.KeyboardRGB.LEDCtrl.ILEDController;
import io.mikejzx.github.KeyboardRGB.LEDCtrl.ILEDListenableKeys;
import io.mikejzx.github.KeyboardRGB.LEDCtrl.LEDBacklit;
import io.mikejzx.github.KeyboardRGB.LEDCtrl.LEDReactive;
import io.mikejzx.github.KeyboardRGB.LEDCtrl.LEDTravel;
import io.mikejzx.github.KeyboardRGB.LEDCtrl.LEDWaveH;
import io.mikejzx.github.KeyboardRGB.LEDCtrl.LEDWaveV;

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
	Use this for icon of .exe wrapper:
	E:\Programs\Tt eSPORTS POSEIDON Z RGB\POSEIDON Z RGB.exe
	(Will extract it eventually)
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

	// TODO: Path follow LED when app starts. With whit wave after it
	// TODO: Create an in-app emulator of the actualy keyboard.
*/

public class MainClass implements NativeKeyListener, HidServicesListener
{
	public static String SOFTWARE_VERSION = "0.0.2_05-SNAPSHOT(alpha)";
	
	public static boolean kill = false;
	public static void kill() { kill = true; }
	public static KeystrokeSniffer sniffer;
	
	public static final short VENDOR_ID = 0x264a;
	public static final short PRODUCT_ID = 0x3006;
	public static final short PACKET_SIZE = 264;
	public static final short POSEIDON_KEYSX = 23;
	public static final short POSEIDON_KEYSY = 6;
	
	private static final byte POSEIDON_START = 0x07;
	private static final byte POSEIDON_LEDCMD = 0x0E;
	private static final byte POSEIDON_PROFILE = 0x01;
	private static final byte POSEIDON_CHANNEL_REDGRN = 0x01;
	private static final byte POSEIDON_CHANNEL_BLU = 0x02;

	// These 32-bit integers represent the hex colour codes.
	// First 3 bytes are RGB colours respectively. Last byte is empty & unused.
	public static int colour1 = 0x88118800; //0xFF220000; //0x4411110;
	public static int colour2 = 0x00FF8800; //0xFFFF0000; //0xFF002200;
	
	public static boolean initialised = false;
	public static boolean capsLockStays = false;
	public static boolean capsOn = false;
	private static HidServices services;
	private static GUIManager gui;
	private static LEDMode ledMode = LEDMode.ReactiveBacklit;
	private static boolean update = false;
	private static MenuItem itemMin, itemShow;
	private static HidDevice hidDevice;
	private static ILEDController ledController;
	private static TrayIcon trayIcon;
	private static Updater swUpdater;
	
	private static LEDBacklit ledContBacklit;
	private static LEDReactive ledContReactive;
	private static LEDWaveH ledContWaveH;
	private static LEDWaveV ledContWaveV;
	private static LEDTravel ledContTravel;
	
	// For developing the program (GUI, tweaks, etc...) without the keyboard connected.
	// TURN THIS OFF IN FINAL BUILDS.
	private boolean RUN_WITHOUT_DEVICE = false;
	private boolean DO_START_EFFECT = true;
	
	public static enum LEDMode {
		Backlit(1, 0, "BackLit", true), 
		ReactiveBacklit(2, 1, "Reactive(+Backlit)", true), 
		WaveH(16, 2, "Wave (Horizontal)", true), 
		WaveV(32, 3, "Wave (Vertical)", true), 
		Rain(4, 4, "Rain", false), 
		Random(8, 5, "Random", false);
		// Unimplemented: LED Spirals, Matrix, 
		
		public int id, idx;
		public String thisName;
		public boolean implemented = false;
		LEDMode(int v, int cv, String displayName, boolean impl) { this.id = v; this.idx = cv; thisName = displayName; implemented = impl; }
		public int getID () { return id; }
		public int getIdx () { return idx; }
		public String getDisplayName() { return thisName; }
		public boolean getImplemented () { return implemented; }
	};
	
	private static final int[][] keyMap = {
	//   ESC NULL F1   F2   F3   F4  NULL  F5   F6   F7   F8  NULL  F9  F10  F11  F12  PRT  SCR  PAU NULL NULL NULL NULL
		{ 8,  0,  16,  24,  32,  40,   0,  48,  56,  64,  72,   0,  80,  88,  96, 104, 112, 120, 128,   0,   0,   0,   0 },
		{ 9,  17, 25,  33,  41,  49,  57,  65,  73,  81,  89,   0,  97, 105, 129,   0,  15,  31,  47,  63,  79,  95, 111 },
		{ 10, 0,  18,  26,  34,  42,   0,  50,  58,  66,  74,  82,  90,  98, 106, 114,  23,  39,  55,  71,  87, 103, 102 },
		{ 11, 0,  19,  27,  35,  43,   0,  51,  59,  67,  75,  83,  91,  99, 115,   0,   0,   0,   0,  46, 119,  78,   0 },
		{ 12, 0,  36,  44,  52,  60,   0,  68,   0,  76,  84,  92, 100, 108, 124,   0,   0,  38,   0,  54,  62,  86, 118 },
		{ 13, 21, 29,   0,   0,   0,   0,  45,   0,   0,   0,   0,  85,  93, 109, 117,  14,  22,  30,  70,   0,  94,   0 }
	};
	
	public static final int VC_PIPE = 43, VC_SUPER = 3675, VC_ADD = 3662, VC_NUMLOCK = 69, VC_DIV = 53, VC_MUL = 3639, VC_MINUS = 3658, VC_QMARK = 53, VC_RSHIFT = 3638, VC_FULLSTOP = 83, VC_FUNCTION = 93;
	// Could be a bit cleaner...
	public static final int[][] keyMapKeycodes = {
		{ NativeKeyEvent.VC_ESCAPE, 0, NativeKeyEvent.VC_F1, NativeKeyEvent.VC_F2, NativeKeyEvent.VC_F3,  NativeKeyEvent.VC_F4, 0, NativeKeyEvent.VC_F5,  NativeKeyEvent.VC_F6, NativeKeyEvent.VC_F7, NativeKeyEvent.VC_F8, 0, NativeKeyEvent.VC_F9, NativeKeyEvent.VC_F10, NativeKeyEvent.VC_F11, NativeKeyEvent.VC_F12, NativeKeyEvent.VC_PRINTSCREEN, NativeKeyEvent.VC_SCROLL_LOCK, NativeKeyEvent.VC_PAUSE },
		{ 41, NativeKeyEvent.VC_1,  NativeKeyEvent.VC_2, NativeKeyEvent.VC_3, NativeKeyEvent.VC_4, NativeKeyEvent.VC_5, NativeKeyEvent.VC_6, NativeKeyEvent.VC_7, NativeKeyEvent.VC_8, NativeKeyEvent.VC_9, NativeKeyEvent.VC_0, 0, 12, 13, NativeKeyEvent.VC_BACKSPACE, 0,  NativeKeyEvent.VC_INSERT,  NativeKeyEvent.VC_HOME,  NativeKeyEvent.VC_PAGE_UP,  VC_NUMLOCK, VC_DIV, VC_MUL, VC_MINUS },
		{ NativeKeyEvent.VC_TAB, 0, NativeKeyEvent.VC_Q,NativeKeyEvent.VC_W, NativeKeyEvent.VC_E, NativeKeyEvent.VC_R, NativeKeyEvent.VC_R, NativeKeyEvent.VC_T, NativeKeyEvent.VC_Y, NativeKeyEvent.VC_U, NativeKeyEvent.VC_I, NativeKeyEvent.VC_O, NativeKeyEvent.VC_P, NativeKeyEvent.VC_OPEN_BRACKET, NativeKeyEvent.VC_CLOSE_BRACKET, VC_PIPE, NativeKeyEvent.VC_DELETE, NativeKeyEvent.VC_END, NativeKeyEvent.VC_PAGE_DOWN, NativeKeyEvent.VC_7, NativeKeyEvent.VC_8, NativeKeyEvent.VC_9, VC_ADD },
		{ NativeKeyEvent.VC_CAPS_LOCK, 0, NativeKeyEvent.VC_A, NativeKeyEvent.VC_S, NativeKeyEvent.VC_D, NativeKeyEvent.VC_F, 0, NativeKeyEvent.VC_G, NativeKeyEvent.VC_H, NativeKeyEvent.VC_J, NativeKeyEvent.VC_K, NativeKeyEvent.VC_L, NativeKeyEvent.VC_SEMICOLON, NativeKeyEvent.VC_QUOTE, NativeKeyEvent.VC_ENTER, 0, 0, 0, 0, NativeKeyEvent.VC_4, NativeKeyEvent.VC_5, NativeKeyEvent.VC_6 },
		{ NativeKeyEvent.VC_SHIFT, 0, NativeKeyEvent.VC_Z, NativeKeyEvent.VC_X, NativeKeyEvent.VC_C, NativeKeyEvent.VC_V, 0, NativeKeyEvent.VC_B, 0, NativeKeyEvent.VC_N, NativeKeyEvent.VC_M, NativeKeyEvent.VC_COMMA, NativeKeyEvent.VC_PERIOD, VC_QMARK, VC_RSHIFT, 0, 0, NativeKeyEvent.VC_UP, 0, NativeKeyEvent.VC_1, NativeKeyEvent.VC_2, NativeKeyEvent.VC_3 },
		{ NativeKeyEvent.VC_CONTROL, VC_SUPER, NativeKeyEvent.VC_ALT, 0, 0, 0, 0, NativeKeyEvent.VC_SPACE, 0, 0, 0, 0, NativeKeyEvent.VC_ALT, VC_FUNCTION, NativeKeyEvent.VC_CONTEXT_MENU, NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_LEFT, NativeKeyEvent.VC_DOWN, NativeKeyEvent.VC_RIGHT, NativeKeyEvent.VC_0, 0, VC_FULLSTOP, NativeKeyEvent.VC_ENTER },
	};
	
	// Function key doesn't work unfortunately. It does not trigger nativeKeyPressed for some reason.
	
	// Used for keys that exist multiple times on keyboard, e.g: lctrl & rctrl. These are all hard-coded. So they are unfortunately neither very manipulable nor developer-friendly.
	public static final KeyMapKey[] keysVariational = new KeyMapKey[] {
		// Ctrl keys
		new KeyMapKey(NativeKeyEvent.VC_CONTROL, new KeyVariant[] { new KeyVariant(0, 5, 2, "lctrl"), new KeyVariant(15, 5, 3, "rctrl"), }),
		// Alt keys
		new KeyMapKey(NativeKeyEvent.VC_ALT, new KeyVariant[] { new KeyVariant(2, 5, 2, "lalt"), new KeyVariant(12, 5, 3, "ralt"), }),
		// Return keys
		new KeyMapKey(NativeKeyEvent.VC_ENTER, new KeyVariant[] { new KeyVariant(14, 3, 1, "entermain"), new KeyVariant(22, 4, 4, "enternumpad"), }),
		// Slash
		new KeyMapKey(NativeKeyEvent.VC_SLASH, new KeyVariant[] { new KeyVariant(13, 4, 1, "slashmain"), new KeyVariant(20, 1, 4, "slashnumpad"), }),
		// PrtScrSysRq & Num-pad multiply. (Both use same key code.)
		new KeyMapKey(NativeKeyEvent.VC_PRINTSCREEN, new KeyVariant[] { new KeyVariant(16, 0, 1, "prtscrsysreq"), new KeyVariant(21, 1, 4, "mul"), }),
		// 0 - Zero
		new KeyMapKey(NativeKeyEvent.VC_0, new KeyVariant[] { new KeyVariant(10, 1, 1, "top_zero"), new KeyVariant(19, 5, 4, "zeronumpad"), }),
		// 1
		new KeyMapKey(NativeKeyEvent.VC_1, new KeyVariant[] { new KeyVariant(1, 1, 1, "top_1"), new KeyVariant(19, 4, 4, "num_1"), }),
		// 2
		new KeyMapKey(NativeKeyEvent.VC_2, new KeyVariant[] { new KeyVariant(2, 1, 1, "top_2"), new KeyVariant(20, 4, 4, "num_2"), }),
		// 3
		new KeyMapKey(NativeKeyEvent.VC_3, new KeyVariant[] { new KeyVariant(3, 1, 1, "top_3"), new KeyVariant(21, 4, 4, "num_3"), }),
		// 4
		new KeyMapKey(NativeKeyEvent.VC_4, new KeyVariant[] { new KeyVariant(4, 1, 1, "top_4"), new KeyVariant(19, 3, 4, "num_4"), }),
		// 5
		new KeyMapKey(NativeKeyEvent.VC_5, new KeyVariant[] { new KeyVariant(5, 1, 1, "top_5"), new KeyVariant(20, 3, 4, "num_5"), }),
		// 6
		new KeyMapKey(NativeKeyEvent.VC_6, new KeyVariant[] { new KeyVariant(6, 1, 1, "top_6"), new KeyVariant(21, 3, 4, "num_6"), }),
		// 7
		new KeyMapKey(NativeKeyEvent.VC_7, new KeyVariant[] { new KeyVariant(7, 1, 1, "top_7"), new KeyVariant(19, 2, 4, "num_7"), }),
		// 8
		new KeyMapKey(NativeKeyEvent.VC_8, new KeyVariant[] { new KeyVariant(8, 1, 1, "top_8"), new KeyVariant(20, 2, 4, "num_8"), }),
		// 9
		new KeyMapKey(NativeKeyEvent.VC_9, new KeyVariant[] { new KeyVariant(9, 1, 1, "top_9"), new KeyVariant(21, 2, 4, "num_9"), }),
	};
	
	public static void main(String[] args) throws IOException, InterruptedException {
		MainClass k = new MainClass();
		k.invoke(args);
	}
	
	public void invoke (String[] args) throws IOException, InterruptedException {
		System.out.println("Hello, world ! Invoked...");
		
		if (RUN_WITHOUT_DEVICE) {
			System.err.println("WARNING: RUN_WITHOUT_DEVICE IS TRUE! THIS SHOULD BE FALSE IF THE LEDS ARE TO BE SET !");
		}
		
		// Check if version matches with .XML file. If not throw an error to remind me xD
		final Properties prop = new Properties();
		prop.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));
		String xmlVer = prop.getProperty("swversion");
		if (xmlVer.equals(SOFTWARE_VERSION)) {
			System.out.println("SWVERSION EXTRACTED FROM XML: " + xmlVer + " [EQUAL]");
		}
		else {
			System.out.println("SWVERSION EXTRACTED FROM XML: " + xmlVer + " [IN-EQUAL, FATAL]");
			String msg = "The version in pom.xml does not match String SOFTWARE_VERSION from MainClass.java !\nTELL THE DEVELOPER TO CHANGE IT GODDAMNIT.";
			String[] options = new String[] { "O.K" };
			JOptionPane.showOptionDialog(null, msg, "ERROR", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
		}
		
		// Randomise on start so I dont get sick of the colours too quickly.
		int rand = ThreadLocalRandom.current().nextInt(0, 3);
		byte r = 0, g = 0, b = 0;
		r = (byte)ThreadLocalRandom.current().nextInt(0, 0xFF);
		g = (byte)ThreadLocalRandom.current().nextInt(0, 0xFF);
		b = (byte)ThreadLocalRandom.current().nextInt(0, 0xFF);
		if (rand == 0) { r = (byte)0xFF; }
		else if (rand == 1) { g = (byte)0xFF; }
		else if (rand == 2) { b = (byte)0xFF; }
		colour1 = (((r & 0xFFFFFFFF) << 24) & 0xFF000000) + 
				(((g & 0xFFFFFFFF) << 16) & 0x00FF0000) + 
				(((b & 0xFFFFFFFF) << 8) & 0x0000FF00);
		rand = ThreadLocalRandom.current().nextInt(0, 3);
		r = (byte)ThreadLocalRandom.current().nextInt(0, 0xFF);
		g = (byte)ThreadLocalRandom.current().nextInt(0, 0xFF);
		b = (byte)ThreadLocalRandom.current().nextInt(0, 0xFF);
		if (rand == 0) { r = (byte)0xFF; }
		else if (rand == 1) { g = (byte)0xFF; }
		else if (rand == 2) { b = (byte)0xFF; }
		colour2 = (((r & 0xFFFFFFFF) << 24) & 0xFF000000) + 
				(((g & 0xFFFFFFFF) << 16) & 0x00FF0000) + 
				(((b & 0xFFFFFFFF) << 8) & 0x0000FF00);
		
		// Key sniffer
		sniffer = new KeystrokeSniffer();
		sniffer.initialise(this);
		
		initialiseNotifyIcon();
		
		/*
		LookAndFeelInfo[] inst = UIManager.getInstalledLookAndFeels();
		for (int i = 0; i < inst.length; i++) { System.out.println(inst[i]); }
		//try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); }
		//try { UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel"); } 
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } 
		catch (InstantiationException e) {e.printStackTrace(); } 
		catch (IllegalAccessException e) {e.printStackTrace(); } 
		catch (ClassNotFoundException e) { e.printStackTrace(); } 
		catch (UnsupportedLookAndFeelException e) { e.printStackTrace(); }
		*/
		
		swUpdater = new Updater();
		//swUpdater.promptForUpdate();
		
		// Initialise registry(prefs)
		PrefsManager.initialise();
		System.out.println("startMinimised = " + PrefsManager.prefStartMinimised);
		System.out.println("capsSustain = " + PrefsManager.prefCapsSustain);
		
		// Initialise controllers. Pooled so not so much Garbage Collection. Not that that's even a problem xD
		int[] cols = new int[] { colour1, colour2 };
		ledContBacklit = new LEDBacklit(); ledContBacklit.setColours(cols);
		ledContReactive = new LEDReactive(); ledContReactive.setColours(cols);
		ledContWaveH = new LEDWaveH (); ledContWaveH.setColours(cols);
		ledContWaveV = new LEDWaveV (); ledContWaveV.setColours(cols);
		ledContTravel = new LEDTravel(); ledContTravel.setColours(cols);
		
		// Initialise GUI.
		gui = new GUIManager();
		gui.initialise();
		
		//boolean wasFocussed = gui.frame.isFocused();
		GUIManager.frame.requestFocus();
		capsOn = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
		System.out.println("Caps lock " + (capsOn ? "on" : "off") + " by default...");
		
		// May move argument handling into a seperate function
		if (args.length > 0) { 
			// Pass in arguments for colours.
			if (args.length == 2) {
				colour1 = Integer.parseInt(args[0]);
				colour2 = Integer.parseInt(args[1]);
			}
		}
		
		initialised = true;
		
		// Initialise actual HID device and relevent objects.
		HidServicesSpecification hidServiceSpecs = new HidServicesSpecification();
    	hidServiceSpecs.setAutoShutdown(true);
    	hidServiceSpecs.setScanInterval(200);
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
			hidDevice = device;
		}
		
		if (device != null || RUN_WITHOUT_DEVICE) {
			setLEDMode(LEDMode.Backlit);
			
			// Cool starting effect.
			if (DO_START_EFFECT) {
				doStartEffect();
			}
			
			// Main loop
			mainLoop();
			device.close();
		}
		else {
			System.err.println("DEVICE IS NULL");
		}
		
		services.shutdown();
		System.out.println("Applcation termination...");
	}
	
	private void mainLoop () throws InterruptedException {
		while (kill ^ true) {
			setLEDs(hidDevice);
			update = ledController.update();
			Thread.sleep(100);
			//Thread.sleep(200);
			while (!update) { Thread.sleep(1); }
		}
	}
	
	// Just for fun.
	private void doStartEffect () throws InterruptedException {
		ILEDController prev = ledController;
		ledController = ledContTravel;
		while (!((LEDTravel)ledController).done) {
			setLEDs(hidDevice);
			update = ledController.update();
			Thread.sleep(5);
			while (!update) { Thread.sleep(1); }
		}
		System.out.println("start effect a complete");
		final int offset = 10;
		int[] colOld = new int[ledContWaveH.colours.length];
		for (int i = 0; i < colOld.length; i++) { colOld[i] = ledContWaveH.colours[i]; }
		ledContWaveH.setColours(new int[] { 0xFFFFFF00, 0x00000000 });
		ledContWaveH.setWavePosition(-offset);
		ledController = ledContWaveH;
		for (int i = 0; i < POSEIDON_KEYSX + offset + 1; i++) {
			setLEDs(hidDevice);
			update = ((LEDWaveH)ledController).updateStartEffect();
			Thread.sleep(10);
			while (!update) { Thread.sleep(1); }
		}
		System.out.println("start effect b complete");
		ledContWaveH.setColours(colOld);
		ledController = prev;
		System.out.println("Done start effect");
	}
	
	private void setLEDs (HidDevice device) {
		if (RUN_WITHOUT_DEVICE || device == null || kill) {
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
    				int colour = ledController.getColourAtKey(x, y);
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
			if (valRG < 0) { System.err.println("ERROR SENDING BUFFER[RG]: " + device.getLastErrorMessage()); }
			
			Thread.sleep(10); // Was 1, set to 10 to prevent weird colour thing,
			
			// Send BLU buffer
			int valB = device.sendFeatureReport(bufferB, (byte)POSEIDON_START);
	    	if (valB < 0) { System.err.println("ERROR SENDING BUFFER[B]: " + device.getLastErrorMessage()); }
		}
		catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	private HidDevice getDevice () throws IOException {
		HidDevice hidDevice = null;
    	for (HidDevice device : services.getAttachedHidDevices()) {
    		//System.out.println(device);
    		if (device.getVendorId() == VENDOR_ID && device.getProductId() == PRODUCT_ID && device.getUsagePage() == 0xffffff01) {
    			boolean open = device.open();
    			//System.err.println(open);
    			if (open) { 
    				System.out.println(device);
    				hidDevice = device;
    				break;
    			}
    		}
    	}
    	return hidDevice;
	}
	
	public static void setLEDMode (LEDMode newMode) {
		ledMode = newMode;
		boolean reactive = false;
		switch (ledMode) {
			case Backlit: { ledController = ledContBacklit; } break;
			case ReactiveBacklit: { ledController = ledContReactive; reactive = true; } break;
			case WaveH: { ledController = ledContWaveH; } break;
			case WaveV: { ledController = ledContWaveV; } break;
			default: { ledController = ledContBacklit; } break;
		}
		
		if (reactive) {
			ledContReactive.setAllKeyLerpsZero();
			System.out.println("Setting reactive lerps to zero.");
		}
		
		int idx = ledMode.getIdx();
		if (GUIManager.combo != null && GUIManager.combo.getSelectedIndex() != idx) {
			GUIManager.combo.setSelectedIndex(idx);
		}
		
		update = true;
	}
	
	public static void refreshLEDColour () {
		ledController.setColours(new int[] { colour1, colour2 });
	}
	
	public static void refreshLEDs () { update = true; }
	
	private void initialiseNotifyIcon () {
		trayIcon = null;
		if (SystemTray.isSupported()) {
			System.out.println("System tray supported... Adding icon.");
			
			itemShow = new MenuItem("Restore Window");
			itemMin = new MenuItem("Minimise Window to Tray");
			MenuItem itemQuit = new MenuItem("Quit"); // TODO GET THIS WORKING
			
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage("ICON.gif");
			ActionListener listener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Object src = e.getSource();
					if (src == itemShow) {
						GUIManager.windowRestore();
					}
					else if (src == itemMin) {
						GUIManager.windowMinimise();
					}
					
					refreshNotifyPopupVisibilityStates();
				}
			};
			// Icon menu
			PopupMenu pop = new PopupMenu();
			
			// Set enabled if window is visible. (Only applies on startup here...)
			refreshNotifyPopupVisibilityStates();
			
			itemShow.addActionListener(listener);
			pop.add(itemShow);
			itemMin.addActionListener(listener);
			pop.add(itemMin);
			itemQuit.addActionListener(listener);
			pop.add(itemQuit);
			
			// Construct the actual icon
			trayIcon = new TrayIcon(image, "Poseidon Z RGB Controller", pop);
			trayIcon.addActionListener(listener);
			
			// Add to tray
			try {
				tray.add(trayIcon);
			}
			catch (AWTException e) {
				System.err.println(e);
			}
		}
		else {
			System.out.println("System tray NOT supported...");
		}
	}
	
	public static void refreshNotifyPopupVisibilityStates() {
		boolean showing = GUIManager.windowShowing;
		itemShow.setEnabled(showing ^ true);
		itemMin.setEnabled(showing);
	}
	
	@Override
	public void nativeKeyPressed(NativeKeyEvent arg0) {
		// Call key listen functions
		if (ledController instanceof ILEDListenableKeys) {
			((ILEDListenableKeys)ledController).keyPress(arg0);
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) { 
		if (ledController instanceof ILEDListenableKeys) {
			((ILEDListenableKeys)ledController).keyRelease(arg0);
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) { }
	
	private boolean dettaching = false;
	public void hidDeviceDetached(HidServicesEvent event) {
		if (dettaching) { return; }
    	dettaching = true;
    	
		String timestamp = new Timestamp(System.currentTimeMillis()).toString();
    	System.err.println("Device detached: [" + timestamp + "] " + event);
    	
    	HidDevice d = event.getHidDevice();
    	if (d.getProductId() == PRODUCT_ID && d.getVendorId() == VENDOR_ID) {
    		d.close();
    		hidDevice = null;
    		System.out.println("Poseidon Z RGB detached.");
    	}
    	
    	try { Thread.sleep(500); } 
    	catch (InterruptedException e) { e.printStackTrace(); }
    	dettaching = false;
    }

    public void hidFailure(HidServicesEvent event) {
    	System.err.println("HID failure: " + event);
    }
    
    private boolean attaching = false;
    public void hidDeviceAttached(HidServicesEvent event) {
    	if (attaching) { return; }
    	attaching = true;
    	
		String timestamp = new Timestamp(System.currentTimeMillis()).toString();
		System.err.println("Device attached: [" + timestamp + "] " + event);

    	HidDevice d = event.getHidDevice();
    	if (d.getProductId() == PRODUCT_ID && d.getVendorId() == VENDOR_ID) {
    		System.out.println("Poseidon Z RGB attached.");
    		
    		try { 
    			hidDevice = getDevice();
    			setAllKeyLerpsZero();
    			refreshLEDs();
    		} 
    		catch (IOException e) { e.printStackTrace(); }
    	}
    	
    	// Prevents console spam, and prevents LED's being set an insane amount of times.
    	try { Thread.sleep(500); } 
    	catch (InterruptedException e) { e.printStackTrace(); }
    	attaching = false;
    }
    
    public static void setAllKeyLerpsZero () {
    	if (ledController instanceof LEDReactive) {
    		((LEDReactive)ledController).setAllKeyLerpsZero ();
    	}
    }
}
