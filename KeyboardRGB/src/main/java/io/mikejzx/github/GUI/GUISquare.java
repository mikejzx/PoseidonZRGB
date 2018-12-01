package io.mikejzx.github.GUI;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

/*
 * This displays a square, basically...
*/

public class GUISquare extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private Color colour;
	
	private final int width = 32;
	private final int height = 32;
	
	public GUISquare(Color c){
	    this.colour = c;
	}

	public void setColour (Color newColour) {
		colour = newColour;
		revalidate();
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    
	    // Main rect
	    g.setColor(colour);
	    g.fillRect(0, 0, width, height);
	    
	    // Outline
	    g.setColor(Color.black);
	    g.drawRect(0, 0, width, height);
	}
}
