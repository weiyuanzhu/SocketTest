package nlight_android.nlight;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nlight_android.models.Device;
import nlight_android.models.Panel;
import nlight_android.nlight.DeviceListFragment.OnDevicdListFragmentListener;
import nlight_android.nlight.InputDialogFragment.NoticeDialogListener;
import nlight_android.socket.TCPConnection;
import nlight_android.util.Constants;
import nlight_android.util.DataParser;
import nlight_android.util.GetCmdEnum;
import nlight_android.util.SetCmdEnum;
import nlight_android.util.ToggleCmdEnum;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nclient.R;



public class DeviceActivity extends BaseActivity implements OnDevicdListFragmentListener,TCPConnection.CallBack, 
															NoticeDialogListener, SearchView.OnQueryTextListener,PopupMenu.OnMenuItemClickListener{
	
	//Comparators
	public static final Comparator<Device> SORT_BY_FAULTY = new Comparator<Device>(){
		@Override
		public int compare(Device lhs, Device rhs) {
			int faultComp = rhs.getFailureStatus() - lhs.getFailureStatus();
			return (faultComp == 0 ? (lhs.getAddress() - rhs.getAddress()): faultComp);
		}

	};	
	
	public static final Comparator<Device> SORT_BY_UNNAMED = new Comparator<Device>(){
		@Override
		public int compare(Device lhs, Device rhs) {
			int nameComp = lhs.getLocation().toLowerCase().compareTo(rhs.getLocation().toLowerCase());
			return (nameComp == 0 ? (lhs.getAddress() - rhs.getAddress()): nameComp);
		}

	};
	
	public static final Comparator<Device> SORT_BY_ALPHABET = new Comparator<Device>(){
		@Override
		public int compare(Device lhs, Device rhs) {
			
			if(lhs.getLocation().startsWith("?") && !rhs.getLocation().startsWith("?")){
				return 1;
				
			}
			else if(rhs.getLocation().startsWith("?") && !lhs.getLocation().startsWith("?")){
				return -1;
			}
			else{
				int nameComp = lhs.getLocation().toLowerCase().compareTo(rhs.getLocation().toLowerCase());
				return (nameComp == 0 ? (lhs.getAddress() - rhs.getAddress()): nameComp);
		
			}
		}

	};
	
	//Fields and Properties
	
	
	private boolean autoRefresh = false;
	private boolean autoRefreshAllDevices = false;
	private boolean autoRefreshSelectedDevice = false;

	
	private Handler mHandler = null;
	
	private Panel panel = null;
	private Device currentSelectedDevice = null;
	private DeviceListFragment deviceListFragment = null;
	private DeviceInfoFragment deviceInfoFragment = null;
	
	private ImageView image = null;
	private TextView faultyDeviceNo = null;
	
	private TCPConnection connection = null;
	
	private int currentDeviceAddress = 0;
	private int currentGroupPosition = 0;
	
	
	private SearchView searchView= null; //search view for search button on the action bar
	
	private SharedPreferences sharedPreferences = null;
	
	//Setters and Getters
	
		/**
		 * @return the autoRefresh
		 */
		public boolean isAutoRefresh() {
			
			autoRefresh = sharedPreferences.getBoolean("pref_key_refresh", false);
			return autoRefresh;
		}


		
		private String getAppVersion(){
			StringBuilder version = new StringBuilder();
	    	version.append("Mackwell N-Light Connect, Version ");
	    	String app_version = getString(R.string.app_version);
	    	version.append(app_version);
			
	    	return version.toString();
		}
		
		
		/**
		 * @return the autoRefreshAllDevices
		 */
		public boolean isAutoRefreshAllDevices() {
			autoRefreshAllDevices = sharedPreferences.getBoolean("pref_auto_refresh_all_devices", false);
			return autoRefreshAllDevices;
		}

		/**
		 * @return the autoRefreshSelectedDevice
		 */
		public boolean isAutoRefreshSelectedDevice() {
			autoRefreshSelectedDevice = sharedPreferences.getBoolean("pref_auto_refresh_selected_device", false);
			return autoRefreshSelectedDevice;
		}



	/* (non-Javadoc)connection.callback interface implementation
	 * @see nlight_android.nlight.BaseActivity#receive(java.util.List, java.lang.String)
	 */
	@Override
	public void receive(List<Integer> rx, String ip) {
		System.out.println(rx);
		if(rx.get(1)==160 && rx.get(2)==39){
			
			int address = rx.get(3);
			
			panel.updateDeviceByAddress(address, rx);
			/*if(currentSelectedDevice!=null){
				currentSelectedDevice.updateDevice(rx);
			}*/
			
			mHandler.post(updateDeviceInfoFragmentUI);
			//mHandler.post(new RefreshTest());
			
			//connection.setListening(true);
			mHandler.post(updateDeviceListFragment);
		}
		
	}
	
	
	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see nlight_android.nlight.InputDialogFragment.NoticeDialogListener#setInformation(java.lang.String)
	 */
	@Override
	public void setInformation(String input, int type) {
		
		//update current selected device
		currentSelectedDevice.setLocation(input);
		
		
		//update both device fragment and device list display
		deviceInfoFragment.updateLocation();
		deviceListFragment.updateLocation(currentGroupPosition,currentDeviceAddress, input);
		
		//send command to panel if not in demo mode
		
		if(!isDemo && connection != null){
			List<Integer> buffer = new ArrayList<Integer>();
		
		
			buffer.add(currentDeviceAddress);		
			buffer.addAll(DataParser.convertString(input));
			System.out.println(buffer);
			List<char[] > commandList = SetCmdEnum.SET_DEVICE_NAME.set(buffer);
			connection.fetchData(commandList);
		}
		
		//update devicelistFragment
		
		
		
		//show a toast
		Toast.makeText(this, "Device has been named.", Toast.LENGTH_LONG).show();
		
	}
	
	/* (non-Javadoc) implements DeviceListFragment listener, on single device clicked
	 * @see nlight_android.nlight.DeviceListFragment.OnDevicdListFragmentListener#onDeviceItemClicked(int, int)
	 */
	@Override
	public void onDeviceItemClicked(int groupPosition, int childPosition) {
		
		currentDeviceAddress = childPosition;
		currentGroupPosition = groupPosition;
		
		System.out.println("current device:-------------->" + currentDeviceAddress);
		
		image.setVisibility(View.INVISIBLE);
		faultyDeviceNo.setVisibility(View.INVISIBLE);
		
		System.out.println("groupPositon: " + groupPosition + " childPosition: " + childPosition);
		
		if(groupPosition==0)
		{
			currentSelectedDevice = panel.getLoop1().getDevice(childPosition);
			deviceInfoFragment = DeviceInfoFragment.newInstance(currentSelectedDevice, isAutoRefreshAllDevices());
		}
		else {
			currentSelectedDevice = panel.getLoop2().getDevice(childPosition);
			deviceInfoFragment = DeviceInfoFragment.newInstance(currentSelectedDevice,isAutoRefreshAllDevices());
		}
		
		
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		
		fragmentTransaction.replace(R.id.device_detail_container, deviceInfoFragment,"device_detail_fragment");
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.commit();
		
	}
	
	
	
	

	

	
	
	//Activity live circle

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);
		
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		mHandler = new Handler();
		
		checkConnectivity();
		Intent intent = getIntent();


		isDemo = intent.getBooleanExtra(LoadingScreenActivity.DEMO_MODE, true);
		System.out.println("Connecton: " + isConnected);
		System.out.println("Demo: "+ isDemo);
		
		//get panel from intent
		
		
		this.panel = intent.getParcelableExtra("panel");
		
		//set action bar
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		//set title with mode
		String title = isDemo? "Panel: " + panel.getPanelLocation().trim() + " (Demo)" : "Panel: " + panel.getPanelLocation().trim() + " (Live)";
		getActionBar().setTitle(title);
		getActionBar().setSubtitle("Device list");
		
		if(!isDemo) this.connection = new TCPConnection(this,panel.getIp());
		
		this.image = (ImageView) findViewById(R.id.deviceInfo_image);
		this.faultyDeviceNo = (TextView) findViewById(R.id.device_faultyNo_text);
		faultyDeviceNo.setText("Panel Fault(s): " + panel.getFaultDeviceNo());
		
		
		if(panel.getOverAllStatus()!=0)
		{
			image.setImageResource(R.drawable.redcross);		
		}
		else image.setImageResource(R.drawable.greentick);
		
		deviceListFragment = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.device_list_fragment);
		deviceListFragment.setLoop1(panel.getLoop1());
		deviceListFragment.setLoop2(panel.getLoop2());
		
		
		//start auto refresh
		mHandler.post(auroRefreshAllDevices);
		mHandler.postDelayed(autoRefreshCurrentDevice,TimeUnit.SECONDS.toMillis(1));
		
	}
	
	

	@Override
	protected void onNewIntent(Intent intent) {
		System.out.println("OnNewIntent");
		setIntent(intent);
		Intent newIntent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(newIntent.getAction())) {
	      String query = newIntent.getStringExtra(SearchManager.QUERY);
	      System.out.println(query);
	    }
		
		
		super.onNewIntent(intent);
		
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device, menu);
		
		
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    /*SearchView searchView = (SearchView) menu.findItem(R.id.action_search_device).getActionView();
	    
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName())); // add searchable.xml configure file to searchView
	    searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
	    searchView.setQueryHint("Search Device");
	    searchView.setOnQueryTextListener(this);*/
		
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		Intent intent = NavUtils.getParentActivityIntent(this);
	    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    		NavUtils.navigateUpTo(this, intent);
	    		
	    		
	    		
	    		return true;
	    	case R.id.action_about:
	        	Toast.makeText(this, getAppVersion(), Toast.LENGTH_SHORT).show();
	            return true;
	        case R.id.action_settings:
	        	Intent setting_intent = new Intent(this,SettingsActivity.class);
	            startActivity(setting_intent);
	            return true;
	            
	        case R.id.action_sort:
	        	View menuItemView = findViewById(R.id.action_sort);
	        	showDropDownMenu(menuItemView);
	        	//deviceListFragment.sort();
	        	
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * when drop down(popup) menu item clicked 
	 * @param popup menu item
	 * @return true for handled, false for default
	 */
	@Override
	public boolean onMenuItemClick(MenuItem arg0) {
		switch(arg0.getItemId()){
			case R.id.action_sort_by_faulty:
				deviceListFragment.sort(SORT_BY_FAULTY);
				System.out.println("---------Sort by faulty-------");
				return true;
			case R.id.action_sort_by_alphabet:
				
				deviceListFragment.sort(SORT_BY_ALPHABET);
				return true;
			case R.id.action_sort_by_unnamed:
				
				deviceListFragment.sort(SORT_BY_UNNAMED);
				return true;
			default: return false;
		}
	
	}
	
	//search callbacks
	@Override
	public boolean onQueryTextChange(String newText) {
		System.out.println(newText);
		deviceListFragment.search(newText);
		return false;
	}



	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	@Override
	protected void onDestroy() {
		if(connection!=null){
			connection.closeConnection();
			connection = null;
		}
		
		//stop all repeating tasks
		mHandler.removeCallbacks(auroRefreshAllDevices);
		mHandler.removeCallbacks(autoRefreshCurrentDevice);
		super.onDestroy();
	}

	
	
	
	
	


	
	

	@Override
	public void ft(int address) {
		
		 //commandList = CommandFactory.ftTest(device.getAddress());
		if(isConnected && !isDemo){
			System.out.println("----------ftTest--------");
			List<char[] > commandList = ToggleCmdEnum.FT.toggle(address);
			connection.fetchData(commandList);
		}
		
	}

	@Override
	public void dt(int address) {
		
		 //commandList = CommandFactory.ftTest(device.getAddress());
		if(isConnected && !isDemo){
			System.out.println("----------ftTest--------");
			List<char[] > commandList = ToggleCmdEnum.DT.toggle(address);
			connection.fetchData(commandList);
		}
		
	}

	/* (non-Javadoc)
	 * @see nlight_android.nlight.DeviceListFragment.OnDevicdListFragmentListener#st(int)
	 */
	@Override
	public void st(int address) {
		
		 //commandList = CommandFactory.ftTest(device.getAddress());
		if(isConnected && !isDemo){
			System.out.println("----------ftTest--------");
			List<char[] > commandList = ToggleCmdEnum.ST.toggle(address);
			connection.fetchData(commandList);
		}
		
	}

	@Override
	public void id(int address) {
		
		 //commandList = CommandFactory.ftTest(device.getAddress());
		if(isConnected && !isDemo){
			System.out.println("----------ID--------");
			List<char[] > commandList = ToggleCmdEnum.IDENTIFY.toggle(address);
			connection.fetchData(commandList);
		}
		
	}

	@Override
	public void stopId(int address) {
		
		 //commandList = CommandFactory.ftTest(device.getAddress());
		if(isConnected && !isDemo){
			System.out.println("--------stopId----------");
			List<char[] > commandList = ToggleCmdEnum.STOP_IDENTIFY.toggle(address);
			connection.fetchData(commandList);
		}
		
	}
	
	

	/* implements device list group open/close listener, 
	 * @see nlight_android.nlight.DeviceListFragment.OnDevicdListFragmentListener#onGroupExpandOrCollapse(int)
	 * @param int groupPosition that indicates which loop
	 */
	@Override
	public void onGroupExpandOrCollapse(int groupPosition) {
		System.out.println("GroupOpen/Close Test " + groupPosition);
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		
		//remove DeviceInfo Fragment
		if(deviceInfoFragment != null){
			fragmentTransaction.remove(deviceInfoFragment);
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			fragmentTransaction.commit();
		}
		
		image.setVisibility(View.VISIBLE);
		faultyDeviceNo.setVisibility(View.VISIBLE);
		
		
		//reset image according to loop faulty status
		switch(groupPosition)
		{
			case 0: 
				faultyDeviceNo.setText("Number of faulty device(s): " + panel.getLoop1().getFaultyDevicesNo());
				if(panel.getLoop1().getFaultyDevicesNo()!=0)
				{
					image.setImageResource(R.drawable.redcross);		
				}
				else image.setImageResource(R.drawable.greentick);
				break;
			case 1: 
				
				faultyDeviceNo.setText("Number of faulty device(s): " + panel.getLoop2().getFaultyDevicesNo());
				if(panel.getLoop2().getFaultyDevicesNo()!=0)
					
				{
					image.setImageResource(R.drawable.redcross);		
				}
				else image.setImageResource(R.drawable.greentick);
				break;
				
			default: 
				break;
		
		
		}
		
	}
	
	@Override
	public void refreshDevice(int address)
	{
		if(isConnected && !isDemo){
			System.out.println("----------refresh device status--------");
			List<char[] > commandList = ToggleCmdEnum.REFRESH.toggle(address);
			connection.fetchData(commandList);
		}
		else {
			
			mHandler.post(updateDeviceInfoFragmentUI);
		}
	
		
	}
	
	
	
	Runnable autoRefreshCurrentDevice = new Runnable(){

		@Override
		public void run() {
			
			System.out.println("---------------auto refresh current selected device----------------");
			System.out.println("AutoRresh: " + isAutoRefreshSelectedDevice());
			
			if( isAutoRefresh() && currentSelectedDevice!=null && isAutoRefreshSelectedDevice()){
				refreshDevice(currentSelectedDevice.getAddress());
			}
			else{
				mHandler.post(updateDeviceInfoFragmentUI);
			}
			mHandler.postDelayed(this, TimeUnit.SECONDS.toMillis(Constants.SELECTED_DEVICE_AUTO_REFRESH_FREQUENCY));
			
		}
		
		
	};
	
	Runnable auroRefreshAllDevices = new Runnable(){

		@Override
		public void run() {
			System.out.println("---------------auto refresh all devices----------------");
			System.out.println("AutoRresh: " + isAutoRefreshAllDevices());
			
			
			if( isAutoRefresh() && isAutoRefreshAllDevices()){
				refreshAllDevices();
			}
			//setup refresh frequency
			//int frequency= Integer.parseInt(getRefreshDuration());
			mHandler.postDelayed(this, TimeUnit.SECONDS.toMillis(Constants.ALL_DEVICES_AUTO_REFRESH_FREQUENCY));
			
			//get current time, using Calendar.getInstance();
			Calendar cal = Calendar.getInstance();
	    	cal.getTime();
	    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	    	System.out.println( "Last updated: " + sdf.format(cal.getTime()));
		
	    	//deviceFragment.updateStampTextView.setText("Last updated: " + sdf.format( cal.getTime()));
			//mHandler.postDelayed(new RefreshTest(), TimeUnit.SECONDS.toMillis(5));
		}
		
		
	};

	
	/**
	 * Send out update all device command GetCmdEnum.UpdateList if it is live
	 * and just update label if it is in demo
	 */
	private void refreshAllDevices(){
		
		
		
		if(!isDemo && connection!=null){
			List<char[] > commandList = GetCmdEnum.UPDATE_LIST.get();
		
			System.out.println(connection.isListening());
			if(connection.isListening())
			{
				connection.fetchData(commandList);
			}
		}
		else{
			mHandler.post(updateDeviceInfoFragmentUI);
		}
		
		
	}
	
	Runnable updateDeviceInfoFragmentUI = new Runnable()
	{

		@Override
		public void run() {
			
			if(currentSelectedDevice != null){
				deviceInfoFragment.updateDevice(currentSelectedDevice, isAutoRefresh());
			}
			
			
			//deviceListFragment.refershStatus();
			//deviceListFragment.updateProgressIcon(1);
		}
	
		
	};
	
	Runnable updateDeviceListFragment = new Runnable(){

		@Override
		public void run() {
			deviceListFragment.updateDeviceList();
		}
		
	};
	
	
	
	/**
	 * A function for popup a PopupMenu below menu item
	 * @param view menuItem
	 */
	public void showDropDownMenu(View view)
	{
		
		System.out.println("Panel Drop Down Menu");
		PopupMenu popup = new PopupMenu(this, view);
		popup.setOnMenuItemClickListener(this);
	    MenuInflater inflater = popup.getMenuInflater();
	    inflater.inflate(R.menu.sort_devicelist, popup.getMenu());
	    popup.show();
		
		
	}
	
	@Override
	public void seekBar() {
		SeekBarDialogFragment dialog = new SeekBarDialogFragment();
		dialog.show(getFragmentManager(), "SetLocation");	
	}

	

	

	

	

	
	
	
	
	
}
