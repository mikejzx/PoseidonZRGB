package io.mikejzx.github.KeyboardRGB.LEDCtrl;

import io.mikejzx.github.KeyboardRGB.Utils;

/*
	This is similar to wave, but instead the wave is applied to every key.
*/

public class LEDWaveG implements ILEDController {

	public int[] colours; // The colours to lerp between
	public int colourCurrent;

	private float lerp = 0.0f;
	private static final float lerpSpeed = 0.1f;
	
	@Override
	public boolean update() {
		lerp = Utils.clamp(lerp + lerpSpeed, 0.0f, 1.0f); 
		if (lerp == 1.0f) {
			lerp = 0.0f;
		}
		colourCurrent = Utils.lerpColour(colours[0], colours[1], lerp);
		
		try { Thread.sleep(10); } 
		catch (InterruptedException e) { e.printStackTrace(); }
		
		// Always updating.
		return true;
	}

	@Override
	public int getColourAtKey(int keyx, int keyy) {
		return colourCurrent;
	}

	@Override
	public void setColours(int[] newColours) {
		colours = newColours;
	}
}
