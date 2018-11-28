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
	public static float lerp (float a, float b, float t) { return (float)(a * (1.0 - t) + b * t); }
	
	public static byte lerpByte (byte a, byte b, float t) { return (byte)round((float)(a * (1.0 - t) + b * t)); }
	public static int lerpColour (int a, int b, float t) {
		byte r0 = (byte)((a >> 24) & 0xFF);
		byte g0 = (byte)((a >> 16) & 0xFF);
		byte b0 = (byte)((a >> 8) & 0xFF);
		byte r1 = (byte)((b >> 24) & 0xFF);
		byte g1 = (byte)((b >> 16) & 0xFF);
		byte b1 = (byte)((b >> 8) & 0xFF);
		byte rl = (byte)Utils.lerp(Byte.toUnsignedInt(r0), Byte.toUnsignedInt(r1), t);
		byte gl = (byte)Utils.lerp(Byte.toUnsignedInt(g0), Byte.toUnsignedInt(g1), t);
		byte bl = (byte)Utils.lerp(Byte.toUnsignedInt(b0), Byte.toUnsignedInt(b1), t);
		int rn = ((rl & 0xFFFFFFFF) << 24) & 0xFF000000;
		int gn = ((gl & 0xFFFFFFFF) << 16) & 0x00FF0000;
		int bn = ((bl & 0xFFFFFFFF) << 8) & 0x0000FF00;
		return rn + gn + bn;
	}
	
	// TODO: Find a way to round very fast. (My internet was down when I wrote this xD)
	public static int round (float x) { return Math.round(x);}
	
	// Sweet, concise, compact clamping function.
	public static int clamp (int n, int m, int x) {return n<= m?m:n>=x?x:n;}
	// Insanely fast Absolute function using bit-wise xor'ing and shifting...
	public static int abs(int x) {return (x+(x>>31))^(x>>31);}
	
	public static float abs (float x) { return x < 0.0 ? -x : x; }
	
	public static Color toRGB (int hex) { return new Color((hex>>24)&0xFF,(hex>>16)&0xFF,(hex>>8)&0xFF); }
	
	public static int toHex (Color c) {
		int r = ((c.getRed() & 0xFFFFFF) << 24) & 0xFF000000;
		int g = ((c.getGreen() & 0xFFFFFF) << 16) & 0x00FF0000;
		int b = ((c.getBlue() & 0xFFFFFF) << 8) & 0x0000FF00;
		return r + g + b;
	}
	
	public static float logerp(float a, float b, float t){
		return a * (float)Math.pow(b / a, t);
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
	
	public static float clamp(float n, float m, float x) {return n<= m?m:n>=x?x:n;}
	public static float coserp(float start, float end, float value)
    {
        return lerp(start, end, 1.0f - (float)Math.cos(value * Math.PI * 0.5f));
    }
	public static float sinerp(float start, float end, float value)
    {
        return lerp(start, end, (float)Math.sin(value * Math.PI * 0.5f));
    }
	public static float hermite(float start, float end, float value)
    {
        return lerp(start, end, value * value * (3.0f - 2.0f * value));
    }
	
	public static int evaluateWaveCurve (int[] points, float lerp) {
		int ret = 0x00000000;
		for (int i = 0; i < points.length; i++) {
			
		}
		return ret;
	}
	
	public static int lerpRainbow (float lerp) {
		float h = 0.5f;
		int ret = 0x00000000;
		
		int r = 0x00;
		int g = 0x00;
		int b = 0x00;
		
		float c = lerp / h;
		if (lerp <= h) {
			r = Utils.lerp(0xFF, 0x00, c);
			g = Utils.lerp(0x00, 0xFF, c);
			b = 0x00;
		}
		else {
			r = 0x00;
			g = Utils.lerp(0xFF, 0x00, c);
			b = Utils.lerp(0x00, 0xFF, c);
		}
		
		ret = (((r & 0xFFFFFFFF) << 24) & 0xFF000000) + 
				(((g & 0xFFFFFFFF) << 16) & 0x00FF0000) + 
				(((b & 0xFFFFFFFF) << 8) & 0x0000FF00);
		
		return ret;
	}
}
