package nlight_android.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nlight_android.nlight.BaseActivity;
import nlight_android.nlight.SeekBarDialogFragment;
import nlight_android.nlight.SetDeviceLocationDialogFragment.NoticeDialogListener;
import nlight_android.nlight.SettingsActivity;
import nlight_android.socket.TCPConnection;
import nlight_android.util.GetCmdEnum;
import nlight_android.util.ToggleCmdEnum;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.nclient.R;

public class TestActivity extends BaseActivity implements TCPConnection.CallBack,NoticeDialogListener{

	TCPConnection tcpConnection;
	final String ip = "192.168.1.20";
	ExecutorService exec = null;
	
	@Override
	public void receive(List<Integer> rx, String ip) {
		System.out.println(rx);
		//tcpConnection.setListening(false);
		//refreshTest();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		preference();
		
		
		setContentView(R.layout.activity_test);
		
		tcpConnection = new TCPConnection(this, ip);
		exec = Executors.newSingleThreadExecutor();
	}
	
	

	@Override
	protected void onStart() {
		super.onStart();
		
		
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_devices, menu);
		return true;
	}

	
	
	public void tcpTest(View v)
	{
		/*List<Integer> buffer = new ArrayList<Integer>();
		buffer.add(00);		
		buffer.addAll(DataParser.convertString("test123"));
		System.out.println(buffer);
		List<char[] > commandList = SetCmdEnum.SET_DEVICE_NAME.set(buffer);
		
		commandList = ToggleCmdEnum.REFRESH.toggle(0);
		
		
		
		
		System.out.println("---------------" + Thread.currentThread().toString());
		
		
		for(int i=0; i<5;i++)
		{
			exec.execute(refreshTest);
			
		}*/
			refreshTest();
	}
		
	private void refreshTest(){

			tcpConnection.closeConnection();
			
			List<char[] > commandList = GetCmdEnum.GET_INIT.get();
			//commandList = ToggleCmdEnum.REFRESH.toggle(1);
			
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getTimeInstance();
			System.out.println("---------------" + Thread.currentThread().toString() + "Refreshed Time:  " + sdf.format(cal.getTime()));
			
			tcpConnection.fetchData(commandList);
			

		
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stu
		
		switch (item.getItemId()) {
       
        case R.id.action_settings:
        	Intent settingIntent = new Intent(this, SettingsActivity.class);
        	startActivity(settingIntent);
        	
            return true;
       
    
        default:
            return super.onOptionsItemSelected(item);
    }
	}



	public void messagTest(View v)
	{
		System.out.println(isConnected);
		checkConnectivity();
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		Boolean test = sharedPref.getBoolean("testPref", false);
		System.out.println("testPref--->" + test);

		SeekBarDialogFragment dialog = new SeekBarDialogFragment();
		dialog.show(getFragmentManager(), "SetLocation");
		
	}

	



	@Override
	public void setLocation(String location) {
		System.out.println("callback ------> " + location);
		
	}


	private void preference()
	{
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		//boolean save = sharedPref.
		
	}
	
}
