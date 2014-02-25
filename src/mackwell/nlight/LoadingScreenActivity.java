package mackwell.nlight;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weiyuan.models.Panel;
import weiyuan.socket.Connection;
import weiyuan.socket.Connection.CallBack;
import weiyuan.util.CommandFactory;
import weiyuan.util.DataParser;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.Menu;

import com.example.nclient.R;

public class LoadingScreenActivity extends Activity implements CallBack {
	
	private String[] ipList = null;
	private List<Panel> panelList = null;
	private Map<String,Panel> panelMap = null;
	private Map<String,Connection> ip_connection_map = null;
	private Map<String,List<Integer>> rxBufferMap = null;
	
	private static int delay = 3000;
	private Handler mHandler = null;
	
	
	
	/*
	 *	callback function for interface Connection.CallBack
	 */

	public void receive(List<Integer> rx, String ip) {
		List<Integer> rxBuffer = rxBufferMap.get(ip);
		rxBuffer.addAll(rx);
		Connection connection = ip_connection_map.get(ip);
		connection.setIsClosed(true);
		System.out.println(ip + " received package: " + connection.getPanelInfoPackageNo() + " rxBuffer size: " + rxBuffer.size());
		if(connection.isRxCompleted())
		{
			//connection.closeConnection();
			parse(ip);
			
		}
		
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading_screen);
		
		mHandler = new Handler();
		init();
		loadAllPanels();
		
		
	}
	
	

	@Override
	protected void onDestroy() {
		for(String key : ip_connection_map.keySet())
		{
			
			Connection connection = ip_connection_map.get(key);
			connection.closeConnection();
			connection = null;
		}
		super.onDestroy();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.loading_screen, menu);
		return true;
	}
	
	Runnable loadFinished = new Runnable()
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			Intent intent = new Intent(LoadingScreenActivity.this, PanelActivity.class);
			
			intent.putParcelableArrayListExtra("panelList", (ArrayList<? extends Parcelable>) panelList);
			startActivity(intent);
			
			finish();
			
		}
		
		
	};
	
	public void init()
	{
		
		/*
		 * Initial collections 
		 * 
		 */
		ipList = new String[] {"192.168.1.17","192.168.1.20","192.168.1.21","192.168.1.23","192.168.1.24"};
		panelList = new ArrayList<Panel>();
		panelMap = new HashMap<String,Panel>();
		ip_connection_map = new HashMap<String,Connection>();
		rxBufferMap = new HashMap<String,List<Integer>>();
		
		
		/*
		 *  Initial connections and rxBuffer for each panel
		 */
		

		for(int i =0; i<ipList.length; i++)
		{
			Connection connection = new Connection(this, ipList[i]);
			ip_connection_map.put(ipList[i], connection);
			rxBufferMap.put(ipList[i], new ArrayList<Integer>());
			
		}


	}

	
	
	public void loadAllPanels()
	{
		List<char[]> commandList = CommandFactory.getPanelInfo();
		
		for(String key : ip_connection_map.keySet()){
			
			Connection conn = (Connection) ip_connection_map.get(key);
			conn.fetchData(commandList);
		}
		
		
	}
	
	public void parse(String ip)
	{
		List<Integer> rxBuffer = rxBufferMap.get(ip);
		
		List<List<Integer>> panelData = DataParser.removeJunkBytes(rxBuffer); 
		List<List<Integer>> eepRom = DataParser.getEepRom(panelData);	
		List<List<List<Integer>>> deviceList = DataParser.getDeviceList(panelData);
		
		
		try {
			Panel newPanel = new Panel(eepRom, deviceList, ip);
			panelMap.put(ip, newPanel);
			panelList.add(newPanel);
			
			if(panelList.size()==5){
				mHandler.post(loadFinished);	
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
