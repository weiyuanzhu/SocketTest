package mackwell.nlight;

import mackwell.nlight.PanelListFragment.OnListItemClickedCallBack;
import android.app.Activity;
import android.net.Uri;
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
	public void onFragmentInteraction(Uri uri) {
		// TODO Auto-generated method stub
		
	}

}
