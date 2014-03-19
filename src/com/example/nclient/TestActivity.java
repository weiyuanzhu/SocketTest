package com.example.nclient;

import java.util.ArrayList;
import java.util.List;

import weiyuan.socket.Connection;
import weiyuan.socket.Connection.CallBack;
import weiyuan.util.DataParser;
import weiyuan.util.SetCmdEnum;
import weiyuan.util.ToggleCmdEnum;
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
import android.view.View;

import mackwell.nlight.BaseActivity;
import messageType.EmergencyMode;
import messageType.EmergencyModeFlag;
import messageType.EmergencyStatus;
import messageType.EmergencyStatusFlag;
import messageType.FailureStatus;
import messageType.FailureStatusFlag;

public class TestActivity extends BaseActivity implements CallBack {

	Connection connection;
	final String ip = "192.168.1.24";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.activity_test);
		
		connection = new Connection(this, ip);
	}
	
	

	@Override
	protected void onStart() {
		super.onStart();
		
		
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
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
	
	public void messagTest(View v)
	{
		System.out.println(isConnected);
		checkConnectivity();
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		Boolean test = sharedPref.getBoolean("testPref", false);
		System.out.println("testPref--->" + test);

	}

	@Override
	public void receive(List<Integer> rx, String ip) {
		System.out.println(rx);
		connection.setIsClosed(true);
		
	}
	

	
}
