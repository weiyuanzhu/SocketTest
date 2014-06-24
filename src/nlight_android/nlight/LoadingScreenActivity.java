package nlight_android.nlight;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nlight_android.models.Device;
import nlight_android.models.Panel;
import nlight_android.socket.TCPConnection;
import nlight_android.socket.PanelConnection;
import nlight_android.socket.UDPConnection;
import nlight_android.socket.UDPConnection.UDPCallback;
import nlight_android.util.CommandFactory;
import nlight_android.util.Constants;
import nlight_android.util.DataParser;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nclient.R;

/**
 * @author  weiyuan zhu15/04/2014 Starting develop branch test on develop branch test 2 on feature branch test 3 on feature branch after rebase
 */

public class LoadingScreenActivity extends BaseActivity implements PanelConnection.CallBack,ListDialogFragment.ListDialogListener, UDPCallback{
	
	public static final String DEMO_MODE = "Demo Mode";
	
	//ipListAll = new String[] {"192.168.1.17","192.168.1.20","192.168.1.21","192.168.1.23","192.168.1.24"};
	private ArrayList<String> ipListAll = null;
	private ArrayList<String> ipListSelected = null;
	
	private static final int LOADING = 0;
	private static final int PARSING = 1;
	private static final int LOADING_FINISHED = 2;
	private static final int ERROR = 3;
	
	private boolean isLoading = false;
	
	private Button liveBtn = null;
	private Button demoBtn = null;
	private TextView progressText = null;
	private ProgressBar progressBar = null;
	private int progress = 0;
	
	private List<Panel> panelList = null;
	private Map<String,Panel> panelMap = null;
	private Map<String,PanelConnection> ip_connection_map = null;
	private Map<String,List<Integer>> rxBufferMap = null;
	
	private UDPConnection udpConnection = null;
	private PanelConnection panelConnection = null;
	List<Map<String, Object>> dataList = null; // datalist for panel list dialog
	
	private static int delay = 1000;
	private Handler mHandler = null;
	
	private int panelToLoad = 0; 
	
	

	/* (non-Javadoc) implement TCPcallback, receiving data package
	 * @see mackwell.nlight.BaseActivity#receive(java.util.List, java.lang.String)
	 */
	public void receive(List<Integer> rx, String ip) {
		
		Message msg = mHandler.obtainMessage();
		progress++;
		
		msg.arg1 = LOADING;
		msg.arg2 = progress;
		
		
		
		List<Integer> rxBuffer = rxBufferMap.get(ip);
		rxBuffer.addAll(rx);
		PanelConnection connection = ip_connection_map.get(ip);
		connection.setListening(false);
		System.out.println(ip + " received package: " + connection.getPanelInfoPackageNo() + " rxBuffer size: " + rxBuffer.size());
		if(connection.isRxCompleted())
		{
			panelToLoad--;

			//update progress with handler
			
			
			if(panelToLoad==0){
				msg.arg1 = PARSING;
				
			}
			
			
			//mHandler.sendMessage(msg);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			parse(ip);
			
		}
		
		mHandler.sendMessage(msg);
		
	}

	/* (non-Javadoc) implementing UDPcallback
	 * @see nlight_android.socket.UDPConnection.UDPCallback#addIp(java.lang.String)
	 */
	public int addIp(String ip)
	{
		System.out.println("Received UDP package");
		if(!ipListAll.contains(ip))
		{
			ipListAll.add(ip);
			
			//put ip and location into a map and add to dataList for dialog listview;
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("ip", ip);
			map.put("location",getPanelLocationFromPreference(ip));
			dataList.add(map);
			
			return 0;
		}
		return 1;
	}
	
	/**
	 * This function takes an ip for the panel and return it's location in the SharedPreference 
	 * @param ip IP address for the panel
	 * @return location for the panel, return "" if not save
	 */
	private String getPanelLocationFromPreference(String ip)
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		boolean savePanelLocation = sp.getBoolean(SettingsActivity.SAVE_PANEL_LOCATION, true);
		
		if(savePanelLocation)
		{
			String panelLocation = sp.getString(ip, "");
			
			return panelLocation;
		}
		
		return "";
	}
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading_screen);
		
		searchUDP();
		
		//init all view items
		liveBtn = (Button) findViewById(R.id.loadscreen_live_imageBtn);
		demoBtn = (Button) findViewById(R.id.loadscreen_demo_imageBtn);
		progressText = (TextView) findViewById(R.id.loadscreen_progress_textView);
		progressBar = (ProgressBar) findViewById(R.id.loadscreen_progressBar);
		
		//init loading panals 
		initializeFields();
		
		//update connection flags
		checkConnectivity();
		
		//handler for deal with UI update and navigation
		mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				
				progressBar.setProgress(msg.arg2);
				
				switch(msg.arg1){
					case LOADING:
						progressText.setText("Loading Panel Data " + " (" + panelToLoad + ")");
						break;
					case PARSING: 
						progressText.setText("Analysing Panel Data");
						break;
					case LOADING_FINISHED:
						progressText.setText("Finish Loading");
						break;
					case ERROR:
						String ipAdd = (String) msg.obj;
						progressText.setText("Cannot connect to " + ipAdd + ", please check connection.");
						progressBar.setVisibility(View.INVISIBLE);
						liveBtn.setEnabled(true);
						break;
					default: 
						
						break;
					
					
				
				}
				
				super.handleMessage(msg);
			}
			
			
		};
		
		
		
		/*
		//create a new udpConnection instance, if it exist, then close previous udp connnection
		if(udpConnection == null ){
			udpConnection = new UDPConnection(Constants.FIND_PANELS, this);
		}
		else
		{
			udpConnection.closeConnection();
			udpConnection = new UDPConnection(Constants.FIND_PANELS,this);
			
		}
		
		//send UDP panel search messages
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(udpConnection);
		exec.shutdown();
		*/
		
	}
	
	//when activity moves to background
	protected void onStop()
	{
		super.onStop();
		
		//close UDP connection
		if(udpConnection!=null)
		{
			udpConnection.setListen(false);
			udpConnection.closeConnection();
		}
		
		//close TCP connections
		for(String key : ip_connection_map.keySet())
		{
			
			PanelConnection connection = ip_connection_map.get(key);
			connection.closeConnection();
			connection = null;
		}
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//reset progress bar and progress
		progress = 0;
		progressBar.setVisibility(View.INVISIBLE);
		
		//re-enable buttons
		demoBtn.setEnabled(true);
		liveBtn.setEnabled(true);
		
		//hide bars
		progressText.setVisibility(View.INVISIBLE);
		//progressBar.setVisibility(View.INVISIBLE);
		
	}


	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		
		//close UDP connection
		if(udpConnection!=null)
		{
			udpConnection.setListen(false);
			udpConnection.closeConnection();
		}
		
		//close TCP connections
		for(String key : ip_connection_map.keySet())
		{
			
			PanelConnection connection = ip_connection_map.get(key);
			connection.closeConnection();
			connection = null;
		}
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.loading_screen, menu);
		return true;
	}
	

	//pass all panel objects to next activity when loading and parsing is finished
	
	Runnable loadFinished = new Runnable()
	{

		@Override
		public void run() {
			
			isLoading = false;
			
			
			
			//create intent
			Intent intent = new Intent(LoadingScreenActivity.this, PanelActivity.class);
			
			//put panelList into intent
			intent.putParcelableArrayListExtra("panelList", (ArrayList<? extends Parcelable>) panelList);
			intent.putExtra(DEMO_MODE, isDemo);
			startActivity(intent);
			
			
			
			//clear ipSelected list 
			ipListSelected.clear();
			
			
			//finish this activity to prevent back navi
			//finish();
			
		}
		
		
	};
	
	public void initializeFields()
	{
		
		/*
		 * Initial collections 
		 * 
		 */
		ipListAll = new ArrayList<String>();
		ipListSelected = new ArrayList<String>();
		
		
		//paneList(Parcelable) is for navigation
		panelList = new ArrayList<Panel>();
		
		panelMap = new HashMap<String,Panel>();
		ip_connection_map = new HashMap<String,PanelConnection>();
		rxBufferMap = new HashMap<String,List<Integer>>();
		
		dataList = new ArrayList<Map<String,Object>>();
		
		//ipList.add("192.168.1.17");
		


	}
	
	public void demoMode(View view){
		
		//set isDemo flag
		demoBtn.setEnabled(false);
		
		isDemo = true;
		
		progressText.setText("Preparing Panel Data");
		progressText.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);

		prepareDataForDemo();
		
		mHandler.postDelayed(loadFinished, delay);
		
	}
	
	
	public void liveMode(View view)
	{
		if(ipListAll.size()==0){
			
			searchUDP();
		}
		popDialog();
		
		
	}
	
	public void parse(String ip)
	{
		
		
		Message msg = mHandler.obtainMessage();
		//msg.arg1 = PARSING;
		//mHandler.sendMessage(msg);
		
		List<Integer> rxBuffer = rxBufferMap.get(ip);
		
		List<List<Integer>> panelData = DataParser.removeJunkBytes(rxBuffer); 
		List<List<Integer>> eepRom = DataParser.getEepRom(panelData);	
		List<List<List<Integer>>> deviceList = DataParser.getDeviceList(panelData,eepRom);
		
		
		try {
			Panel newPanel = new Panel(eepRom, deviceList, ip);
			panelMap.put(ip, newPanel);
			panelList.add(newPanel);
			
			if(panelList.size()==ipListSelected.size()){
				
				//msg = new Message();
				msg.arg1 = LOADING_FINISHED;
				mHandler.sendMessage(msg);

				mHandler.postDelayed(loadFinished, delay);	
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void prepareDataForDemo()
	{
		panelList = new ArrayList<Panel>();
	
		Panel panel = new Panel("192.168.1.18");
		panel.setPanelLocation("Mackwell L&B 1");
		panel.setSerialNumber((long)1376880756);
		panel.setGtinArray(new int[]{131,1,166,43,154,4});

		panel.getLoop1().addDevice(new Device(0,"LB 1",0,0,0,254,1375167879,new int[]{11,1,166,43,154,4}));
		panel.getLoop1().addDevice(new Device(1,"LB 2",0,0,0,200,1374967295,new int[]{45,2,166,43,154,4}));
		panel.getLoop2().addDevice(new Device(128,"LB 4",0,0,0,150,1374467255,new int[]{78,3,166,43,154,4}));
		panel.getLoop2().addDevice(new Device(129,"LB 4",0,0,0,150,1374537221,new int[]{130,4,166,43,154,4}));
		
		
		panelList.add(panel);
	
		panel = new Panel("192.168.1.19");
		panel.setPanelLocation("Mackwell L&B 2");
		panel.setSerialNumber((long)1375868516);
		panel.setGtinArray(new int[]{132,2,166,43,154,4});
		
		panel.getLoop1().addDevice(new Device(0,"LB 5",72,48,0,0,1365167879,new int[]{145,5,166,43,154,4}));
		panel.getLoop1().addDevice(new Device(1,"LB 6",0,0,0,200,1366965291,new int[]{178,5,166,43,154,4}));
		panel.getLoop2().addDevice(new Device(128,"LB 7",0,0,0,150,1361562293,new int[]{223,3,166,43,154,4}));
		panel.getLoop2().addDevice(new Device(129,"LB 8",0,0,0,150,1363967292,new int[]{243,4,166,43,154,4}));

		panelList.add(panel);
		
		
	}


	@Override
	public void error(String ip) {
		// TODO Auto-generated method stub
		super.error(ip);
		System.out.println("Error: " + ip);
		ip_connection_map.get(ip).closeConnection();
		
		Message msg = mHandler.obtainMessage();
		msg.arg1 = ERROR;
		msg.obj = ip;
		mHandler.sendMessage(msg);
		
		ipListAll.remove(ip);
		panelToLoad	= ipListAll.size();	
	}
	
	
	
	/* (non-Javadoc) implementing ListDialogFragment's ListDialogListener interface
	 * @see nlight_android.nlight.ListDialogFragment.ListDialogListener#cancelDialog(java.util.List)
	 */
	@Override
	public void cancelDialog(List<Integer> selected) {
		
		savePanelSelectionToIpLIstSelected(selected);	
		saveCheckedStatsToPreference();
		
	}

	// 
	/* (non-Javadoc) implementing ListDialogFragment's ListDialogListener interface
	 * @see nlight_android.nlight.ListDialogFragment.ListDialogListener#connectPanels(java.util.List)
	 */
	@Override
	public void connectPanels(List<Integer> selected) {
		
		//connecting to panels, and close UDP socket
		udpConnection.closeConnection();
		
		//save checkBox status
		
		savePanelSelectionToIpLIstSelected(selected);
		
		progressBar.setMax(16*ipListSelected.size());
		
		
		System.out.println(ipListSelected);
		
		
		
		
		/*
		 *  Initial connections and rxBuffer for each panel
		 */
		
		for(String ip : ipListSelected)
		{
			PanelConnection connection = new PanelConnection(this, ip);
			ip_connection_map.put(ip, connection);
			rxBufferMap.put(ip, new ArrayList<Integer>());
			
		}
		//on main thread
		//show progress bar and text
		
		//set isDemo flag
		isDemo = false;
		panelToLoad = ipListSelected.size();
		
		
		//Message msg = mHandler.obtainMessage();
		//msg.arg1 = LOADING;
		//mHandler.sendMessage(msg);

		
		
		//check if loading is already in process and panel selected not equal to 0
		if(!isLoading && ipListSelected.size()!=0){
			progressText.setText("Loading Panel Data " + " (" + panelToLoad + ")");
			
			progressText.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.VISIBLE);
			
			
			System.out.println("------------liveMode clicked");
			List<char[]> commandList = CommandFactory.getPanelInfo();
			
			for(String ip: ipListSelected){
				
				PanelConnection conn = (PanelConnection) ip_connection_map.get(ip);
				conn.fetchData(commandList);
			}
			
			
			//set button disable
			liveBtn.setEnabled(false);
			isLoading = true;
			
		}
		
		saveCheckedStatsToPreference();
		
	}
	
	
	public void popDialog()
	{
	
		
		/*Map<String, Object> map = new HashMap<String,Object>();
		map.put("ip", "192.168.1.20");
		map.put("location","Mackwell R&D");
		dataList.add(map);
		
		map = new HashMap<String,Object>();
		map.put("ip", "192.168.1.24");
		map.put("location","Mackwell Special");
		dataList.add(map);*/
		
		
		
		//create a new ListDialogFragment and set its String[] ips to be udp search result
		
		ListDialogFragment panelListDialog = new ListDialogFragment();
				
		//get a String[] from ipSet and pass to dialog window
		String[] ipArray = new String[ipListAll.size()];
		ipListAll.toArray(ipArray);
		panelListDialog.setIps(ipArray);
		panelListDialog.setDataList(dataList);
				
		//test.setIps(null); //null test
		panelListDialog.show(getFragmentManager(), "test"); //popup dialog
				
		
	}
	
	/**
	 * This is when search button clicked on loading screen
	 * @param view
	 */
	public void searchPanelsBtn(View view)
	{
		
		searchUDP();
	}

	private void searchUDP(){
		if(udpConnection == null ){
			udpConnection = new UDPConnection(Constants.FIND_PANELS, this);
		}
		else
		{
			udpConnection.closeConnection();
			udpConnection = new UDPConnection(Constants.FIND_PANELS,this);
			
		}
		
		//send UDP panel search messages
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(udpConnection);
		exec.shutdown();
		
	}
	
	/**
	 * save checked status to SharedPreference
	 */
	private void saveCheckedStatsToPreference()
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		boolean saveChecked = sp.getBoolean(SettingsActivity.SAVE_CHECKED, true);
		SharedPreferences.Editor editor = sp.edit();
		
		if(saveChecked)
		{
			System.out.println("Save checked");
			
			for(String ip : ipListAll)
			{
				StringBuilder sb = new StringBuilder(ip);
				sb.append(" ");
				String ip_ = sb.toString();
				
				System.out.println(ip_);
				editor.putBoolean(ip_, ipListSelected.contains(ip)? true: false);
				editor.commit();
			
				
			}
		
		}
		
		// clear selected IP list 
		//ipSelected.clear();
	}

	/**
	 * Go thought all IP list and put selected ip in the ipSelected set 
	 * @param selected list of position that panels selected in the multi selection dialog
	 */
	private void savePanelSelectionToIpLIstSelected(List<Integer> selected)
	{
		for(Integer i: selected)
		{
			String item = ipListAll.get(i);
			if(!ipListSelected.contains(item)){
				ipListSelected.add(item);
			}
		}
	}

}
