package com.mackwell.nlight.nlight;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.mackwell.nlight.R;
import com.mackwell.nlight.models.Device;
import com.mackwell.nlight.models.Panel;
import com.mackwell.nlight.nlight.DeviceListFragment.OnDevicdListFragmentListener;
import com.mackwell.nlight.nlight.InputDialogFragment.NoticeDialogListener;
import com.mackwell.nlight.socket.TCPConnection;
import com.mackwell.nlight.util.Constants;
import com.mackwell.nlight.util.DataParser;
import com.mackwell.nlight.util.GetCmdEnum;
import com.mackwell.nlight.util.SetCmdEnum;
import com.mackwell.nlight.util.ToggleCmdEnum;



public class DeviceActivity extends BaseActivity implements OnDevicdListFragmentListener,TCPConnection.CallBack, 
															NoticeDialogListener, SearchView.OnQueryTextListener,PopupMenu.OnMenuItemClickListener{
	
	//Comparators
	public static final Comparator<Device> SORT_BY_FAULTY = new Comparator<Device>(){
		@Override
		public int compare(Device lhs, Device rhs) {
			int lhsInt = (lhs.isFaulty()) ? 0 : 1;
			int rhsInt = (rhs.isFaulty()) ? 0 : 1;
			
			int faultComp = lhsInt - rhsInt;
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
	
	private boolean multiSelectionMode = false;

	
	private Handler mHandler = null;
	
	private Panel panel = null;
	private Device currentSelectedDevice = null;
	private DeviceListFragment deviceListFragment = null;
	private DeviceInfoFragment deviceInfoFragment = null;
	
	private ImageView imageView = null;
	private TextView messageTextView = null;
	
	private TCPConnection connection = null;
	
	private int currentDeviceAddress = 0;
	private int currentGroupPosition = 0;
	
	
	private SearchView searchView= null; //search view for search button on the action bar
	
	private SharedPreferences sharedPreferences = null;
	
	//Setters and Getters
	
		/**
		 * check if should autoRefresh 
		 * @return true if autoRefresh is checked in preference and device IS NOT in multi-selection mode
		 */
		public boolean isAutoRefresh() {
			
			autoRefresh = sharedPreferences.getBoolean("pref_key_refresh", false) && !multiSelectionMode;
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
		
		imageView.setVisibility(View.INVISIBLE);
		messageTextView.setVisibility(View.INVISIBLE);
		
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
	
	/* (non-Javadoc)when device list entering multi-selection mode
	 * @see nlight_android.nlight.DeviceListFragment.OnDevicdListFragmentListener#onMultiSelectionMode()
	 */
	@Override
	public void onMultiSelectionMode(boolean multiSelect) {
		multiSelectionMode = multiSelect;

		currentSelectedDevice = null;
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		if(deviceInfoFragment != null){
			fragmentTransaction.remove(deviceInfoFragment);
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			fragmentTransaction.commit();
		}
		
		imageView.setImageResource(R.drawable.mackwell_logo);
		imageView.setVisibility(View.VISIBLE);
		messageTextView.setVisibility(View.VISIBLE);
		messageTextView.setText(R.string.text_multi_selection_mode);
		
		
	}

	
	
	
	

	

	
	
	//Activity life circle

	


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
		String title = isDemo? "Panel: " + panel.getPanelLocation().trim() + getResources().getString(R.string.text_demo)
				: "Panel: " + panel.getPanelLocation().trim() + getResources().getString(R.string.text_live);
		getActionBar().setTitle(title);
		getActionBar().setSubtitle(R.string.subtitle_activity_device);
		
		if(!isDemo) this.connection = new TCPConnection(this,panel.getIp());
		
		this.imageView = (ImageView) findViewById(R.id.deviceInfo_image);
		this.messageTextView = (TextView) findViewById(R.id.device_faultyNo_text);
		messageTextView.setText(getResources().getString(R.string.text_panel_faulty_message, panel.getFaultDeviceNo()));
		
		
		if(panel.getOverAllStatus()!=0)
		{
			imageView.setImageResource(R.drawable.redcross);		
		}
		else imageView.setImageResource(R.drawable.greentick);
		
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
	public void ft(List<Integer> addressList) {
		
		 //commandList = CommandFactory.ftTest(device.getAddress());
		if(isConnected && !isDemo){
			System.out.println("----------ftTest--------");
			List<char[] > commandList = ToggleCmdEnum.FT.multiToggleTest(addressList);
			connection.fetchData(commandList);
			if(addressList.contains(64)|| addressList.contains(192)){
				
				mHandler.postDelayed(refreshAllDevices, 10000);
				
				
			}
		}
		
	}

	@Override
	public void dt(List<Integer> addressList) {
		
		 //commandList = CommandFactory.ftTest(device.getAddress());
		if(isConnected && !isDemo){
			System.out.println("----------ftTest--------");
			List<char[] > commandList = ToggleCmdEnum.DT.multiToggleTest(addressList);
			connection.fetchData(commandList);
			
			
		}
		
	}

	/* (non-Javadoc)
	 * @see nlight_android.nlight.DeviceListFragment.OnDevicdListFragmentListener#st(int)
	 */
	@Override
	public void st(List<Integer> addressList) {
		
		 //commandList = CommandFactory.ftTest(device.getAddress());
		if(isConnected && !isDemo){
			System.out.println("----------ftTest--------");
			List<char[] > commandList = ToggleCmdEnum.ST.multiToggleTest(addressList);
			connection.fetchData(commandList);
		}
		
	}

	@Override
	public void id(List<Integer> addressList) {
		
		 //commandList = CommandFactory.ftTest(device.getAddress());
		if(isConnected && !isDemo){
			System.out.println("----------ID--------");
			List<char[] > commandList = ToggleCmdEnum.IDENTIFY.multiToggleTest(addressList);
			connection.fetchData(commandList);
		}
		
	}

	@Override
	public void stopId(List<Integer> addressList) {
		
		 //commandList = CommandFactory.ftTest(device.getAddress());
		if(isConnected && !isDemo){
			System.out.println("--------stopId----------");
			List<char[] > commandList = ToggleCmdEnum.STOP_IDENTIFY.multiToggleTest(addressList);
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
		
		//set currentSelectedDevcict to null to prevent fragment error 
//		(getResources() can not be called when fragment is not attached)
		currentSelectedDevice = null;
		
		//remove DeviceInfo Fragment
		if(deviceInfoFragment != null){
			fragmentTransaction.remove(deviceInfoFragment);
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			fragmentTransaction.commit();
		}
		
		imageView.setVisibility(View.VISIBLE);
		messageTextView.setVisibility(View.VISIBLE);
		
		
		//reset image according to loop faulty status
		switch(groupPosition)
		{
			case 0: 
				messageTextView.setText(getResources().getString(R.string.text_loop_faulty_message,"Loop1",panel.getLoop1().getFaultyDevicesNo()));
				if(panel.getLoop1().getFaultyDevicesNo()!=0)
				{
					imageView.setImageResource(R.drawable.redcross);		
				}
				else imageView.setImageResource(R.drawable.greentick);
				break;
			case 1: 
				
				messageTextView.setText(getResources().getString(R.string.text_loop_faulty_message,"Loop2",panel.getLoop2().getFaultyDevicesNo()));
				if(panel.getLoop2().getFaultyDevicesNo()!=0)
					
				{
					imageView.setImageResource(R.drawable.redcross);		
				}
				else imageView.setImageResource(R.drawable.greentick);
				break;
				
			default: 
				break;
		
		
		}
		
	}
	
	@Override
	public void refreshSelectedDevices(List<Integer> addressList)
	{
		if(isConnected && !isDemo){
			System.out.println("----------refresh device status--------");
			List<char[] > commandList = ToggleCmdEnum.REFRESH.multiToggleTest(addressList);
			connection.fetchData(commandList);
		}
		else {
			
			mHandler.post(updateDeviceInfoFragmentUI);
		}
	
		
	}
	
	public void refreshsSingleDevice(int address)
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
	
	Runnable refreshAllDevices  = new Runnable(){

		@Override
		public void run() {
			if(!isDemo && connection!=null){
				List<char[] > commandList = GetCmdEnum.UPDATE_LIST.get();
			
				System.out.println(connection.isListening());
				if(connection.isListening())
				{
					connection.fetchData(commandList);
				}
			}
			
		}
		
	};
	
	
	Runnable autoRefreshCurrentDevice = new Runnable(){

		@Override
		public void run() {
			
			//System.out.println("---------------auto refresh current selected device----------------");
			//System.out.println("AutoRresh: " + isAutoRefreshSelectedDevice());
			
			if( isAutoRefresh() && currentSelectedDevice!=null && isAutoRefreshSelectedDevice())
			{
				refreshsSingleDevice(currentSelectedDevice.getAddress());
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
			//System.out.println("---------------auto refresh all devices----------------");
			//System.out.println("AutoRresh: " + isAutoRefreshAllDevices());
			
			
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
