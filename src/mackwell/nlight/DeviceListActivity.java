package mackwell.nlight;

import weiyuan.models.Loop;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.nclient.R;

public class DeviceListActivity extends ListActivity {
	
	
	//private List<Loop> loops;
	
	private String[] deviceList;
	
	private ArrayAdapter<String> arrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_list);
		
		Intent intent = getIntent();
		
		Loop loop = (Loop) intent.getSerializableExtra("loop");  
		
		deviceList = new String[] {"Device 1 ","Device 2","Device 3"};
		arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2,android.R.id.text1, deviceList);
		
		setListAdapter(arrayAdapter);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device_list, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		//System.out.println(deviceList[position]);
		
		Intent intent = new Intent(this, DeviceInfoActivity.class);
		
		intent.putExtra("device", deviceList[position]);
		
		startActivity(intent);
	}
	
	

}
