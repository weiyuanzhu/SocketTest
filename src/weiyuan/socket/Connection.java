package weiyuan.socket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

public class Connection {
	
	//interface for callback
		public interface CallBack 
		{
				void receive(List<Integer> rx,String ip);
		}
	
	static final int UART_STOP_BIT_H = 0x5A;
	static final int UART_STOP_BIT_L = 0xA5;
	static final int UART_NEW_LINE_H = 0x0D;
	static final int UART_NEW_LINE_L = 0x0A;
	
	private int panelInfoPackageNo;
	private boolean rxCompleted;
	
	
	
	
	//private fields
	private WeakReference<CallBack> mCallBack;
	
	private int port;
	private String ip;
	
	private boolean isClosed; // a flag for stop the background listening 
	
	private Socket socket;
	
	private List<Integer> rxBuffer; //buffer for receive data
	

	private List<char[]> commandList;

	private PrintWriter out;
	private InputStream in; 
	

	
	
	//Constructor , requires a delegation object for callback
	public Connection(CallBack callBack, String ip)
	{
		this.ip = ip;
		this.isClosed = false;
		this.rxCompleted = false;
		this.rxBuffer = new ArrayList<Integer>();
		this.port = 500;
		this.mCallBack = new WeakReference<CallBack>(callBack);
		
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
	 
	

	
	public void fetchData(List<char[]> commandList){
		
		this.commandList = commandList;
		new Thread(fetch).start();
		System.out.println("connection started ");
		
		
	}

	public void closeConnection()
	{
		isClosed = true;
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
	
	
	/*	runnable for thread
	 *	thread will keep listening on socket.inputstream until received data is complete
	 *	and then calls its delegate to 
	 * 
	*/
	Runnable fetch = new Runnable(){

		@Override
		public void run() {
			panelInfoPackageNo = 0;
			System.out.println("Slaver is doing job on a new thread.");
			
			//char[] getPackageTest = new char[] {2, 165, 64, 15, 96, 0,0x5A,0xA5,0x0D,0x0A};
			//char[] getConfig = new char[] {0x02,0xA0,0x21,0x68,0x18,0x5A,0xA5,0x0D,0x0A};
			
			for(int i=0; i<commandList.size(); i++){
				
				char[] command = (char[]) commandList.get(i);
			
				try {
					// init socket and in/out stream
					
					isClosed = false;
					
					if(socket == null ||  socket.isClosed())
					{
						socket = new Socket(ip,port);	
						socket.setSoTimeout(500);
						socket.setReceiveBufferSize(20000);
						out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"ISO8859_1")),false);
						in = socket.getInputStream();
						
						System.out.println("\nConnected to: " + socket.getInetAddress() + ": "+  socket.getPort());
					}
					

					// send command to panel
					out.print(command);
					out.flush();
	
					/*
					 *   Receive bytes from panel and put in rxBuffer arrayList
					 */
			
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
						
						if(in.available()==0 && !rxBuffer.isEmpty() && (data == UART_NEW_LINE_L) && 
		        				rxBuffer.get(rxBuffer.size() - 2).equals(UART_NEW_LINE_H) &&
		        				rxBuffer.get(rxBuffer.size() - 3).equals(UART_STOP_BIT_L) &&
		        				rxBuffer.get(rxBuffer.size() - 4).equals(UART_STOP_BIT_H))   // check finished bit; to be changed 
						{
							//System.out.println(rxBuffer.get(rxBuffer.size()-23));
							
							//mCallback.get() to get mCallBack instance, for it is  weakReference
							
							panelInfoPackageNo ++ ;
							
							if(panelInfoPackageNo == commandList.size()){
								System.out.println(" Recieve task completed");
								rxCompleted = true;
							}
							
							mCallBack.get().receive(rxBuffer,ip);
							rxBuffer.clear();
						}
						
						
						if(in.available()>0)
						{
							data = in.read();
							rxBuffer.add(data);
		
						}
						
						//keep listening while available until isClosed flag is set to true
						
						else
						{
							Thread.sleep(0,100);
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
					
					
					if(panelInfoPackageNo == commandList.size()){
						System.out.println(" Recieve task completed");
						rxCompleted = true;
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
		
			}
		}
	
	};

	public String getIp() {
		return ip;
	}


	public int getPanelInfoPackageNo() {
		return panelInfoPackageNo;
	}


	public boolean isRxCompleted() {
		return rxCompleted;
	}






	
	

}
