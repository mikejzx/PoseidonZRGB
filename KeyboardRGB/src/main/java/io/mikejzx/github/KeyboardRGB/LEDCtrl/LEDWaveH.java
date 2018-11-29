package io.mikejzx.github.KeyboardRGB.LEDCtrl;

import io.mikejzx.github.KeyboardRGB.MainClass;
import io.mikejzx.github.KeyboardRGB.Utils;

public class LEDWaveH implements ILEDController {

	public int[] colours;
	private int[][] keyColours;
	private int waveStartX = 0;
	
	@Override
	public boolean update() {
		// Iterate through each. Sets colour based on x position
		
		for (int y = 0; y < MainClass.POSEIDON_KEYSY; y++) {
			for (int x = 0; x < MainClass.POSEIDON_KEYSX; x++) {
				float div = (float)(MainClass.POSEIDON_KEYSX + waveStartX);
				float lerp = (float)(x - waveStartX) / div;

				if (lerp < 0.0f || lerp > 1.0f) {
					lerp = (float)(((x * 2) + MainClass.POSEIDON_KEYSX) - waveStartX) / div;
					
					// For some reason, x needs to be multiplied by two, or else the previous wave,
					// will be moving at a faster rate than the original.
				}
				
				lerp = Utils.clamp(lerp, 0.0f, 1.0f); 
				//lerp = Utils.abs(lerp - 0.5f) * 2.0f;
				
				/* int colour = Utils.evaluateWaveCurve(rainbow, lerp); */
				//int colour = Utils.lerpRainbow(lerp);
				int colour = Utils.lerpColour(colours[0], colours[1], lerp);
				
				keyColours[x][y] = colour;
			}
		}
		
		try { Thread.sleep(100); } 
		catch (InterruptedException e) { e.printStackTrace(); }
		
		// Wrap
		waveStartX++;
		if (waveStartX >= MainClass.POSEIDON_KEYSX) {
			waveStartX = 0;
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
	
	public void setWavePosition(int newPos) {
		waveStartX = newPos;
	}
	
	// Modified version of update that smoothly goes back to colour a.
	public boolean updateStartEffect() {
		for (int y = 0; y < MainClass.POSEIDON_KEYSY; y++) {
			for (int x = 0; x < MainClass.POSEIDON_KEYSX; x++) {
				float div = (float)(MainClass.POSEIDON_KEYSX + waveStartX);
				float lerp = (float)(x - waveStartX) / div;
				//if (lerp < 0.0f || lerp > 1.0f) {lerp = (float)(((x * 2) + MainClass.POSEIDON_KEYSX) - waveStartX) / div;}
				lerp = Utils.clamp(lerp, 0.0f, 1.0f); 
				lerp = Utils.abs(lerp - 0.5f) * 2.0f;
				int colour = Utils.lerpColour(colours[0], colours[1], lerp * lerp);
				keyColours[x][y] = colour;
			}
		}
		
		//try { Thread.sleep(100); } 
		//catch (InterruptedException e) { e.printStackTrace(); }
		
		// Wrap
		waveStartX++;
		if (waveStartX >= MainClass.POSEIDON_KEYSX) {
			waveStartX = 0;
		}
		
		// Always updating.
		return true;
	}
}
