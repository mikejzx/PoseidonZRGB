package io.mikejzx.github.KeyboardRGB.IPC;

import java.util.ArrayList;
import java.util.List;

public class CommandObjectIPC {
	public List<String> buffer;
	
	// Ctor
	public CommandObjectIPC () {
		this.buffer = new ArrayList<String>();
	}
	
	public void execute () {};
}
