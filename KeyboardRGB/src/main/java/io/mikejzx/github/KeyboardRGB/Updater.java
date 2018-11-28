package io.mikejzx.github.KeyboardRGB;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Updater {

	/*
	 * This class is used for software-update-checking.
	 * Simply sends a HTTP request to a raw .txt file, and reads it.
	*/
	
	public void promptForUpdate () {
		String msg = "Would you like to check for software updates?\nYou may need to give the program permission through your PC's firewall,\nas a HTTP request must be sent to check for the latest software update.";
		String[] options = new String[] { "O.K", "Cancel" };
		int result = JOptionPane.showOptionDialog(null, msg, "Poseidon Z RGB Controller Updater", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		switch (result) {
			case (0): {
				checkForSoftwareUpdate();
			} break;
			
			case (1): {
				return;
			}
		}
	}
	
	public void checkForSoftwareUpdate () {
		try {
			// This link doesn't yet exist. So don't bother trying to run this function.
			//URL url = new URL("https://mikejzx.github.io/Files/keyboardrgb_version.txt");
			URL url = new URL("https://mikejzx.github.io/Downloadables/keyboardrgb_version.txt");
			Scanner scan = new Scanner(url.openStream());
			System.out.println("Latest version: " + scan.next());
			scan.close();
		} 
		catch (MalformedURLException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }
	}
}
