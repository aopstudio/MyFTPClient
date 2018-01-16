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
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		Client cli=new Client();
		cli.sendCommand();
	}
}
