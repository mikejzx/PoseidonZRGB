package io.mikejzx.github.KeyboardRGB.LEDCtrl;

import io.mikejzx.github.KeyboardRGB.MainClass;
import io.mikejzx.github.KeyboardRGB.Utils;

/*
 * Update(01.12.2018) : This class has been merged with LEDWaveV.java ! No longer seperate.
 * */

public class LEDWave implements ILEDController {

	public int[] colours;
	private int[][] keyColours;
	private int waveStart = 0;
	public enum WaveDirection {
		UP, DOWN, LEFT, RIGHT
	};
	public WaveDirection waveDir = WaveDirection.DOWN;
	
	@Override
	public boolean update() {
		int max = MainClass.POSEIDON_KEYSX;
		if (waveDir == WaveDirection.DOWN || waveDir == WaveDirection.UP) {
			max = MainClass.POSEIDON_KEYSY;
		}
		
		// Iterate through each. Sets colour based on x position
		for (int y = 0; y < MainClass.POSEIDON_KEYSY; y++) {
			for (int x = 0; x < MainClass.POSEIDON_KEYSX; x++) {
				int val = x;
				
				// Vertical wave
				if (waveDir == WaveDirection.DOWN || waveDir == WaveDirection.UP) {
					val = y;
				}
				
				float div = (float)(max + waveStart);
				float lerp = (float)(val - waveStart) / div;

				if (lerp < 0.0f || lerp > 1.0f) {
					lerp = (float)(((val * 2) + max) - waveStart) / div;
					// Needs to be multiplied by two, or else the previous wave, moves faster.
				}
				
				lerp = Utils.clamp(lerp, 0.0f, 1.0f); 
				int colour = Utils.lerpColour(colours[0], colours[1], lerp);
				keyColours[x][y] = colour;
			}
		}
		
		try { Thread.sleep(100); } 
		catch (InterruptedException e) { e.printStackTrace(); }
		
		// Wrap
		if (waveDir == WaveDirection.LEFT || waveDir == WaveDirection.DOWN) {
			waveStart++;
			if (waveStart >= max) {
				waveStart = 0;
			}
		}
		else {
			waveStart--;
			if (waveStart <= 0) {
				waveStart = max;
			}
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
		waveStart = newPos;
	}
	
	// Modified version of update that smoothly goes back to colour a.
	public boolean updateStartEffect() {
		for (int y = 0; y < MainClass.POSEIDON_KEYSY; y++) {
			for (int x = 0; x < MainClass.POSEIDON_KEYSX; x++) {
				float div = (float)(MainClass.POSEIDON_KEYSX + waveStart);
				float lerp = (float)(x - waveStart) / div;
				//if (lerp < 0.0f || lerp > 1.0f) {lerp = (float)(((x * 2) + MainClass.POSEIDON_KEYSX) - waveStartX) / div;}
				lerp = Utils.clamp(lerp, 0.0f, 1.0f); 
				lerp = Utils.abs(lerp - 0.5f) * 2.0f;
				int colour = Utils.lerpColour(colours[0], colours[1], lerp * lerp);
				keyColours[x][y] = colour;
			}
		}
		
		// Wrap
		waveStart++;
		if (waveStart >= MainClass.POSEIDON_KEYSX) {
			waveStart = 0;
		}
		
		// Always updating.
		return true;
	}
}
