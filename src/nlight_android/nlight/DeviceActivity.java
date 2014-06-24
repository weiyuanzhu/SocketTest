package nlight_android.nlight;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nlight_android.models.Device;
import nlight_android.models.Panel;
import nlight_android.nlight.DeviceInfoFragment.DeviceSetLocationListener;
import nlight_android.nlight.DeviceListFragment.OnDevicdListFragmentListener;
import nlight_android.nlight.SetDeviceLocationDialogFragment.NoticeDialogListener;
import nlight_android.socket.TCPConnection;
import nlight_android.util.DataParser;
import nlight_android.util.SetCmdEnum;
import nlight_android.util.ToggleCmdEnum;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nclient.R;



public class DeviceActivity extends BaseActivity implements OnDevicdListFragmentListener,TCPConnection.CallBack, 
															DeviceSetLocationListener,NoticeDialogListener, SearchView.OnQueryTextListener{
	
	
	
	private boolean isAutoFresh = true;
	private Handler mHandler;
	
	private Panel panel = null;
	private Device currentSelectedDevice;
	private DeviceListFragment deviceListFragment = null;
	private DeviceInfoFragment deviceFragment = null;
	
	private ImageView image = null;
	private TextView faultyDeviceNo;
	
	private TCPConnection connection;
	
	private int currentDeviceAddress = 0;
	private int currentGroupPosition = 0;
	
	
	private SearchView searchView= null; //search view for search button on the action bar
	
	//connection.callback interface implementation
	@Override
	public void receive(List<Integer> rx, String ip) {
		System.out.println(rx);
		if(rx.get(1)==160){
			currentSelectedDevice.updateDevice(rx);
			mHandler.post(refreshDevice);
			//mHandler.post(new RefreshTest());
			connection.setListening(true);
			//deviceListFragment.updateProgressIcon(0);
		}
	}
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);
		
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
		
		this.connection = new TCPConnection(this,panel.getIp());
		
		this.image = (ImageView) findViewById(R.id.deviceInfo_image);
		this.faultyDeviceNo = (TextView) findViewById(R.id.device_faultyNo_text);
		faultyDeviceNo.setText("Panel Faulty Number: " + panel.getFaultDeviceNo());
		
		
		if(panel.getOverAllStatus()!=0)
		{
			image.setImageResource(R.drawable.redcross);		
		}
		else image.setImageResource(R.drawable.greentick);
		
		deviceListFragment = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.device_list_fragment);
		deviceListFragment.setLoop1(panel.getLoop1());
		deviceListFragment.setLoop2(panel.getLoop2());
		
		
		//start auto refresh
		//mHandler.postDelayed(new RefreshTest(),1000);
		
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
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search_device).getActionView();
	    
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName())); // add searchable.xml configure file to searchView
	    searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
	    searchView.setQueryHint("Search Device");
	    searchView.setOnQueryTextListener(this);
		
		
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
	        	deviceListFragment.sort();
	        	
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
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
			deviceFragment = DeviceInfoFragment.newInstance(currentSelectedDevice);
		}
		else {
			currentSelectedDevice = panel.getLoop2().getDevice(childPosition);
			deviceFragment = DeviceInfoFragment.newInstance(currentSelectedDevice);
		}
		
		
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		
		fragmentTransaction.replace(R.id.device_detail_container, deviceFragment,"device_detail_fragment");
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.commit();
		
	}
	
	
	
	
	@Override
	protected void onDestroy() {
		connection.closeConnection();
		connection = null;
		super.onDestroy();
	}

	private String getAppVersion(){
		StringBuilder version = new StringBuilder();
    	version.append("Mackwell N-Light Connect, Version ");
    	String app_version = getString(R.string.app_version);
    	version.append(app_version);
		
    	return version.toString();
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
	
	@Override
	public void refreshDevice(int address)
	{
		if(isConnected && !isDemo){
			System.out.println("----------refresh device status--------");
			List<char[] > commandList = ToggleCmdEnum.REFRESH.toggle(address);
			connection.fetchData(commandList);
		}
		else {
			
			mHandler.post(refreshDevice);
		}
		
		
		
	}
	
	@Override
	public void seekBar() {
		SeekBarDialogFragment dialog = new SeekBarDialogFragment();
		dialog.show(getFragmentManager(), "SetLocation");	
	}
	
	@Override
	public void setDeviceLocation(String location) {
		//display dialog
		
		SetDeviceLocationDialogFragment dialog = new SetDeviceLocationDialogFragment();
		dialog.show(getFragmentManager(), "setDeviceLocationDialog");
		
	}

	@Override
	public void setLocation(String location) {
		//update device fragment
		deviceFragment.updateLocation(location);
		
		//send command to panel
		
		List<Integer> buffer = new ArrayList<Integer>();
		
		buffer.add(currentDeviceAddress);		
		buffer.addAll(DataParser.convertString(location));
		System.out.println(buffer);
		List<char[] > commandList = SetCmdEnum.SET_DEVICE_NAME.set(buffer);
		connection.fetchData(commandList);
		
		//update devicelistFragment
		deviceListFragment.updateLocation(currentGroupPosition,currentDeviceAddress, location);
		
		
		//show a toast
		Toast.makeText(this, "Device has been named.", Toast.LENGTH_LONG).show();
		
	}


	
	Runnable refreshDevice = new Runnable()
	{

		@Override
		public void run() {
			
			deviceFragment.updateDevice(currentSelectedDevice);
			deviceListFragment.refershStatus();
			deviceListFragment.updateProgressIcon(1);
		}
	
		
	};

	/* implements device list group open/close listener, 
	 * @see nlight_android.nlight.DeviceListFragment.OnDevicdListFragmentListener#onGroupExpandOrCollapse(int)
	 * @param int groupPosition that indicates which loop
	 */
	@Override
	public void onGroupExpandOrCollapse(int groupPosition) {
		System.out.println("GroupOpen/Close Test " + groupPosition);
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		
		//remove DeviceInfo Fragment
		if(deviceFragment != null){
			fragmentTransaction.remove(deviceFragment);
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			fragmentTransaction.commit();
		}
		
		image.setVisibility(View.VISIBLE);
		faultyDeviceNo.setVisibility(View.VISIBLE);
		
		
		//reset image according to loop faulty status
		switch(groupPosition)
		{
			case 0: 
				faultyDeviceNo.setText("Loop1 Faulty Number: " + panel.getLoop1().getFaultyDevicesNo());
				if(panel.getLoop1().getFaultyDevicesNo()!=0)
				{
					image.setImageResource(R.drawable.redcross);		
				}
				else image.setImageResource(R.drawable.greentick);
				break;
			case 1: 
				
				faultyDeviceNo.setText("Loop2 Faulty Number: " + panel.getLoop2().getFaultyDevicesNo());
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
	
	private class RefreshTest implements Runnable{

		@Override
		public void run() {
			System.out.println("---------------auto refresh test----------------");
			if(isAutoFresh){
				
				
					refreshDevice(0);
				
			}
			
			
			mHandler.postDelayed(new RefreshTest(), TimeUnit.SECONDS.toMillis(30));
			//get current time, using Calendar.getInstance();
			Calendar cal = Calendar.getInstance();
	    	cal.getTime();
	    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	    	System.out.println( "Last updated: " + sdf.format(cal.getTime()));
		
	    	//deviceFragment.updateStampTextView.setText("Last updated: " + sdf.format( cal.getTime()));
			//mHandler.postDelayed(new RefreshTest(), TimeUnit.SECONDS.toMillis(5));
		}
		
		
	}

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
	
}
