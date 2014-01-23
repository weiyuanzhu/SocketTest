package weiyuan.socket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GetCmd {
	
	
	private static final int port = 500;
	
	
	private List<Integer> rxBuffer = null;
	private List<Integer[]> eeprom = null;
	
	
	private Socket socket = null;
	private PrintWriter out = null;
	private InputStream in = null;
	
	public GetCmd() {
		super();
		this.rxBuffer = new ArrayList<Integer>();
		
		
	}

	public byte[] getCMD(String ip,char[] cmd)
	
	{
		
		byte[] rx = null;
		//thread id
		//System.out.println("I am running on OverallStatus" + Thread.currentThread().getName());
		
		/*System.out.print("Outstream: " );
		for(int i = 0; i<cmd.length;i++)
		{
			System.out.print((int)cmd[i]+ " ");
			
		}
		String test = new String(cmd,0,cmd.length);
    	System.out.println("\n"+ "Outstream chars= " + test);*/
    	
		
		try {
			// init socket and in/out stream
			socket = new Socket(ip,port);
			socket.setSoTimeout(0);
			
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"ISO8859_1")),false);
			in = socket.getInputStream();
   
			System.out.println("\nConnect to: " + socket.getInetAddress() + ": "+  socket.getPort());

			// send command to panel
			out.print(cmd);
			out.flush();

			/*
			 *   Receive bytes from panel and put in rxBuffer arrayList
			 */
			
			Thread.sleep(5000);
			
			System.out.println("in.available()= "+ in.available());
			
			
			int count = 0;
			while (count == 0) {
				   count = in.available();
				  }
			rx = new byte[count];
			
			int readCount = 0; 
			while (readCount < count) {
				
			   readCount += in.read(rx, readCount, count - readCount);
			}

			
			
			/*int data = 0;
			
			
			while(in.available()>0)
			{	
				
					data = in.read();	
					rxBuffer.add(data);	
					//System.out.print(in.available() + " ");
				
			}*/
			
		
			
			
			/*for(int j=0; j<rxBuffer.size();j+=1033)
			{
				System.out.println("------------------------Package " + j + "---------------------------");
				for(int i = j; i < j+1033;i++)
				{		
					System.out.print(rxBuffer.get(i)+ " ");
				}
				System.out.println();
			}*/
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{		
			try {
				if(socket != null)  
				{		
					out.close();
					in.close();
					socket.close();			
				}
				
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}		

		return rx;
	}
	
	

}
