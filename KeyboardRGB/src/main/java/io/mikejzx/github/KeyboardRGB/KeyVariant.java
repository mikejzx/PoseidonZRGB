package io.mikejzx.github.KeyboardRGB;

public class KeyVariant {
	public int x, y, loc;
	public String brief = "";
	
	// Ctor
	public KeyVariant(int xp, int yp, int loca) {
		this.x =  xp;
		this.y = yp;
		this.loc = loca;
	}
	
	public KeyVariant(int xp, int yp, int loca, String brief_n) {
		this.x =  xp;
		this.y = yp;
		this.loc = loca;
		this.brief = brief_n;
	}
}
