package weiyuan.socket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

public class Connection {
	
	
	//interface for callback
	public interface ConnectionDelegattion 
	{
			void receive(List<Integer> rx);
	}
	
	//private fields
	private ConnectionDelegattion delegate;
	
	private int port;
	
	private boolean isClosed; // a flag for stop the background listening 
	
	private Socket socket;
	
	private List<Integer> rxBuffer; //buffer for receive data


	private PrintWriter out;
	private InputStream in; 
	

	
	
	//Constructor , requires a delegation object for callback
	public Connection(ConnectionDelegattion delegator)
	{
		
		this.isClosed = false;
		this.rxBuffer = new ArrayList<Integer>();
		this.port = 500;
		this.delegate = delegator;
	}

	
	//set this.isClosed
	public void setIsClosed(boolean bool)
	{
		this.isClosed = bool;
	}
	
	/*	function for pull data from panel
	 *  this function will start a new background thread to receive data from panel
	 * 
	 * */
	 
	public void fetchData(){

		
		new Thread(fetch).start();
		System.out.println("connection started ");
		
	}

	/*	runnable for thread
	 *	thread will keep listening on socket.inputstream until received data is complete
	 *	and then calls its delegate to 
	 * 
	*/
	Runnable fetch = new Runnable(){

		@Override
		public void run() {
			System.out.println("Slaver is doing job on a new thread.");
			
			char[] getPackageTest = new char[] {2, 165, 64, 15, 96, 0,0x5A,0xA5,0x0D,0x0A};
			char[] getConfig = new char[] {0x02,0xA0,0x21,0x68,0x18,0x5A,0xA5,0x0D,0x0A};
			
			try {
				// init socket and in/out stream
				
				socket = new Socket("192.168.1.23",port);	
				socket.setSoTimeout(0);
				socket.setReceiveBufferSize(20000);
				isClosed = false;
				
				
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"ISO8859_1")),false);
				in = socket.getInputStream();
	   
				System.out.println("\nConnected to: " + socket.getInetAddress() + ": "+  socket.getPort());

				// send command to panel
				out.print(getConfig);
				out.flush();

				/*
				 *   Receive bytes from panel and put in rxBuffer arrayList
				 */
				
				//Thread.sleep(1000);
				
				System.out.println("in.available()= "+ in.available());
				
				
				/*int count = 0;
				while (count == 0) {
					   count = in.available();
					  }
				rx = new byte[count];
				
				int readCount = 0; 
				while (readCount < count) {
					
				   readCount += in.read(rx, readCount, count - readCount);
				}

				*/
				
				int data = 0;
				
				
				while(!isClosed && !socket.isClosed())
				{	
					
					if(rxBuffer.size()>23 && rxBuffer.get(rxBuffer.size()-23)==0xAE)   // check finished bit; to be changed 
					{
						System.out.println(rxBuffer.get(rxBuffer.size()-23));
						delegate.receive(rxBuffer);
						rxBuffer.clear();
					}
					
					//keep listening while available until isClosed flag is set to true
					if(in.available()>0)
					{
						data = in.read();	
						rxBuffer.add(data);						
					}
					else
					{
						Thread.sleep(100);
					}				
				}
			
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
					ex.printStackTrace();
				}
			}		
		
		}
	
	};

}
