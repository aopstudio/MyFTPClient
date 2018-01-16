package top.neusoftware.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class Main {
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
					dataSocket=new Socket("2001:67c:1560:8001::14",dpNumber);
				}
				else {
					continue;
				}
			}
		}
		writer.println(command);
		writer.flush();
		/*writer.println("USER root");
		writer.flush();
		response=reader.readLine();
		System.out.println(response);*/
	}
}
