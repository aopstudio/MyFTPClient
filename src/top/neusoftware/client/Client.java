package top.neusoftware.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class Client {
	Socket s;
	Socket dataSocket;
	BufferedReader sin;
	BufferedReader reader;
	PrintWriter writer;
	String response;
	String command;
	public Client() throws UnknownHostException, IOException {
		s=new Socket("127.0.0.1",21);
		sin=new BufferedReader(new InputStreamReader(System.in));
		reader=new BufferedReader(new InputStreamReader(s.getInputStream()));
		writer=new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
	}
	
	public void firstAccess() throws IOException {
		writer.println("Hello");
		writer.flush();
		response=reader.readLine();
		System.out.println(response);
	}
	public void sendCommand() throws UnknownHostException, IOException {
		firstAccess();
		while(!(command=sin.readLine()).equals("QUIT")){
			if(!command.equals("PASV")) {
				writer.println(command);
				writer.flush();
				response=reader.readLine();
				System.out.println(response);
			}
			else {
				writer.println(command);
				writer.flush();
				response=reader.readLine();
				System.out.println(response);
				if(response.startsWith("227")) {
					StringTokenizer tk=new StringTokenizer(response,",");
					for(int i=0;i<4;i++) {
						tk.nextToken();
					}
					int p1=Integer.valueOf(tk.nextToken());
					String str2=tk.nextToken();
					int p2=Integer.valueOf(str2.substring(0,str2.length()-1));
					int dpNumber=p1*256+p2;
					System.out.println(dpNumber);
					dataSocket=new Socket("127.0.0.1",dpNumber);
				}
				else {
					continue;
				}
			}
		}
		writer.println(command);
		writer.flush();
	}
	public void getFile() throws UnsupportedEncodingException, IOException {
		DataInputStream dis = new DataInputStream(dataSocket.getInputStream());
		String s = "";
		while ((s = dis.readLine()) != null) {
		String l = new String(s.getBytes("ISO-8859-1"), "utf-8");
		System.out.println(l);
		}
	}
	public void sendFile() {
		FileInputStream fis = null;
		
	}
	/*writer.println("USER root");
	writer.flush();
	response=reader.readLine();
	System.out.println(response);*/
}