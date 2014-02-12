package mackwell.nlight;

import mackwell.nlight.PanelListFragment.OnListItemClickedCallBack;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;

import com.example.nclient.R;

public class MainActivity extends Activity implements OnListItemClickedCallBack{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}



	@Override
	public void onListItemClicked(String ip, String location) {
		
		System.out.println(location + " " +  ip);
		
		PanelInfoFragment panelFragment = PanelInfoFragment.newInstance(ip, location);
		
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		
		fragmentTransaction.replace(R.id.panel_detail_container, panelFragment,"tagTest");
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.commit();
		
		
	}

}
