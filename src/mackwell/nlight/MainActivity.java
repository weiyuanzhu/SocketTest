package mackwell.nlight;

import java.util.ArrayList;
import java.util.List;

import mackwell.nlight.PanelListFragment.OnListItemClickedCallBack;
import weiyuan.models.Panel;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.example.nclient.R;

public class MainActivity extends Activity implements OnListItemClickedCallBack{
	
	private List<Panel> panelList = null;
	
	private List<PanelInfoFragment> fragmentList = null;
	
	private ImageView panelInfoImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		panelInfoImage = (ImageView) findViewById(R.id.panelInfo_image);
		
		panelList = new ArrayList<Panel>(5);
		
		fragmentList = new ArrayList<PanelInfoFragment>(5);
		for(int i=0; i<5; i++)
		{
			panelList.add(null);
			fragmentList.add(null);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}



	@Override
	public void onListItemClicked(String ip, String location, int index) {
		
		panelInfoImage.setVisibility(View.INVISIBLE);
		System.out.println(location + " " +  ip + "positon: " + index);
		
		if(panelList.get(index)==null)
		{
			panelList.set(index, new Panel());
		}
		if(fragmentList.get(index) == null)
		{
			PanelInfoFragment panelFragment = PanelInfoFragment.newInstance(ip, location,panelList.get(index));
			fragmentList.set(index, panelFragment);
		}
		
		
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		
		fragmentTransaction.replace(R.id.panel_detail_container, fragmentList.get(index),"tagTest");
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.commit();
		
		
	}

}
