package com.example.nclient;

import java.util.ArrayList;
import java.util.List;

import weiyuan.socket.Connection;
import weiyuan.socket.Connection.CallBack;
import weiyuan.util.DataParser;
import weiyuan.util.SetCmdEnum;
import weiyuan.util.ToggleCmdEnum;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import messageType.EmergencyMode;
import messageType.EmergencyModeFlag;
import messageType.EmergencyStatus;
import messageType.EmergencyStatusFlag;
import messageType.FailureStatus;
import messageType.FailureStatusFlag;

public class TestActivity extends Activity implements CallBack {
	
	Connection connection;
	final String ip = "192.168.1.24";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		connection = new Connection(this, ip);
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
		StringBuilder sb = new StringBuilder();
		for(FailureStatus fs : new FailureStatusFlag().getFlagStatus(200))
		{
			
			sb.append(fs.getDescription()+",");
			
			
		}
		System.out.println(sb);
		
	}

	@Override
	public void receive(List<Integer> rx, String ip) {
		System.out.println(rx);
		connection.setIsClosed(true);
		
	}
	
}
