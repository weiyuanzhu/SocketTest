package nlight_android.nlight;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nlight_android.models.Panel;
import nlight_android.nlight.PanelListFragment.OnPanelListItemClickedCallBack;
import nlight_android.socket.TCPConnection;
import nlight_android.util.CommandFactory;
import nlight_android.util.DataParser;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.nclient.R;

/**
 * @author weiyuan zhu
 *
 */
public class PanelActivity extends BaseActivity implements OnPanelListItemClickedCallBack, TCPConnection.CallBack, PopupMenu.OnMenuItemClickListener{
	
	private List<Panel> panelList = null;
	private Map<String,Panel> panelMap = null;
	private Map<String,TCPConnection> panel_connection_map = null;
	private Map<String,List<Integer>> rxBufferMap = null;
	
	private List<PanelInfoFragment> fragmentList = null;
	
	private Panel currentDisplayingPanel;
	private Panel panelWithFaulyDevices;
	private ImageView panelInfoImage;

	private PanelListFragment panelListFragment;
	
	//private int currentSelected;

	
	//call back for connection
	@Override
	public void receive(List<Integer> rx, String ip) {
		List<Integer> rxBuffer = rxBufferMap.get(ip);
		rxBuffer.addAll(rx);
		TCPConnection connection = panel_connection_map.get(ip);
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
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		//set title with demo
		String title = isDemo? "N-Light Connect (Demo)" :  "N-Light Connect (Live)";
		getActionBar().setTitle(title);
		
		System.out.println("DeomoMode--------> " + isDemo);
		
		
		//check panelList
		if(panelList!=null){
			panelListFragment.setPanelList(panelList);
		
			fragmentList = new ArrayList<PanelInfoFragment>(panelList.size());

			System.out.println("All panel get: " + panelList.size());
			
			//init
			initPanelMap();
		}
		else 
		{
			//if panelList exist, init FragmentList and pass panelList to PanelListFragment
			fragmentList = new ArrayList<PanelInfoFragment>(panelList.size());
			
			initPanel();
			panelListFragment.setPanelList(panelList);
		}
		
		savePanelToPreference();
		//panelListFragment.refreshStatus(isDemo, isConnected);

		
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
	            Intent intent = new Intent(this,SettingsActivity.class);
	            startActivity(intent);
	            return true;
	        case R.id.action_refresh:
	        	Toast.makeText(this, "Refreshing panel status...", Toast.LENGTH_LONG).show();
	        	panelListFragment.refreshStatus(isDemo, isConnected);
	        	return true;
	    
	        case R.id.action_show_devices:
	        	View menuItemView = findViewById(R.id.action_show_devices);
	        	showDropDownMenu(menuItemView);
	        	return true;
	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	/* (non-Javadoc) callback for pupupMenu items
	 * @see android.widget.PopupMenu.OnMenuItemClickListener#onMenuItemClick(android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch(item.getItemId())
		{
			case R.id.action_show_loops:
				System.out.println("Show Loops");
				if(currentDisplayingPanel != null){
					showDevices(currentDisplayingPanel);
				}
				return true;
			case R.id.action_show_faulty_devices:
				System.out.println("Show Faulty Devices");
				if(currentDisplayingPanel != null){
					panelWithFaulyDevices = Panel.getPanelWithFaulty(currentDisplayingPanel);
					showDevices(panelWithFaulyDevices);
				}
				return true;
        	
			default:
	            return false;
		
		}
	}



	

	@Override
	protected void onDestroy() {
		
		if(panelMap!=null && panel_connection_map!=null){
			for(String key : panelMap.keySet())
			{
				
				TCPConnection connection = panel_connection_map.get(key);
				if(connection!=null){
					connection.closeConnection();
					connection = null;
				}
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
			
			TCPConnection connection = new TCPConnection(this, key);
			
			panel_connection_map.put(key, connection);
			
		}
		
		System.out.println(panel_connection_map);
		
		List<char[]> commandList = CommandFactory.getPanelInfo();
		
		for(String key : panelMap.keySet()){
			
			TCPConnection conn = (TCPConnection) panel_connection_map.get(key);
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
		panel_connection_map = new HashMap<String,TCPConnection>();
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
    	version.append("Mackwell N-Light Connect, Version ");
    	String app_version = getString(R.string.app_version);
    	version.append(app_version);
		
    	return version.toString();
	}
	
	private void showDevices(Panel panel){
		System.out.println("Get Device List");
		
		Intent intent = new Intent(this, DeviceActivity.class);

		if(panel!=null){
			
			intent.putExtra("location", panel.getPanelLocation());
			intent.putExtra("panel", panel);
			intent.putExtra("loop1",panel.getLoop1());
			intent.putExtra("loop2",panel.getLoop2());
			intent.putExtra(LoadingScreenActivity.DEMO_MODE, isDemo);
			startActivity(intent);
			
		}
		
	}

	private void savePanelToPreference()
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isSave = sp.getBoolean(SettingsActivity.SAVE_PANEL_LOCATION, true);
		if(isSave)
		{
			for(Panel panel : panelList)
			{
				SharedPreferences.Editor editor = sp.edit();
				editor.putString(panel.getIp(), panel.getPanelLocation());
				editor.commit();
			}
			
		};
		
		
		
	}
	
	public void showDropDownMenu(View view)
	{
		
		System.out.println("Drop Down Menu");
		PopupMenu popup = new PopupMenu(this, view);
		popup.setOnMenuItemClickListener(this);
	    MenuInflater inflater = popup.getMenuInflater();
	    inflater.inflate(R.menu.show_devices, popup.getMenu());
	    popup.show();
		
		
	}
		
}
