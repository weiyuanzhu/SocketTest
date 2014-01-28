package mackwell.nlight;

import java.util.ArrayList;
import java.util.List;

import weiyuan.socket.Connection;
import weiyuan.util.DataParser;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.example.nclient.R;

public class PanelInfo extends ListActivity  implements Connection.Delegation{

	
	private List<Integer> rxBuffer = null;  //raw data pull from panel
	
	private List<List<Integer>> panelData = null;  //all panel data (removed junk bytes)
	private List<List<Integer>> eepRom = null;		//panel eeprom data
	private List<List<Integer>> deviceList = null;	//device list
	
	private Connection connection = null;	
	
	


	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_panel_info);
		
		//setup ListView adapter
		
	
		//create connector
		rxBuffer = new ArrayList<Integer>();
		connection = new Connection(this);
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.panel_info, menu);
		return true;
	}

	
	//function for delegate interface
	@Override
	public void receive(List<Integer> rx) {
		
		rxBuffer = new ArrayList<Integer>(rx);

		connection.setIsClosed(true);
				
		if(this.rxBuffer.size()< 16500)
		{		
			System.out.println("not complete " + this.rxBuffer.size());
			connection.fetchData();			
		}
		else {
			//rxComplete = true;
			//new Thread(parse).start();
		}
		

		System.out.println("Actual bytes received: " + rxBuffer.size());
		
		
		
	}
	
	public void parse(View v)
	{
		
		panelData = DataParser.removeJunkBytes(rxBuffer);
		eepRom = DataParser.getEepRom(panelData);
		
		System.out.println("================EEPROM========================");
		
		for(int i=0; i<eepRom.size();i++)
		{
			System.out.println("eepRom "+ i + ": \n" + eepRom.get(i));
		}
		
		System.out.println("================Device List========================");
		deviceList = DataParser.getDeviceList(panelData);
		
		
		for(int i=0; i<deviceList.size();i++)
		{
			System.out.println("device: "+ i + " " + deviceList.get(i));
		}
		
	}

	public void fetchPanelInfo(View v)
	{
		connection.fetchData();
		
	}
	
	 
	
	

}
