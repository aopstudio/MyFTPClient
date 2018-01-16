package top.neusoftware.client;

import java.net.*;
import java.util.StringTokenizer;
import java.io.*;

public class Client {
	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket s=new Socket("2001:67c:1560:8001::14",21);
		Socket dataSocket;
		BufferedReader sin=new BufferedReader(new InputStreamReader(System.in));
		BufferedReader reader=new BufferedReader(new InputStreamReader(s.getInputStream()));
		PrintWriter writer=new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
		String response;
		String command=null;
		writer.print("Hello");
		writer.flush();
		response=reader.readLine();
		System.out.println(response);
	}
}
