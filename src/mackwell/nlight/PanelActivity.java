package mackwell.nlight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mackwell.nlight.PanelListFragment.OnListItemClickedCallBack;
import weiyuan.models.Panel;
import weiyuan.socket.Connection;
import weiyuan.socket.Connection.CallBack;
import weiyuan.util.CommandFactory;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.example.nclient.R;

public class PanelActivity extends Activity implements OnListItemClickedCallBack, CallBack{
	
	private List<Panel> panelList = null;
	private Map<String,Connection> panel_connection_map = null;
	private Map<String,List<Integer>> rxBufferMap = null;
	
	private List<PanelInfoFragment> fragmentList = null;
	
	
	private ImageView panelInfoImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		panelInfoImage = (ImageView) findViewById(R.id.panelInfo_image);
		
		panelList = new ArrayList<Panel>(5);
		
		fragmentList = new ArrayList<PanelInfoFragment>(5);

		initRxBufferMap();
		initPanelList();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}



	@Override
	protected void onDestroy() {
		
		for(Panel p : panelList)
		{
			String ip = p.getIp();
			Connection connection = panel_connection_map.get(ip);
			connection.closeConnection();
		}
		
		super.onDestroy();
	}

	@Override
	public void onListItemClicked(String ip, String location, int index) {
		
		
		panelInfoImage.setVisibility(View.INVISIBLE);
		System.out.println(location + " " +  ip + "positon: " + index);
		
		if(panelList.get(index)==null)
		{
			panelList.set(index, new Panel(ip));
		}
		if(fragmentList.get(index) == null)
		{
			PanelInfoFragment panelFragment = PanelInfoFragment.newInstance(ip, location,panelList.get(index));
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
		
		
		
		for(Panel p : panelList){
			
			Connection connection = new Connection(this, p.getIp());
			
			panel_connection_map.put(p.getIp(), connection);
			
		}
		
		System.out.println(panel_connection_map);
		
		List<char[]> commandList = CommandFactory.getPanelInfo();
		
		for(Panel p : panelList){
			String ip = p.getIp();
			Connection conn = (Connection) panel_connection_map.get(ip);
			conn.fetchData(commandList);
		}
		
	}

	@Override
	public void receive(List<Integer> rx, String ip) {
		List<Integer> rxBuffer = rxBufferMap.get(ip);
		rxBuffer.addAll(rx);
		Connection connection = panel_connection_map.get(ip);
		connection.setIsClosed(true);
		System.out.println(ip + " received package: " + connection.getPanelInfoPackageNo() + "rxBuffer size: " + rxBuffer.size());
	}

	
	public void initPanelList()

	{
			panel_connection_map = new HashMap<String,Connection>();
			
			String ip1 = "192.168.1.17";
			panelList.add(new Panel(ip1));
			fragmentList.add(null);
			
			String ip2 = "192.168.1.21";
			panelList.add(new Panel(ip2));
			fragmentList.add(null);
			
			String ip3 = "192.168.1.20";
			panelList.add(new Panel(ip3));
			fragmentList.add(null);
			
			String ip4 = "192.168.1.23";
			panelList.add(new Panel(ip4));
			fragmentList.add(null);
		
			String ip5 = "192.168.1.24";
			panelList.add(new Panel(ip5));
			fragmentList.add(null);
		
	}

	public void initRxBufferMap()
	{
		rxBufferMap = new HashMap<String,List<Integer>>();
		
		String ip1 = "192.168.1.17";
		rxBufferMap.put(ip1, new ArrayList<Integer>());
		
		String ip2 = "192.168.1.21";
		rxBufferMap.put(ip2, new ArrayList<Integer>());
		
		String ip3 = "192.168.1.20";
		rxBufferMap.put(ip3, new ArrayList<Integer>());
		
		String ip4 = "192.168.1.23";
		rxBufferMap.put(ip4, new ArrayList<Integer>());
		
		String ip5 = "192.168.1.24";
		rxBufferMap.put(ip5, new ArrayList<Integer>());
		
		
		
	}
}
