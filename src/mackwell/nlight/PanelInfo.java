package mackwell.nlight;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import weiyuan.models.Panel;
import weiyuan.socket.Connection;
import weiyuan.util.CommandFactory;
import weiyuan.util.DataParser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.example.nclient.R;

public class PanelInfo extends Activity  implements Connection.Delegation{

	
	private List<Integer> rxBuffer = null;  //raw data pull from panel
	
	private List<List<Integer>> panelData = null;  //all panel data (removed junk bytes)
	private List<List<Integer>> eepRom = null;		//panel eeprom data
	private List<List<Integer>> deviceList = null;	//device list
	
	private Connection connection = null;	
	
	private Panel panel = null;
	
	private List<char[]> commandList;
	


	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_panel_info);
		
		//setup ListView adapter
		
	
		//create connector
		
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
		
		rxBuffer.addAll(rx);

		connection.setIsClosed(true);
				
		/*if(this.rxBuffer.size() > 15000)
		{		
			connection.setIsClosed(true);		
		}*/
		
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
		rxBuffer = new ArrayList<Integer>();
		commandList = new ArrayList<char[]>();
		
		//char[] getPackage0 = new char[] {2, 165, 64, 0, 32, 0,0x5A,0xA5,0x0D,0x0A};
		//char[] getPackage1 = new char[] {2, 165, 64, 15, 96, 0,0x5A,0xA5,0x0D,0x0A};
		//char[] getPackage2 = new char[] {2, 165, 64, 15, 96, 0,0x5A,0xA5,0x0D,0x0A};
		//char[] getConfig = new char[] {0x02,0xA0,0x21,0x68,0x18,0x5A,0xA5,0x0D,0x0A};
		
		commandList = CommandFactory.getPanelInfo();
		//commandList.add(getPackage1);
		
		connection = new Connection(this,commandList);
		connection.fetchData();
		
	}
	
	public void getPanelInfo(View v) throws UnsupportedEncodingException
	{
		panel = new Panel(eepRom);
		System.out.println("================Panel Info========================");
		System.out.println(panel.toString());
		
	}
	
	public void command (View v)
	{
		commandList = CommandFactory.getPanelInfo();
		for (char[] c : commandList){
			System.out.println(c);
		}
	}

}
