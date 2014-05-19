package nlight_android.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class UDPConnection implements Runnable{

	private List<int[]> panelList;
	
	private static final int SERVER_PORT = 1460;
	private static final int LISTEN_PORT = 5001;
	
	private DatagramSocket udpSocket = null;
	private DatagramPacket udpPacket = null; 
	private boolean isListen = true;
	
	private String msg;
	
	public UDPConnection(String msg)
	{
		super();
		panelList = new ArrayList<int[]>();
		this.msg = msg;
	}
	

	public void run()
	{
		try {
			InetAddress address = InetAddress.getByName("255.255.255.255");
			
			
			if(udpSocket==null){
				udpSocket = new DatagramSocket(LISTEN_PORT);
			}
			
			int msg_len = msg == null? 0 : msg.length();
			
			udpPacket = new DatagramPacket(msg.getBytes(),msg_len,address,SERVER_PORT);
			
			
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			udpSocket.send(udpPacket);
			Runnable receive= new Runnable()
			{

				@Override
				public void run() {

					System.out.println("---------------receiving udp packages------------");
					byte[] buf = new byte[1024];
					udpPacket = new DatagramPacket(buf, buf.length);
					while(isListen)
					{
						try {
							udpSocket.receive(udpPacket);
							int[] buffer = new int[buf.length];
							int i = 0;
							for(byte b : udpPacket.getData()) {
								int a = b & 0xFF;
								buffer[i] = a;
								
								i++;
							}
							
							/*for(int j =0; j<buffer.length;j++)
							{
								System.out.print(buffer[j] + " ");
								
							}*/
							
							panelList.add(buffer);
							
							
					
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}



			};
			Thread t = new Thread(receive);
			t.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

	
	public List<String> getIpList(){
		List<String> panelIpList = new ArrayList<String>();
		
		for(int i=0; i<panelList.size();i++)
		{
			StringBuilder sb = new StringBuilder();
			for(int j=11; j<15; j++)
			{
				sb.append(panelList.get(i)[j]);
				sb.append(".");
			}
			sb.deleteCharAt(sb.length()-1);
			panelIpList.add(sb.toString());
		}
		
		
		return panelIpList;
		
	}
	
	public boolean isListen() {
		return isListen;
	}

	public void setListen(boolean isListen) {
		this.isListen = isListen;
	}
	
	public List<int[]> getPanelList() {
		return panelList;
	}
	
}
