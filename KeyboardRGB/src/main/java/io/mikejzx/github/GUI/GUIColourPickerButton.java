package io.mikejzx.github.GUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;

import io.mikejzx.github.KeyboardRGB.MainClass;
import io.mikejzx.github.KeyboardRGB.Utils;

/*
 * This class is a JButton that opens a JColorPicker on click.
 * (Made to simply all the bullshit)
 * Each instance will have corresponding GUISquare
 * */

public class GUIColourPickerButton extends JButton implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private String colourPickerTitle = "Colour Picker";
	private int[] reference; // int[]'s are immutable. So I can pass it by reference !
	private int index = 0;
	private GUISquare sqr;
	
	// Ctor
	public GUIColourPickerButton(String lab, String colourPickTitle, int[] ref, int idx, GUISquare square) {
		super(lab);
		System.out.println("[GUIColourPickerButton.java] ctor called");
		
		this.colourPickerTitle = colourPickTitle;
		this.addActionListener(this);
		this.reference = ref;
		this.index = idx;
		this.sqr = square;
		if (ref != null && ref.length > 0) {
			this.sqr.setColour(Utils.toRGB(ref[idx]));
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("[GUIColourPickerButton.java] actionPerformed: cmd:" + e.getActionCommand());
		Color col = JColorChooser.showDialog(null, colourPickerTitle, Utils.toRGB(reference[index]));
		if (col != null) {
			reference[index] = Utils.toHex(col);
			MainClass.setAllKeyLerpsZero(); 
			MainClass.refreshLEDs();
			sqr.setColour(col);
		}
	}
}
