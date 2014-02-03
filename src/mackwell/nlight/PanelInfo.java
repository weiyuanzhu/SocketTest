package mackwell.nlight;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weiyuan.models.Panel;
import weiyuan.socket.Connection;
import weiyuan.util.CommandFactory;
import weiyuan.util.DataParser;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.example.nclient.R;

public class PanelInfo extends ListActivity  implements Connection.Delegation{
	
	private Handler progressHandler;
	private Handler listUpdateHandler;

	private ProgressBar progressBar = null;
	
	private List<Map<String,Object>>  data;
	
	private List<Integer> rxBuffer = null;  //raw data pull from panel
	
	private List<List<Integer>> panelData = null;  //all panel data (removed junk bytes)
	private List<List<Integer>> eepRom = null;		//panel eeprom data
	private List<List<Integer>> deviceList = null;	//device list
	
	private Connection connection = null;	
	
	private Panel panel = null;
	private SimpleAdapter sa;
	
	private List<char[]> commandList;
	
	private int packageCount;
	


	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_panel_info);
		
		
		progressHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				
				progressBar.setProgress(msg.arg1);
				
				if(msg.arg1 == 16)
				{
					progressBar.setVisibility(View.GONE);
					
				}
				
			}
			
			
		};
		
		listUpdateHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				
				try {
					data = getData(new Panel(eepRom));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				sa = new SimpleAdapter(PanelInfo.this, data, R.layout.panel_info_row, new String[] {"text1","text2"}, new int[] {R.id.textView1,R.id.textView2});
				
				setListAdapter(sa);
			
			}
			
			
		};
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		
		progressBar.setMax(16);
		
		
		
		//setup list view
		
		sa = new SimpleAdapter(this, getData(panel), R.layout.panel_info_row, new String[] {"text1","text2"}, new int[] {R.id.textView1,R.id.textView2});
		
		setListAdapter(sa);
		
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
		
		packageCount += 1 ;
		
		Message msg = progressHandler.obtainMessage();
		msg.arg1 = packageCount;
		
		progressHandler.sendMessage(msg);
		
		
		rxBuffer.addAll(rx);

		connection.setIsClosed(true);
				
		/*if(this.rxBuffer.size() > 15000)
		{		
			connection.setIsClosed(true);		
		}*/
		
		
		if(packageCount == 16)
		{
			connection.closeConnection();
			//progressBar.setVisibility(View.INVISIBLE);
			
			parse();
			
		}
		System.out.println("Actual bytes received: " + rxBuffer.size());
		
		
		
	}
	
	public void parse()
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
		
		Message msg = listUpdateHandler.obtainMessage();
		listUpdateHandler.sendMessage(msg);
		
	}

	public void fetchPanelInfo(View v)
	{
		packageCount = 0;
		progressBar.setVisibility(View.VISIBLE);
		
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
	
	public Panel getPanel()
	{
		try {
			panel = new Panel(eepRom);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("================Panel Info========================");
		System.out.println(panel.toString());
		
		return panel;
		
	}

	public List<Map<String,Object>> getData(Panel panel)
	{
		
		data = new ArrayList<Map<String,Object>>();
		
		if(panel == null)
		{
					
			Map<String,Object> map = new HashMap<String,Object>();
			
			map.put("text1", "Location");
			map.put("text2", "**");
		
			data.add(map);
			
			map = new HashMap<String,Object>();
			
			map.put("text1", "Contact");
			map.put("text2", "**");
		
			data.add(map);
		}
		else
		{
			Map<String,Object> map = new HashMap<String,Object>();
			
			map.put("text1", "Location");
			map.put("text2", panel.getPanelLocation());
			data.add(map);
			
			map.put("text1", "Contact");
			map.put("text2", panel.getContact());
			data.add(map);
			
			
		}
		
		return data;
	}
	
	
}
