package io.mikejzx.github.KeyboardRGB;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Nice utilities
public final class Utils {
	
	public static String hex(int n) { return "0x" + String.format("%8s", Integer.toHexString(n)).replace(' ', '0').toUpperCase(); }
	
	public static String hex(byte n) { return "0x" + String.format("%8s", Integer.toHexString(n)).replace(' ', '0').toUpperCase().substring(6, 8); }
	
	public static int lerp (int a, int b, float t) { return round((float)(a * (1.0 - t) + b * t)); }
	
	public static byte lerpByte (byte a, byte b, float t) { return (byte)round((float)(a * (1.0 - t) + b * t)); }
	// TODO: Find a way to round very fast. (My internet was down when I wrote this xD)
	public static int round (float x) { return Math.round(x);}
	
	// Sweet, concise, compact clamping function.
	public static int clamp (int n, int m, int x) {return n<= m?m:n>=x?x:n;}
	// Insanely fast Absolute function using bit-wise xor'ing and shifting...
	public static int abs(int x) {return (x+(x>>31))^(x>>31);}
	
	public static Color toRGB (int hex) { return new Color((hex>>24)&0xFF,(hex>>16)&0xFF,(hex>>8)&0xFF); }
	
	public static int toHex (Color c) {
		int r = ((c.getRed() & 0xFFFFFF) << 24) & 0xFF000000;
		int g = ((c.getGreen() & 0xFFFFFF) << 16) & 0x00FF0000;
		int b = ((c.getBlue() & 0xFFFFFF) << 8) & 0x0000FF00;
		return r + g + b;
	}
	
	public static int[] getDupes(int[] arr, int exclude) {
		final Set<Integer> setToReturn = new HashSet<Integer>();
		final Set<Integer> set1 = new HashSet<Integer>();
		
		List<Integer> l = new ArrayList<Integer>();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != exclude) {
				l.add(arr[i]);
			}
		}
		for (Integer yourInt : l) {
			if (!set1.add(yourInt)) {
				setToReturn.add(yourInt);
			}
		}
		final Object[] ret = setToReturn.toArray();
		int[] fin = new int[ret.length];
		for (int i = 0; i < fin.length; i++) {
			fin[i] = (int)ret[i];
		}
		return fin;
	}
	
	public static int[] getDupes(List<Integer> arr, int exclude) {
		final Set<Integer> setToReturn = new HashSet<Integer>();
		final Set<Integer> set1 = new HashSet<Integer>();
		
		List<Integer> l = new ArrayList<Integer>();
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i) != exclude) {
				l.add(arr.get(i));
			}
		}
		for (Integer yourInt : l) {
			if (!set1.add(yourInt)) {
				setToReturn.add(yourInt);
			}
		}
		final Object[] ret = setToReturn.toArray();
		int[] fin = new int[ret.length];
		for (int i = 0; i < fin.length; i++) {
			fin[i] = (int)ret[i];
		}
		return fin;
	}
}
