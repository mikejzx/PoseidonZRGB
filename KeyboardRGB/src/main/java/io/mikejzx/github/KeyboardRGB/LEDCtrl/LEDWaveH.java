package io.mikejzx.github.KeyboardRGB.LEDCtrl;

import io.mikejzx.github.KeyboardRGB.MainClass;
import io.mikejzx.github.KeyboardRGB.Utils;

public class LEDWaveH implements ILEDController {

	private int[] colours;
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
					
					//lerp = (float)((x + MainClass.POSEIDON_KEYSX) - waveStartX) / (float)(waveStartX);
				}
				
				lerp = Utils.clamp(lerp, 0.0f, 1.0f); 
				//lerp = Utils.abs(lerp - 0.5f) * 2.0f;
				
				/*
				int[] rainbow = new int[] {
					0xFF000000, // Red
					0x00FF0000, // Green
					0x0000FF00, // Blue
				};
				int colour = Utils.evaluateWaveCurve(rainbow, lerp);
				*/
				
				int colour = Utils.lerpRainbow(lerp);
						
				//int colour = Utils.lerpColour(colours[0], colours[1], lerp);
				//colour = Utils.lerpColour(0x00ff0000, 0xff000000, lerp);
				//if (lerp == 0.0f || lerp == 0.0f) { colour = 0x00000000; }
				
				keyColours[x][y] = colour;
			}
		}
		
		//try { Thread.sleep(200); } 
		//catch (InterruptedException e) { e.printStackTrace(); }
		
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
