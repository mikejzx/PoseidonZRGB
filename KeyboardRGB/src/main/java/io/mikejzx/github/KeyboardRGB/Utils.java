package io.mikejzx.github.KeyboardRGB;

public final class Utils {
	
	public static String hex(int n) {
	    return "0x" + String.format("%8s", Integer.toHexString(n)).replace(' ', '0').toUpperCase();
	}
	
	public static String hex(byte n) {
	    return "0x" + String.format("%8s", Integer.toHexString(n)).replace(' ', '0').toUpperCase().substring(6, 8);
	}
}
