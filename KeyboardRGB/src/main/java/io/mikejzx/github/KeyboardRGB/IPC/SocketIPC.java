package io.mikejzx.github.KeyboardRGB.IPC;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import io.mikejzx.github.KeyboardRGB.IPC.CommandObjectIPC;

/* 
	-- Used for passing keystroke information from Python program.
	Thanks to dilbert for the reference:
		https://stackoverflow.com/questions/17262364/passing-data-from-a-java-program-to-a-python-program-and-getting-results-back
*/

public class SocketIPC {
	
	public PrintWriter out;
	public BufferedReader in;
	
	private Socket socket = null;
	private ServerSocket serverSocket = null;
	private ConnectionListener connectListener = null;
	private DataListener dataListener = null;
	private Thread connectListenerThread = null;
	private Thread dataListenerThread = null;
	private CommandObjectIPC ipcEventCmd = null;
	
	// Constructor
	public SocketIPC(int port) {
		ipcEventCmd = new CommandObjectIPC();
		connectListener = new ConnectionListener(port);
		connectListenerThread = new Thread(connectListener);
		connectListenerThread.start();
	}
	
	public void send(String msg) {
		if (out != null) {
			out.println(msg);
		}
	}
	
	public void flush () {
		if (out != null) {
			out.flush();
		}
	}
	
	public void close() {
		if (out != null) {
			out.flush();
			out.close();
			try {
				in.close();
				socket.close();
				serverSocket.close();
			}
			catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	public String recv() throws IOException {
		if (in != null) { return in.readLine(); }
		return "";
	}
	
	public void setCmd (CommandObjectIPC eventCmd) {
		if (eventCmd != null) {
			this.ipcEventCmd = eventCmd;
		}
	}
	
	class ConnectionListener extends Thread {
		private int _port;
		
		// Constructor
		public ConnectionListener (int port) { this._port = port; }
		@Override
		public void run () {
			try {
				serverSocket = new ServerSocket(_port);
				socket = serverSocket.accept();
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
				dataListener = new DataListener();
				dataListenerThread = new Thread(dataListener);
				dataListenerThread.start();
			}
			catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	class DataListener extends Thread {
		String dataStr = null;
		
		// Constructor
		public DataListener() {}
		
		@Override
		public void run() {
			try {
				while (true) {
					dataStr = recv();
					ipcEventCmd.buffer.add(dataStr);
					ipcEventCmd.execute();
				}
			}
			catch (Exception e) { e.printStackTrace(); }
		}
		
		public String read () {
			String returnVal = null;
			if (!ipcEventCmd.buffer.isEmpty()) {
				returnVal = ipcEventCmd.buffer.remove(0);
			}
			return returnVal;
		}
	}
}
