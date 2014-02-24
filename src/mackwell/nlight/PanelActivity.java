package mackwell.nlight;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mackwell.nlight.PanelListFragment.OnListItemClickedCallBack;
import weiyuan.models.Panel;
import weiyuan.socket.Connection;
import weiyuan.socket.Connection.CallBack;
import weiyuan.util.CommandFactory;
import weiyuan.util.DataParser;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.example.nclient.R;

public class PanelActivity extends Activity implements OnListItemClickedCallBack, CallBack{
	
	private Map<String,Panel> panelMap = null;
	private Map<String,Connection> panel_connection_map = null;
	private Map<String,List<Integer>> rxBufferMap = null;
	
	private List<PanelInfoFragment> fragmentList = null;
	
	
	private ImageView panelInfoImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		panelInfoImage = (ImageView) findViewById(R.id.panelInfo_image);
	
		fragmentList = new ArrayList<PanelInfoFragment>(5);

		initPanelMap();


		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}



	@Override
	protected void onDestroy() {
		
		for(String key : panelMap.keySet())
		{
			
			Connection connection = panel_connection_map.get(key);
			connection.closeConnection();
			connection = null;
		}
		
		super.onDestroy();
	}

	@Override
	public void onListItemClicked(String ip, String location, int index) {
		
		
		panelInfoImage.setVisibility(View.INVISIBLE);
		System.out.println(location + " " +  ip + "positon: " + index);
		
		if(panelMap.get(ip)==null)
		{
			panelMap.put(ip, new Panel(ip));
		}
		if(fragmentList.get(index) == null)
		{
			PanelInfoFragment panelFragment = PanelInfoFragment.newInstance(ip, location,panelMap.get(ip));
			fragmentList.set(index, panelFragment);
		}
		
		
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		
		fragmentTransaction.replace(R.id.panel_detail_container, fragmentList.get(index),"tagTest");
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.commit();
		
		
	}

	@Override
	public void getAllPanels() {
		System.out.println("getAllPanels");
		
		
		
		for(String key : panelMap.keySet()){
			
			Connection connection = new Connection(this, key);
			
			panel_connection_map.put(key, connection);
			
		}
		
		System.out.println(panel_connection_map);
		
		List<char[]> commandList = CommandFactory.getPanelInfo();
		
		for(String key : panelMap.keySet()){
			
			Connection conn = (Connection) panel_connection_map.get(key);
			conn.fetchData(commandList);
		}
		
	}

	@Override
	public void receive(List<Integer> rx, String ip) {
		List<Integer> rxBuffer = rxBufferMap.get(ip);
		rxBuffer.addAll(rx);
		Connection connection = panel_connection_map.get(ip);
		connection.setIsClosed(true);
		System.out.println(ip + " received package: " + connection.getPanelInfoPackageNo() + " rxBuffer size: " + rxBuffer.size());
		if(connection.isRxCompleted())
		{
			//connection.closeConnection();
			parse(ip);
			
		}
	}

	
	public void initPanelMap()

	{	
			panelMap = new HashMap<String,Panel>();
			panel_connection_map = new HashMap<String,Connection>();
			rxBufferMap = new HashMap<String,List<Integer>>();
			
			String ip1 = "192.168.1.17";
			panelMap.put(ip1, new Panel(ip1));
			rxBufferMap.put(ip1, new ArrayList<Integer>());
			fragmentList.add(null);
			
			String ip2 = "192.168.1.21";
			panelMap.put(ip2, new Panel(ip2));
			rxBufferMap.put(ip2, new ArrayList<Integer>());
			fragmentList.add(null);
			
			String ip3 = "192.168.1.20";
			panelMap.put(ip3, new Panel(ip3));
			rxBufferMap.put(ip3, new ArrayList<Integer>());
			fragmentList.add(null);
			
			String ip4 = "192.168.1.23";
			panelMap.put(ip4, new Panel(ip4));
			rxBufferMap.put(ip4, new ArrayList<Integer>());
			fragmentList.add(null);
		
			String ip5 = "192.168.1.24";
			panelMap.put(ip5, new Panel(ip5));
			rxBufferMap.put(ip5, new ArrayList<Integer>());
			fragmentList.add(null);
			
		
	}


	public void parse(String ip){
		
		List<Integer> rxBuffer = rxBufferMap.get(ip);
		
		List<List<Integer>> panelData = DataParser.removeJunkBytes(rxBuffer); 
		List<List<Integer>> eepRom = DataParser.getEepRom(panelData);	
		List<List<List<Integer>>> deviceList = DataParser.getDeviceList(panelData);
		
		
		try {
			Panel newPanel = new Panel(eepRom, deviceList, ip);
			panelMap.put(ip, newPanel);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

		

		
	
		
	}
}
