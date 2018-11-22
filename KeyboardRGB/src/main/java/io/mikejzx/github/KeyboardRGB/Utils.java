package io.mikejzx.github.KeyboardRGB;

// Nice utilities
public final class Utils {
	
	public static String hex(int n) { return "0x" + String.format("%8s", Integer.toHexString(n)).replace(' ', '0').toUpperCase(); }
	
	public static String hex(byte n) { return "0x" + String.format("%8s", Integer.toHexString(n)).replace(' ', '0').toUpperCase().substring(6, 8); }
	
	public static int lerp (int a, int b, float t) {  
		return round((float)(a * (1.0 - t) + b * t)); 
	}
	public static byte lerpByte (byte a, byte b, float t) { return (byte)round((float)(a * (1.0 - t) + b * t)); }
	// TODO: Find a way to round very fast. (My internet was down when I wrote this xD)
	public static int round (float x) { return Math.round(x);}
	
	// Sweet, concise, compact clamping function.
	public static int clamp (int n, int m, int x) {return n<= m?m:n>=x?x:n;}
	// Insanely fast Absolute function using bit-wise xor'ing and shifting...
	public static int abs(int x) {return (x+(x>>31))^(x>>31);}
}
