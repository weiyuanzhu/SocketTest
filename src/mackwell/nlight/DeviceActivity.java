package mackwell.nlight;

import java.util.List;

import mackwell.nlight.DeviceListFragment.OnDevicdListFragmentListener;
import weiyuan.models.Panel;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import mackwell.nlight.DeviceListFragment;

import com.example.nclient.R;

public class DeviceActivity extends Activity implements OnDevicdListFragmentListener{
	
	private Panel panel = null;
	private DeviceListFragment deviceListFragment = null;
	private DeviceInfoFragment deviceFragment = null;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);
		
		Intent intent = getIntent();
		
		this.panel = intent.getParcelableExtra("panel");
		
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

	@Override
	public void onDeviceItemClicked(int groupPosition, int childPosition) {
		
		System.out.println("groupPositon: " + groupPosition + " childPosition: " + childPosition);
		
		if(groupPosition==0)
		{
			deviceFragment = DeviceInfoFragment.newInstance(panel.getLoop1().getDevice(childPosition));
		}
		else deviceFragment = DeviceInfoFragment.newInstance(panel.getLoop2().getDevice(childPosition));
		
		
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		
		fragmentTransaction.replace(R.id.device_detail_container, deviceFragment,"device_detail_fragment");
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.commit();
		
	}

	
}
