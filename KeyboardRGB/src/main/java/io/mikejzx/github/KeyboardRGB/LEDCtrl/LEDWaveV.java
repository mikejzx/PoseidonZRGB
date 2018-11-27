package io.mikejzx.github.KeyboardRGB.LEDCtrl;

import io.mikejzx.github.KeyboardRGB.MainClass;
import io.mikejzx.github.KeyboardRGB.Utils;

public class LEDWaveV implements ILEDController {

	private int[] colours;
	private int[][] keyColours;
	private int waveStartY = 0;
	
	@Override
	public boolean update() {
		// Iterate through each. Sets colour based on x position
		for (int y = 0; y < MainClass.POSEIDON_KEYSY; y++) {
			for (int x = 0; x < MainClass.POSEIDON_KEYSX; x++) {
				float div = (float)(MainClass.POSEIDON_KEYSY + waveStartY);
				float lerp = (float)(y - waveStartY) / div;
				
				if (lerp < 0.0f || lerp > 1.0f) {
					lerp = (float)((y + MainClass.POSEIDON_KEYSY) - waveStartY) / (float)(waveStartY);
				}
				
				lerp = Utils.clamp(lerp, 0.0f, 1.0f); 
				//lerp = Utils.abs(lerp - 0.5f) * 2.0f;
				int colour = Utils.lerpColour(colours[0], colours[1], lerp);
				
				keyColours[x][y] = colour;
			}
		}
		
		try { Thread.sleep(50); } 
		catch (InterruptedException e) { e.printStackTrace(); }
		
		// Wrap
		waveStartY++;
		if (waveStartY >= MainClass.POSEIDON_KEYSY) {
			waveStartY = 0;
		}
		
		// Always updating.
		return true;
	}

	@Override
	public int getColourAtKey(int keyx, int keyy) {
		return keyColours[keyx][keyy];
	}

	@Override
	public void setColours(int[] newColours) {
		colours = newColours;
		keyColours = new int[MainClass.POSEIDON_KEYSX][MainClass.POSEIDON_KEYSY];
	}
}
