package top.neusoftware.client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

public class Client {
	Socket s;
	Socket dataSocket;
	BufferedReader sin;
	BufferedReader reader;
	PrintWriter writer;
	String response;
	String command;
	String filePath="E:/";//默认工作空间是E盘
	private static DecimalFormat df = null;
	static {  
        // 设置数字格式，保留一位有效小数  
        df = new DecimalFormat("#0.0");  
        df.setRoundingMode(RoundingMode.HALF_UP);  
        df.setMinimumFractionDigits(1);  
        df.setMaximumFractionDigits(1);  
    }  
	public Client() throws UnknownHostException, IOException {
		s=new Socket("127.0.0.1",3000);
		sin=new BufferedReader(new InputStreamReader(System.in));
		reader=new BufferedReader(new InputStreamReader(s.getInputStream()));
		writer=new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
	}
	
	public void firstAccess() throws IOException {
		//writer.println("Hello");
		//writer.flush();
		response=reader.readLine();
		System.out.println(response);
	}
	public void sendCommand() throws UnknownHostException, IOException, InterruptedException {
		firstAccess();
		while(!(command=sin.readLine()).equals("QUIT")){
			if(command.equals("PASV")) {
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
			else if(command.startsWith("LIST")) {
				writer.println(command);
				writer.flush();
				response=reader.readLine();
				System.out.println(response);
				if(response.startsWith("150"))
					while(!(response=reader.readLine()).equals("EOF")) {
						System.out.println(response);
					}
			}
			else if(command.startsWith("STOR")) {
				if(command.length()>5) {
					File file1=new File(filePath+command.substring(5));//当前工作目录下路径
					File file2=new File(command.substring(5));//全局路径
					if(file1.exists()) {
						writer.println(command);
						writer.flush();
						response=reader.readLine();
						System.out.println(response);
						if(response.startsWith("150")) {
							sendFile(filePath+command.substring(5));
						}
					}
					else if(file2.exists()) {
						writer.println(command);
						writer.flush();
						response=reader.readLine();
						System.out.println(response);
						if(response.startsWith("150")) {
							sendFile(command.substring(5));
						}
					}
				}
				else
					System.out.println("501 syntax error");
			}
			else if(command.startsWith("RETR")) {			
				writer.println(command);
				writer.flush();
				response=reader.readLine();
				System.out.println(response);
				if(response.startsWith("150")) {
					receiveFile();
				}
			}
			else {
				writer.println(command);
				writer.flush();
				response=reader.readLine();
				System.out.println(response);					
			}
		}
		writer.println(command);
		writer.flush();
	}
	public void sendFile(String fileName) throws IOException {
		
		DataOutputStream dos=null;	
		FileInputStream fis=null;
		
		try {  
            File file1 = new File(filePath+fileName);//在当前工作路径寻找
            File file2=new File(fileName);//全局寻找
            if(file1.exists()) {  
                fis = new FileInputStream(file1);  
                dos = new DataOutputStream(dataSocket.getOutputStream());  
  
                // 文件名和长度  
                dos.writeUTF(file1.getName());  
                dos.flush();  
                dos.writeLong(file1.length());  
                dos.flush();  
  
                // 开始传输文件  
                System.out.println("======== 开始传输文件 ========");  
                byte[] bytes = new byte[1024];  
                int length = 0;  
                long progress = 0;  
                while((length = fis.read(bytes, 0, bytes.length)) != -1) {  
                    dos.write(bytes, 0, length);  
                    dos.flush();  
                    progress += length;  
                    System.out.print("| " + (100*progress/file1.length()) + "% |");  
                }  
                System.out.println();  
                System.out.println("======== 文件传输成功 ========");  
            }  
            else if(file2.exists()) {  
                fis = new FileInputStream(file2);  
                dos = new DataOutputStream(dataSocket.getOutputStream());  
  
                // 文件名和长度  
                dos.writeUTF(file2.getName());  
                dos.flush();  
                dos.writeLong(file2.length());  
                dos.flush();  
  
                // 开始传输文件  
                System.out.println("======== 开始传输文件 ========");  
                byte[] bytes = new byte[1024];  
                int length = 0;  
                long progress = 0;  
                while((length = fis.read(bytes, 0, bytes.length)) != -1) {  
                    dos.write(bytes, 0, length);  
                    dos.flush();  
                    progress += length;  
                    System.out.print("| " + (100*progress/file2.length()) + "% |");  
                }  
                System.out.println();  
                System.out.println("======== 文件传输成功 ========");  
            }
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if(fis != null)  
                fis.close();  
            if(dos != null)  
                dos.close();  
            dataSocket.close();  
        }  
	}
	public void receiveFile() throws IOException, InterruptedException {
			
			byte[] inputByte = null;  

	        DataInputStream dis = null;  
	        FileOutputStream fos = null;  
	        
	        try {  
                dis = new DataInputStream(dataSocket.getInputStream());  
  
                // 文件名和长度  
                String fileName = dis.readUTF();  
                long fileLength = dis.readLong();  
                File directory = new File(filePath);  
                if(!directory.exists()) {  
                    directory.mkdir();  
                }  
                File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);  
                fos = new FileOutputStream(file);  
  
                // 开始接收文件  
                byte[] bytes = new byte[1024];  
                int length = 0;  
                while((length = dis.read(bytes, 0, bytes.length)) != -1) {  
                    fos.write(bytes, 0, length);  
                    fos.flush();  
                }  
                System.out.println("======== 文件接收成功 [File Name：" + fileName + "] [Size：" + getFormatFileSize(fileLength) + "] ========");  
            } catch (Exception e) {  
                e.printStackTrace();  
            } finally {  
                try {  
                    if(fos != null)  
                        fos.close();  
                    if(dis != null)  
                        dis.close();  
                    dataSocket.close();  
                } catch (Exception e) {}  
            }  
        }  
		private String getFormatFileSize(long length) {  
		    double size = ((double) length) / (1 << 30);  
		    if(size >= 1) {  
		        return df.format(size) + "GB";  
		    }  
		    size = ((double) length) / (1 << 20);  
		    if(size >= 1) {  
		        return df.format(size) + "MB";  
		    }  
		    size = ((double) length) / (1 << 10);  
		    if(size >= 1) {  
		        return df.format(size) + "KB";  
		    }  
		    return length + "B";  
		}  
	/*writer.println("USER root");
	writer.flush();
	response=reader.readLine();
	System.out.println(response);*/
}