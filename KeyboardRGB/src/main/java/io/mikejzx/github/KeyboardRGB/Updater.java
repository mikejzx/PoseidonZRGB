package io.mikejzx.github.KeyboardRGB;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Updater {

	/*
	 * This class is used for software-update-checking.
	 * Simply sends a HTTP request to a .txt file on my website hosted
	 * on GitHub pages.
	*/
	
	public void checkForSoftwareUpdate () {
		try {
			URL url = new URL("https://mikejzx.github.io/Files/keyboardrgb_version.txt");
			Scanner scan = new Scanner(url.openStream());
			System.out.println("Latest version: " + scan.next());
			scan.close();
		} 
		catch (MalformedURLException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }
	}
}
