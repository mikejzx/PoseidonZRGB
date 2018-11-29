package io.mikejzx.github.KeyboardRGB;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class GUISquare extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private Color colour;
	
	private int x;
	private int y;
	private final int width = 32;
	private final int height = 32;
	
	public GUISquare(int x, int y, Color c){
	    this.x = x;
	    this.y = y;
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
	    g.fillRect(x, y, width, height);
	    
	    // Outline
	    g.setColor(Color.black);
	    g.drawRect(x, y, width, height);
	}
}
