package mackwell.nlight;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mackwell.nlight.PanelListFragment.OnPanelListItemClickedCallBack;
import weiyuan.models.Panel;
import weiyuan.socket.Connection;
import weiyuan.socket.Connection.CallBack;
import weiyuan.util.CommandFactory;
import weiyuan.util.DataParser;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nclient.R;

/**
 * @author weiyuan zhu
 *
 */
public class PanelActivity extends BaseActivity implements OnPanelListItemClickedCallBack, Connection.CallBack{
	
	private List<Panel> panelList = null;
	private Map<String,Panel> panelMap = null;
	private Map<String,Connection> panel_connection_map = null;
	private Map<String,List<Integer>> rxBufferMap = null;
	
	private List<PanelInfoFragment> fragmentList = null;
	
	private Panel currentDisplayingPanel;
	private ImageView panelInfoImage;
	private PanelListFragment panelListFragment;
	
	private int currentSelected;
	
	//call back for connection
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
	
	//activity life circle

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_panel);
		
		panelInfoImage = (ImageView) findViewById(R.id.panelInfo_image);
		panelListFragment = (PanelListFragment) getFragmentManager().findFragmentById(R.id.fragment_panel_list);
	
		//update connection flags
		checkConnectivity();
		
		Intent intent = getIntent();
		
		panelList = intent.getParcelableArrayListExtra("panelList");
		isDemo = intent.getBooleanExtra(LoadingScreenActivity.DEMO_MODE, true);
		
		System.out.println("DeomoMode--------> " + isDemo);
		
		if(panelList!=null){
			panelListFragment.setPanelList(panelList);
		
			fragmentList = new ArrayList<PanelInfoFragment>(panelList.size());

			System.out.println("All panel get: " + panelList.size());
			initPanelMap();
		}
		else 
		{
			fragmentList = new ArrayList<PanelInfoFragment>(5);
			initPanel();
			panelListFragment.setPanelList(panelList);
		}
		
		
		panelListFragment.refreshStatus(isDemo, isConnected);

		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_about:
	        	
	        	Toast.makeText(this, getAppVersion(), Toast.LENGTH_SHORT).show();
	        	
	            return true;
	        case R.id.action_settings:
	            
	            return true;
	        case R.id.action_refresh:
	        	panelListFragment.refreshStatus(isDemo, isConnected);
	        	return true;
	        case R.id.action_show_device_list:
	        	if(currentDisplayingPanel != null){
	        		showDevices();
	        	}
	        	
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}



	@Override
	protected void onDestroy() {
		
		for(String key : panelMap.keySet())
		{
			
			Connection connection = panel_connection_map.get(key);
			if(connection!=null){
				connection.closeConnection();
				connection = null;
			}
		}
		
		super.onDestroy();
	}

	@Override
	public void onListItemClicked(String ip, String location, int index) {
		
		currentDisplayingPanel = panelMap.get(ip);
		
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
		//fragmentTransaction.addToBackStack(null);  add fragment to backstack
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.commit();
		
		
	}


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

	

	
	public void initPanelMap()
	{	
		panelMap = new HashMap<String,Panel>();
		
		for(int i=0; i<panelList.size();i++)
		{
			panelMap.put(panelList.get(i).getIp(), panelList.get(i));	
			PanelInfoFragment panelFragment = PanelInfoFragment.newInstance(panelList.get(i).getIp(), panelList.get(i).getPanelLocation(),panelList.get(i));
			fragmentList.add(panelFragment);
			
		}
			
			
	}


	public void parse(String ip){

		
		List<Integer> rxBuffer = rxBufferMap.get(ip);
		
		List<List<Integer>> panelData = DataParser.removeJunkBytes(rxBuffer); 
		List<List<Integer>> eepRom = DataParser.getEepRom(panelData);	
		List<List<List<Integer>>> deviceList = DataParser.getDeviceList(panelData,eepRom);
		
		
		try {
			Panel newPanel = new Panel(eepRom, deviceList, ip);
			panelMap.put(ip, newPanel);
		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}

	@Override
	public void passTest() {
		int temp = 0;
		
		for(String key:panelMap.keySet())
		{
			panelList.set(temp, panelMap.get(key));
			temp++;
			
		}
		
		System.out.println("Pass Panel Test");
		
		Intent intent = new Intent(this,PanelInfoActivity.class);
		intent.putParcelableArrayListExtra("panelList", (ArrayList<? extends Parcelable>) panelList);
		
		startActivity(intent);
	}
	
	private void initPanel()
	{
		panelList = new ArrayList<Panel>();
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
		
		for(String k: panelMap.keySet())
		{
			panelList.add(panelMap.get(k));
			
		}
		
		
	}
	
	private String getAppVersion(){
		StringBuilder version = new StringBuilder();
    	version.append("Mackwell N-Light Android, Version ");
    	String app_version = getString(R.string.app_version);
    	version.append(app_version);
		
    	return version.toString();
	}
	
	private void showDevices(){
		System.out.println("Get Device List");
		
		Intent intent = new Intent(this, DeviceActivity.class);

		if(currentDisplayingPanel!=null){
			
			intent.putExtra("location", currentDisplayingPanel.getPanelLocation());
			intent.putExtra("panel", currentDisplayingPanel);
			intent.putExtra("loop1",currentDisplayingPanel.getLoop1());
			intent.putExtra("loop2",currentDisplayingPanel.getLoop2());
			startActivity(intent);
			
		}
		
	}
}
