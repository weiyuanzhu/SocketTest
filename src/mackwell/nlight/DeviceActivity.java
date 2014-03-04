package mackwell.nlight;

import java.util.List;

import mackwell.nlight.DeviceListFragment.OnDeviceFragmentInteractionListener;
import weiyuan.models.Panel;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;

import com.example.nclient.R;

public class DeviceActivity extends Activity implements OnDeviceFragmentInteractionListener{
	
	private Panel panel = null;
	private DeviceListFragment deviceListFragment = null;
	private List<DeviceInfoFragment> fragmentList = null;
	
	

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
	public void onFragmentInteraction(Uri uri) {
		// TODO Auto-generated method stub
		
	}

}
