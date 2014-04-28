package nlight_android.nlight;

import java.util.ArrayList;
import java.util.List;

import nlight_android.models.Device;
import nlight_android.models.Panel;
import nlight_android.nlight.DeviceInfoFragment.DeviceSetLocationListener;
import nlight_android.nlight.DeviceListFragment.OnDevicdListFragmentListener;
import nlight_android.nlight.SetDeviceLocationDialogFragment.NoticeDialogListener;
import nlight_android.socket.Connection;
import nlight_android.util.DataParser;
import nlight_android.util.SetCmdEnum;
import nlight_android.util.ToggleCmdEnum;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nclient.R;

public class DeviceActivity extends BaseActivity implements OnDevicdListFragmentListener,Connection.CallBack, 
															DeviceSetLocationListener,NoticeDialogListener{
	private Handler mHandler;
	
	private Panel panel = null;
	private Device currentSelectedDevice;
	private DeviceListFragment deviceListFragment = null;
	private DeviceInfoFragment deviceFragment = null;
	
	private ImageView image = null;
	private Connection connection;
	
	private int currentDeviceAddress;
	private int currentGroupPosition;
	
	//connection.callback interface implementation
	@Override
	public void receive(List<Integer> rx, String ip) {
		System.out.println(rx);
		currentSelectedDevice.updateDevice(rx);
		mHandler.post(refreshDevice);
		connection.setIsClosed(true);
		
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
		
		//set title with demo
		String title = isDemo? "Panel: " + panel.getPanelLocation() + " (Demo)" : "Panel: " + panel.getPanelLocation() + " (Live)";
		getActionBar().setTitle(title);
		
		this.connection = new Connection(this,panel.getIp());
		
		this.image = (ImageView) findViewById(R.id.deviceInfo_image);
		
		if(panel.getOverAllStatus()!=0)
		{
			image.setImageResource(R.drawable.redcross);		
		}else image.setImageResource(R.drawable.greentick);
		
		deviceListFragment = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.device_list_fragment);
		deviceListFragment.setLoop1(panel.getLoop1());
		deviceListFragment.setLoop2(panel.getLoop2());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device, menu);
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
	            
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onDeviceItemClicked(int groupPosition, int childPosition) {
		
		currentDeviceAddress = childPosition;
		currentGroupPosition = groupPosition;
		
		System.out.println("current device:-------------->" + currentDeviceAddress);
		
		image.setVisibility(View.INVISIBLE);
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
	
	public void refreshDevice(int address)
	{
		if(isConnected && !isDemo){
			System.out.println("----------refresh device status--------");
			List<char[] > commandList = ToggleCmdEnum.REFRESH.toggle(address);
			connection.fetchData(commandList);
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
		}
	
		
		
	};
	


	
}
