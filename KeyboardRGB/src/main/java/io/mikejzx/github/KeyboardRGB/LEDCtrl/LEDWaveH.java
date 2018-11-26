package io.mikejzx.github.KeyboardRGB.LEDCtrl;

import io.mikejzx.github.KeyboardRGB.MainClass;
import io.mikejzx.github.KeyboardRGB.Utils;

public class LEDWaveH implements ILEDController {

	private int[] colours;
	private int[][] keyColours;
	private int waveStartX = 0;
	
	private final float wavelength = 4;
	
	@Override
	public boolean update() {
		// Iterate through each. Sets colour based on x position
		
		for (int y = 0; y < MainClass.POSEIDON_KEYSY; y++) {
			for (int x = 0; x < MainClass.POSEIDON_KEYSX; x++) {
				float lerp = (float)(x - waveStartX) / (float)(MainClass.POSEIDON_KEYSX + waveStartX);

				if (lerp < 0.0f) {
					lerp = (float)(x - waveStartX - MainClass.POSEIDON_KEYSX) / (float)(MainClass.POSEIDON_KEYSX + waveStartX);
				}
				
				lerp = Utils.clamp(lerp, 0.0f, 1.0f);
				
				int colour = Utils.lerpColour(colours[0], colours[1], lerp);
				
				if (lerp == 0.0f) { colour = 0x00; }
				
				int xClamp = Utils.clamp(x, 0, keyColours.length - 1);
				keyColours[xClamp][y] = colour;
			}
		}
		
		/*
		for (int y = 0; y < MainClass.POSEIDON_KEYSY; y++) {
			for (int x = (int) (waveStartX - wavelength); x < waveStartX; x++) {
				float lerp = (float)x / ((float)waveStartX + (float)wavelength);
				lerp = Utils.clamp(lerp, 0.0f, 1.0f);
				int colour = Utils.lerpColour(colours[0], colours[1], lerp);
				
				int xClamp = Utils.clamp(x, 0, keyColours.length - 1);
				keyColours[xClamp][y] = colour;
			}
		}
		*/
		
		try { Thread.sleep(100); } 
		catch (InterruptedException e) { e.printStackTrace(); }
		
		// Wrap
		waveStartX++;
		if (waveStartX >= MainClass.POSEIDON_KEYSX) {
			waveStartX = 0;
		}
		//System.out.println(waveStartX);
		
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