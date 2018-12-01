package io.mikejzx.github.KeyboardRGB.LEDCtrl;

// The simplest form of LED lighting. Static LED's basically.

public class LEDBacklit implements ILEDController {
	
	public int[] colours;
	
	@Override
	// Backlit only has 1 colour.
	public void setColours (int[] newColours) {
		colours = new int[] { newColours[0] };
	}
	
	@Override
	// Returns the the update thing.
	public boolean update() {
		return false;
	}
	
	@Override
	public int getColourAtKey (int keyx, int keyy) {
		return colours[0];
	}
}
