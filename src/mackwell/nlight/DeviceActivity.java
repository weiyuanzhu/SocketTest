package mackwell.nlight;

import java.util.List;

import mackwell.nlight.DeviceListFragment.OnDevicdListFragmentListener;
import weiyuan.models.Panel;
import weiyuan.socket.Connection;
import weiyuan.socket.Connection.CallBack;
import weiyuan.util.ToggleCmdEnum;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import mackwell.nlight.DeviceListFragment;

import com.example.nclient.R;

public class DeviceActivity extends Activity implements OnDevicdListFragmentListener,CallBack{
	
	private Panel panel = null;
	private DeviceListFragment deviceListFragment = null;
	private DeviceInfoFragment deviceFragment = null;
	
	private ImageView image = null;
	private Connection connection;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		
		this.panel = intent.getParcelableExtra("panel");
		
		this.connection = new Connection(this,panel.getIp());
		
		this.image = (ImageView) findViewById(R.id.deviceInfo_image);
		
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
		
		image.setVisibility(View.INVISIBLE);
		System.out.println("groupPositon: " + groupPosition + " childPosition: " + childPosition);
		
		if(groupPosition==0)
		{
			deviceFragment = DeviceInfoFragment.newInstance(panel.getLoop1().getDevice(childPosition));
		}
		else {
			deviceFragment = DeviceInfoFragment.newInstance(panel.getLoop2().getDevice(childPosition));
			
			
		}
		
		
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		
		fragmentTransaction.replace(R.id.device_detail_container, deviceFragment,"device_detail_fragment");
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.commit();
		
	}
	
	private String getAppVersion(){
		StringBuilder version = new StringBuilder();
    	version.append("Mackwell N-Light Android, Version ");
    	String app_version = getString(R.string.app_version);
    	version.append(app_version);
		
    	return version.toString();
	}

	@Override
	public void receive(List<Integer> rx, String ip) {
		System.out.println(rx);
		connection.setIsClosed(true);
		
	}
	
	

	@Override
	public void ft(int address) {
		System.out.println("----------ftTest--------");
		 //commandList = CommandFactory.ftTest(device.getAddress());
		List<char[] > commandList = ToggleCmdEnum.FT.toggle(address);
		connection.fetchData(commandList);
		
	}

	@Override
	public void dt(int address) {
		System.out.println("----------ftTest--------");
		 //commandList = CommandFactory.ftTest(device.getAddress());
		List<char[] > commandList = ToggleCmdEnum.DT.toggle(address);
		connection.fetchData(commandList);
		
	}

	@Override
	public void st(int address) {
		System.out.println("----------ftTest--------");
		 //commandList = CommandFactory.ftTest(device.getAddress());
		List<char[] > commandList = ToggleCmdEnum.ST.toggle(address);
		connection.fetchData(commandList);
		
	}

	@Override
	public void id(int address) {
		System.out.println("----------ftTest--------");
		 //commandList = CommandFactory.ftTest(device.getAddress());
		List<char[] > commandList = ToggleCmdEnum.IDENTIFY.toggle(address);
		connection.fetchData(commandList);
		
	}

	
}
