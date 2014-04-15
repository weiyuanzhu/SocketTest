package mackwell.nlight;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weiyuan.models.Device;
import weiyuan.models.Panel;
import weiyuan.socket.Connection;
import weiyuan.socket.Connection.CallBack;
import weiyuan.util.CommandFactory;
import weiyuan.util.DataParser;
import weiyuan.util.GetCmdEnum;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nclient.R;

/**
 * @author weiyuan zhu
 * 15/04/2014 Starting develop branch
 * test 2 on feature branch
 */
public class LoadingScreenActivity extends BaseActivity{
	
	public static final String DEMO_MODE = "Demo Mode";
	
	//ipList = new String[] {"192.168.1.17","192.168.1.20","192.168.1.21","192.168.1.23","192.168.1.24"};
	private final String[] ipList = new String[] {"192.168.1.24"};
	
	private static final int LOADING = 0;
	private static final int PARSING = 1;
	private static final int LOADING_FINISHED = 2;
	private static final int ERROR = 3;
	
	private boolean isLoading = false;
	
	private Button liveBtn = null;
	private Button demoBtn = null;
	private TextView progressText = null;
	private ProgressBar progressBar = null;
	
	private List<Panel> panelList = null;
	private Map<String,Panel> panelMap = null;
	private Map<String,Connection> ip_connection_map = null;
	private Map<String,List<Integer>> rxBufferMap = null;
	
	private static int delay = 1000;
	private Handler mHandler = null;
	
	private int panelToLoad = 0; 
	
	
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
			panelToLoad --;

			//update progress with handler
			Message msg = mHandler.obtainMessage();
			
			if(panelToLoad==0){
				msg.arg1 = PARSING;
				
			}else msg.arg1 = LOADING;
			
			
			mHandler.sendMessage(msg);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			parse(ip);
			
		}
		
	}

	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading_screen);
		
		//init all view items
		liveBtn = (Button) findViewById(R.id.loadscreen_live_imageBtn);
		demoBtn = (Button) findViewById(R.id.loadscreen_demo_imageBtn);
		progressText = (TextView) findViewById(R.id.loadscreen_progress_textView);
		progressBar = (ProgressBar) findViewById(R.id.loadscreen_progressBar);
		
		//update connection flags
		checkConnectivity();
		
		//handler for deal with ui update and navigation
		mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				
				switch(msg.arg1){
					case LOADING:
						progressText.setText("Loading Panel Data " + " (" + panelToLoad + ")");
					case PARSING: 
						progressText.setText("Analysing Panel Data");
						break;
					case LOADING_FINISHED:
						progressText.setText("Finish Loading");
						break;
					case ERROR:
						String ipAdd = (String) msg.obj;
						progressText.setText("Cannot connect to " + ipAdd + ", please check connection.");
						progressBar.setVisibility(View.INVISIBLE);
						liveBtn.setEnabled(true);
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		
		//re-enable buttons
		demoBtn.setEnabled(true);
		liveBtn.setEnabled(true);
		
		//hide bars
		progressText.setVisibility(View.INVISIBLE);
		progressBar.setVisibility(View.INVISIBLE);
		
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
			
			
			//finish this activity to prevent back navi
			//finish();
			
		}
		
		
	};
	
	public void init()
	{
		
		/*
		 * Initial collections 
		 * 
		 */
		
		
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
		demoBtn.setEnabled(false);
		
		isDemo = true;
		
		progressText.setText("Preparing Panel Data");
		progressText.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);

		prepareDataForDemo();
		
		mHandler.postDelayed(loadFinished, delay);
		
	}
	
	
	public void loadAllPanels(View v)
	{
		
		//on main thread
		//show progress bar and text
		
		//set isDemo flag
		isDemo = false;
		panelToLoad = ipList.length;
		
		
		//Message msg = mHandler.obtainMessage();
		//msg.arg1 = LOADING;
		//mHandler.sendMessage(msg);

		progressText.setText("Loading Panel Data " + " (" + panelToLoad + ")");
		
		progressText.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		
		//check if loading is already in process
		if(!isLoading){
			
			
			System.out.println("------------liveMode clicked");
			List<char[]> commandList = CommandFactory.getPanelInfo();
			
			for(String ip: ipList){
				
				Connection conn = (Connection) ip_connection_map.get(ip);
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
		//msg.arg1 = PARSING;
		//mHandler.sendMessage(msg);
		
		List<Integer> rxBuffer = rxBufferMap.get(ip);
		
		List<List<Integer>> panelData = DataParser.removeJunkBytes(rxBuffer); 
		List<List<Integer>> eepRom = DataParser.getEepRom(panelData);	
		List<List<List<Integer>>> deviceList = DataParser.getDeviceList(panelData,eepRom);
		
		
		try {
			Panel newPanel = new Panel(eepRom, deviceList, ip);
			panelMap.put(ip, newPanel);
			panelList.add(newPanel);
			
			if(panelList.size()==ipList.length){
				
				//msg = new Message();
				msg.arg1 = LOADING_FINISHED;
				mHandler.sendMessage(msg);

				mHandler.postDelayed(loadFinished, delay);	
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void prepareDataForDemo()
	{
	
		Panel panel = new Panel("192.168.1.18");
		panel.setPanelLocation("Mackwell L&B 1");
		panel.setSerialNumber((long)1376880756);
		panel.setGtinArray(new int[]{131,1,166,43,154,4});

		panel.getLoop1().addDevice(new Device(0,"LB 1",0,0,0,254,1375167879,new int[]{11,1,166,43,154,4}));
		panel.getLoop1().addDevice(new Device(1,"LB 2",0,0,0,200,1374967295,new int[]{45,2,166,43,154,4}));
		panel.getLoop2().addDevice(new Device(128,"LB 4",0,0,0,150,1374467255,new int[]{78,3,166,43,154,4}));
		panel.getLoop2().addDevice(new Device(129,"LB 4",0,0,0,150,1374537221,new int[]{130,4,166,43,154,4}));
		
		
		panelList.add(panel);
	
		panel = new Panel("192.168.1.19");
		panel.setPanelLocation("Mackwell L&B 2");
		panel.setSerialNumber((long)1375868516);
		panel.setGtinArray(new int[]{132,2,166,43,154,4});
		
		panel.getLoop1().addDevice(new Device(0,"LB 5",72,48,0,0,1365167879,new int[]{145,5,166,43,154,4}));
		panel.getLoop1().addDevice(new Device(1,"LB 6",0,0,0,200,1366965291,new int[]{178,5,166,43,154,4}));
		panel.getLoop2().addDevice(new Device(128,"LB 7",0,0,0,150,1361562293,new int[]{223,3,166,43,154,4}));
		panel.getLoop2().addDevice(new Device(129,"LB 8",0,0,0,150,1363967292,new int[]{243,4,166,43,154,4}));

		panelList.add(panel);
		
		
	}


	@Override
	public void error(String ip) {
		// TODO Auto-generated method stub
		super.error(ip);
		System.out.println("Error: " + ip);
		ip_connection_map.get(ip).closeConnection();
		
		Message msg = mHandler.obtainMessage();
		msg.arg1 = ERROR;
		msg.obj = ip;
		mHandler.sendMessage(msg);
		
	}
	
	public void updateDeviceList (View view)
	{
		System.out.println("----------------------getDeviceList--------------------");
		Connection conn = ip_connection_map.get(ipList[0]);
		List<char[]> commandList = GetCmdEnum.UPDATE_LIST.get();
		conn.fetchData(commandList);
		
	}

	

}
