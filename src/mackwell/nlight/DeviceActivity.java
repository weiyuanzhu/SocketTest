package mackwell.nlight;

import mackwell.nlight.DeviceListFragment.OnDeviceFragmentInteractionListener;

import com.example.nclient.R;
import com.example.nclient.R.layout;
import com.example.nclient.R.menu;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class DeviceActivity extends Activity implements OnDeviceFragmentInteractionListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);
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
