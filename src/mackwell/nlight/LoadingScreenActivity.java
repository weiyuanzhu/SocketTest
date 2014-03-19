package mackwell.nlight;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weiyuan.models.Panel;
import weiyuan.socket.Connection;
import weiyuan.socket.Connection.CallBack;
import weiyuan.util.CommandFactory;
import weiyuan.util.DataParser;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nclient.R;

public class LoadingScreenActivity extends BaseActivity implements CallBack {
	
	public static final String DEMO_MODE = "Demo Mode";
	
	private static final int PARSING = 1;
	private static final int LOADING_FINISHED = 2;
	
	private ImageButton liveBtn = null;
	private ImageButton demoBtn = null;
	private TextView progressText = null;
	private ProgressBar progressBar = null;
	
	private String[] ipList = null;
	private List<Panel> panelList = null;
	private Map<String,Panel> panelMap = null;
	private Map<String,Connection> ip_connection_map = null;
	private Map<String,List<Integer>> rxBufferMap = null;
	
	private static int delay = 1000;
	private Handler mHandler = null;
	
	private boolean isLoading = false;
	
	
	/*
	 *	callback function for interface Connection.CallBack
	 */

	public void receive(List<Integer> rx, String ip) {
		List<Integer> rxBuffer = rxBufferMap.get(ip);
		rxBuffer.addAll(rx);
		Connection connection = ip_connection_map.get(ip);
		connection.setIsClosed(true);
		System.out.println(ip + " received package: " + connection.getPanelInfoPackageNo() + " rxBuffer size: " + rxBuffer.size());
		if(connection.isRxCompleted())
		{
			//connection.closeConnection();
			parse(ip);
			
		}
		
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading_screen);
		
		//init all view items
		liveBtn = (ImageButton) findViewById(R.id.loadscreen_live_imageBtn);
		demoBtn = (ImageButton) findViewById(R.id.loadscreen_demo_imageBtn);
		progressText = (TextView) findViewById(R.id.loadscreen_progress_textView);
		progressBar = (ProgressBar) findViewById(R.id.loadscreen_progressBar);
		
	
		
		//handler for deal with ui update and navigation
		mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				
				switch(msg.arg1){
					case PARSING: 
						progressText.setText("Analysing Panel Data");
						break;
					case LOADING_FINISHED:
						progressText.setText("Finish Loading");
						break;
					default: break;
				
				}
				
				super.handleMessage(msg);
			}
			
			
		};
		
		//init loading panals 
		init();
		
		
		
	}
	
	

	@Override
	protected void onDestroy() {
		for(String key : ip_connection_map.keySet())
		{
			
			Connection connection = ip_connection_map.get(key);
			connection.closeConnection();
			connection = null;
		}
		super.onDestroy();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.loading_screen, menu);
		return true;
	}
	

	//pass all panel objects to next activity when loading and parsing is finished
	
	Runnable loadFinished = new Runnable()
	{

		@Override
		public void run() {
			
			isLoading = false;
			
			
			
			//create intent
			Intent intent = new Intent(LoadingScreenActivity.this, PanelActivity.class);
			
			//put panelList into intent
			intent.putParcelableArrayListExtra("panelList", (ArrayList<? extends Parcelable>) panelList);
			intent.putExtra(DEMO_MODE, isDemo);
			startActivity(intent);
			
			finish();
			
		}
		
		
	};
	
	public void init()
	{
		
		/*
		 * Initial collections 
		 * 
		 */
		//ipList = new String[] {"192.168.1.17","192.168.1.20","192.168.1.21","192.168.1.23","192.168.1.24"};
		ipList = new String[] {"192.168.1.23"};
		
		//paneList(Parcable) is for navigation
		panelList = new ArrayList<Panel>();
		
		panelMap = new HashMap<String,Panel>();
		ip_connection_map = new HashMap<String,Connection>();
		rxBufferMap = new HashMap<String,List<Integer>>();
		
		
		/*
		 *  Initial connections and rxBuffer for each panel
		 */
		

		for(int i =0; i<ipList.length; i++)
		{
			Connection connection = new Connection(this, ipList[i]);
			ip_connection_map.put(ipList[i], connection);
			rxBufferMap.put(ipList[i], new ArrayList<Integer>());
			
		}


	}
	
	public void demoMode(View v){
		
		//set isDemo flag
		
		isDemo = true;
		
		progressText.setText("Preparing Panel Data");
		progressText.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		
		for(int i =0; i<ipList.length; i++)
		{
			Panel newPanel = new Panel(ipList[i]);
			panelList.add(newPanel);
	
		}
		
		mHandler.postDelayed(loadFinished, delay);
		
	}
	
	
	public void loadAllPanels(View v)
	{
		
		//still on main thread
		//show progress bar and text
		
		//set isDemo flag
		isDemo = false;
		
		progressText.setText("Loading Panel Data");
		progressText.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		
		//check if loading is already in process
		if(!isLoading){
			
			
			System.out.println("clicked");
			List<char[]> commandList = CommandFactory.getPanelInfo();
			
			for(String key : ip_connection_map.keySet()){
				
				Connection conn = (Connection) ip_connection_map.get(key);
				conn.fetchData(commandList);
			}
			
			
			//set button disable
			liveBtn.setEnabled(false);
			isLoading = true;
			
		}
		
		
	}
	
	public void parse(String ip)
	{
		
		
		Message msg = mHandler.obtainMessage();
		msg.arg1 = PARSING;
		mHandler.sendMessage(msg);
		
		List<Integer> rxBuffer = rxBufferMap.get(ip);
		
		List<List<Integer>> panelData = DataParser.removeJunkBytes(rxBuffer); 
		List<List<Integer>> eepRom = DataParser.getEepRom(panelData);	
		List<List<List<Integer>>> deviceList = DataParser.getDeviceList(panelData,eepRom);
		
		
		try {
			Panel newPanel = new Panel(eepRom, deviceList, ip);
			panelMap.put(ip, newPanel);
			panelList.add(newPanel);
			
			if(panelList.size()==ipList.length){
				
				msg = new Message();
				msg.arg1 = LOADING_FINISHED;
				mHandler.sendMessage(msg);

				mHandler.postDelayed(loadFinished, delay);	
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
