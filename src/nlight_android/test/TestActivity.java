package nlight_android.test;

import java.util.ArrayList;
import java.util.List;

import nlight_android.util.ToggleCmdEnum;
import nlight_android.messageType.EmergencyMode;
import nlight_android.messageType.EmergencyModeFlag;
import nlight_android.messageType.EmergencyStatus;
import nlight_android.messageType.EmergencyStatusFlag;
import nlight_android.messageType.FailureStatus;
import nlight_android.messageType.FailureStatusFlag;
import nlight_android.nlight.BaseActivity;
import nlight_android.nlight.SeekBarDialogFragment;
import nlight_android.nlight.SetDeviceLocationDialogFragment;
import nlight_android.nlight.SettingsActivity;
import nlight_android.nlight.SettingsFragment;
import nlight_android.nlight.SetDeviceLocationDialogFragment.NoticeDialogListener;
import nlight_android.socket.TCPConnection;
import nlight_android.socket.TCPConnection.CallBack;
import nlight_android.util.DataParser;
import nlight_android.util.SetCmdEnum;

import com.example.nclient.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import nlight_android.nlight.*;
import nlight_android.nlight.SetDeviceLocationDialogFragment.*;
import nlight_android.socket.TCPConnection.*;
import nlight_android.socket.*;
import android.os.*;
import android.view.*;

import java.util.*;

public class TestActivity extends BaseActivity implements TCPConnection.CallBack,NoticeDialogListener{

	TCPConnection connection;
	final String ip = "192.168.1.24";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		preference();
		
		
		setContentView(R.layout.activity_test);
		
		connection = new TCPConnection(this, ip);
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

	
	
	public void test(View v)
	{
		List<Integer> buffer = new ArrayList<Integer>();
		buffer.add(00);		
		buffer.addAll(DataParser.convertString("test123"));
		System.out.println(buffer);
		List<char[] > commandList = SetCmdEnum.SET_DEVICE_NAME.set(buffer);
		connection.fetchData(commandList);
		
		
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
	public void receive(List<Integer> rx, String ip) {
		System.out.println(rx);
		connection.setIsClosed(true);
		
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
