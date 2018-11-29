package io.mikejzx.github.KeyboardRGB.LEDCtrl;

import io.mikejzx.github.KeyboardRGB.MainClass;

/*
 *  This unique effect is simply for display when the program starts,
 *  it is not selectable to run continously.
*/

public class LEDTravel implements ILEDController {
	private int[][] keyColours;
	private int[] currentPoint = { 0, 0 };
	private int[] currentPoint2 = { MainClass.keyMapKeycodes[MainClass.keyMapKeycodes.length - 1].length - 1, MainClass.keyMapKeycodes.length - 1 };
	private int currentLevel = 0;
	private int currentLevel2 = MainClass.keyMapKeycodes.length - 1;
	private boolean left = false;
	private boolean left2 = true;
	private boolean done1 = false;
	private boolean done2 = false;
	public boolean done = false;
	
	@Override
	public boolean update() {
		// Iterate through each. Sets colour based on x position
		done2 = true;
		for (int y = 0; y < MainClass.POSEIDON_KEYSY; y++) {
			if (done) { break; }
			for (int x = 0; x < MainClass.POSEIDON_KEYSX; x++) {
				int colour = keyColours[x][y];
				
				if (colour != 0x00000000) {
					byte r = (byte)((colour >> 24) & 0xFF);
					int r0 = Byte.toUnsignedInt(r) - 8;
					if (r0 < 0) { r0 = 0; }
					r = (byte)r0;
					
					int rn = ((r & 0xFFFFFFFF) << 24) & 0xFF000000;
					int gn = ((r & 0xFFFFFFFF) << 16) & 0x00FF0000;
					int bn = ((r & 0xFFFFFFFF) << 8) & 0x0000FF00;
					colour = (rn + gn + bn);
					keyColours[x][y] = colour;
				}
				
				// Currently at this point.
				if (((x == currentPoint[0] && y == currentPoint[1]) 
						|| (x == currentPoint2[0] && y == currentPoint2[1]))
						&& !done1) {
					colour = 0xFFFFFF00;
					keyColours[x][y] = colour;
				}
				
				if (currentLevel == currentLevel2) {
					done1 = true;
				}
				if (colour != 0x00) {
					done2 = false;
				}
			}
		}
		
		// On effect complete.
		if (done2 == true) {
			done = true;
		}
		
		if (currentPoint[0] < MainClass.keyMapKeycodes[currentLevel].length && currentPoint[0] > -1) {
			currentPoint[0] += left ? -1 : 1;
		}
		else {
			if (currentLevel < MainClass.keyMapKeycodes.length - 1) {
				int x = 0;
				if (currentLevel % 2 == 0) {
					x = MainClass.keyMapKeycodes[currentLevel + 1].length - 1;
					left = true;
				}
				else {
					left = false;
				}
				currentLevel++;
				currentPoint = new int[] { x, currentLevel };
			}
		}
		
		if (currentPoint2[0] < MainClass.keyMapKeycodes[currentLevel2].length && currentPoint2[0] > -1) {
			currentPoint2[0] += left2 ? -1 : 1;
		}
		else {
			if (currentLevel2 > 0) {
				int x = 0;
				if (currentLevel2 % 2 == 0) {
					x = MainClass.keyMapKeycodes[currentLevel2 - 1].length - 1;
					left2 = true;
				}
				else {
					left2 = false;
				}
				currentLevel2--;
				currentPoint2 = new int[] { x, currentLevel2 };
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
		keyColours = new int[MainClass.POSEIDON_KEYSX][MainClass.POSEIDON_KEYSY];
	}
}
