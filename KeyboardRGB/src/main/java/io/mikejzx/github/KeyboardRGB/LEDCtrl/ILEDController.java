package io.mikejzx.github.KeyboardRGB.LEDCtrl;

public interface ILEDController {
	boolean update();
	int getColourAtKey(int keyx, int keyy);
	void setColours(int[] newColours);
}
