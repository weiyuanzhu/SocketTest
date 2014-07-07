package nlight_android.nlight;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nlight_android.models.Panel;
import nlight_android.socket.TCPConnection;
import nlight_android.util.CommandFactory;
import nlight_android.util.DataParser;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.example.nclient.R;

/**
 * A simple   {@link android.support.v4.app.Fragment}  subclass. Use the {@link PanelInfoFragment#newInstance}  factory method to create an instance ofthis fragment.
 */
@SuppressLint("ValidFragment")
public class PanelInfoFragment extends Fragment implements TCPConnection.CallBack {
	
	

	

	private static final String ARG_IP = "ip";
	private static final String ARG_LOCATION = "location";


	private String ip;
	private String location;
	
	private TextView locationTextView;
	private Button fetchButton;
	private Button deviceButton;
	private ListView listView;
	private ProgressBar progressBar = null;

	private Handler progressHandler;
	private Handler listUpdateHandler;

	
	
	private List<Map<String,Object>>  listDataSource; //data for listview
	
	private List<Integer> rxBuffer = null;  //raw data pull from panel
	
	private List<List<Integer>> panelData = null;  //all panel data (removed junk bytes)
	private List<List<Integer>> eepRom = null;		//panel eeprom data (bytes)
	private List<List<List<Integer>>> deviceList = null;	//device list (bytes)
	
	private TCPConnection connection;	
	
	private Panel panel = null;
	
	private SimpleAdapter simpleAdapter;
	
	private List<char[]> commandList;
	
	private int packageCount;
	
	
	
	/* (non-Javadoc)
	 * @see nlight_android.socket.TCPConnection.CallBack#receive(java.util.List, java.lang.String)
	 */
	@Override
	public void receive(List<Integer> rx,String ip) {


		packageCount += 1 ;
		
		Message msg = progressHandler.obtainMessage();
		msg.arg1 = packageCount;
		
		progressHandler.sendMessage(msg);
		
		
		rxBuffer.addAll(rx);

		connection.setListening(true);
				
		/*if(this.rxBuffer.size() > 15000)
		{		
			connection.setIsClosed(true);		
		}*/
		
		
		if(packageCount == 16)
		{
			connection.closeConnection();
			connection = null;
			//progressBar.setVisibility(View.INVISIBLE);
			
			parse();
		
		}
		System.out.println("Actual bytes received: " + rxBuffer.size());
		
	}
	
	
	
	
	
	
	/**
	 * Use this factory method to create a new instance of this fragment using the provided parameters.
	 * @return  A new instance of fragment PanelInfoFragment.
	 */
	// TODO: Rename and change types and number of parameters
	@SuppressLint("ValidFragment")
	public static PanelInfoFragment newInstance(String ip, String location,Panel panel) {
		PanelInfoFragment fragment = new PanelInfoFragment(panel);
		Bundle args = new Bundle();
		args.putString(ARG_IP, ip);
		args.putString(ARG_LOCATION, location);
		fragment.setArguments(args);
		return fragment;
	}

	public PanelInfoFragment(Panel panel) {
		// Required empty public constructor
		
		this.panel = panel;
		
		//set BigInteger gtin from gtinArray int[]
		if(panel.getGtinArray()!=null)
		{	
			BigInteger gtin = BigInteger.valueOf(panel.getGtinArray()[0] + panel.getGtinArray()[1] * 256 + 
				panel.getGtinArray()[2] * 65536 + panel.getGtinArray()[3] * 16777216L + 
				panel.getGtinArray()[4] * 4294967296L + panel.getGtinArray()[5] * 1099511627776L);
			this.panel.setGtin(gtin);
		}
		
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		if (getArguments() != null) {
			ip = getArguments().getString(ARG_IP);
			location = getArguments().getString(ARG_LOCATION).trim();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		return inflater.inflate(R.layout.fragment_panel_info, container, false);

	}

	@SuppressLint("HandlerLeak")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		System.out.println("---------onViewCreated-----------");
		
		locationTextView = (TextView) getActivity().findViewById(R.id.panel_location);

		locationTextView.setText(location + " (" + panel.getFaultDeviceNo() + ")");
		locationTextView.setLongClickable(true);
		locationTextView.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View arg0) {
				//display dialog
				InputDialogFragment dialog = new InputDialogFragment();
				
				//setup dialog title and input hint
				dialog.setType(InputDialogFragment.PANEL_NAME);
				dialog.setHint(panel.getPanelLocation());
				dialog.show(getFragmentManager(), "UserInputDialog");
				return true;
			}
			
			
			
		
		});
		
		
		progressHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				
				progressBar.setProgress(msg.arg1);
				
				if(msg.arg1 == 16)
				{
					progressBar.setProgress(0);
					progressBar.setVisibility(View.INVISIBLE);
					
				}
				
			}
			
			
		};
		
		listUpdateHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				
				try {
					panel = new Panel (eepRom,deviceList,ip);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				listDataSource = getData(panel);
				
				simpleAdapter = new SimpleAdapter(getActivity(), listDataSource, R.layout.panel_info_row, 
						new String[] {"text1","text2"}, new int[] {R.id.panel_description,R.id.panel_value});
				
				listView.setAdapter(simpleAdapter);
			
			}
			
			
		};
		
		progressBar = (ProgressBar) getActivity().findViewById(R.id.panel_progressBar);
		
		fetchButton = (Button) getActivity().findViewById(R.id.panelInfo_fetchButton);
		fetchButton.setOnClickListener(fetchClicked);
		
		deviceButton = (Button) getActivity().findViewById(R.id.panelInfo_deviceButton);
		deviceButton.setOnClickListener(deviceBtnClicked);
		
		progressBar.setMax(16);
		
		
		listView = (ListView) getActivity().findViewById(R.id.panelInfo_listView);
		//setup list view
		
		simpleAdapter = new SimpleAdapter(getActivity(), getData(panel), R.layout.panel_info_row, new String[] {"text1","text2"}, new int[] {R.id.panel_description,R.id.panel_value});
		
		
		
		listView.setAdapter(simpleAdapter);
		
		
		//setup list item long click listener
		listView.setOnItemLongClickListener (longClickListener);
		
		
		

	}
	
	
	
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		//System.out.println("---------onStart-----------");
		//TextView deleteIcon = (TextView) listView.getChildAt(1).findViewById(R.id.panel_value);  
		
		//TextView test = (TextView) listView.getAdapter().getView(1, null, null).findViewById(R.id.panel_value);
		//deleteIcon.setTextColor(Color.BLUE);
		//test.setText("list view test");
	}

	
	



	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		/*System.out.println("---------onResume-----------");
		TextView deleteIcon = (TextView) listView.getChildAt(1).findViewById(R.id.panel_value);  
		
		TextView test = (TextView) listView.getAdapter().getView(1, null, null).findViewById(R.id.panel_value);
		deleteIcon.setTextColor(Color.BLUE);
		test.setText("list view test");*/
	}






	OnClickListener fetchClicked = new OnClickListener()
	{
		

		@Override
	    public void onClick(View v) {

	 
	       
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
		
		
		
		PanelInfoFragment currentFragment= (PanelInfoFragment)getFragmentManager().findFragmentByTag("tagTest");
		connection = new TCPConnection(currentFragment,ip);
		connection.fetchData(commandList);
		}
		
	};
	
	
	OnClickListener deviceBtnClicked = new OnClickListener(){

		@Override
		public void onClick(View arg0) {

			System.out.println("Get Device List");
			
			Intent intent = new Intent(getActivity(), DeviceActivity.class);

			if(panel!=null){
				
				intent.putExtra("location", panel.getPanelLocation());
				intent.putExtra("panel", panel);
				intent.putExtra("loop1",panel.getLoop1());
				intent.putExtra("loop2",panel.getLoop2());
				startActivity(intent);
				
			}
			
			
			
		}
		
		
	};
	
	public List<Map<String,Object>> getData(Panel panel)
	{
		
		listDataSource = new ArrayList<Map<String,Object>>();
		
			
		Map<String,Object> map = new HashMap<String,Object>();
			
		/*map.put("text1", "Location");
		map.put("text2", panel==null? "..." : panel.getPanelLocation());
		
		listDataSource.add(map);*/
		
		map = new HashMap<String,Object>();
		
		map.put("text1", "IP address");
		map.put("text2", panel==null? "..." : panel.getIp());
			
		listDataSource.add(map);
		
		map = new HashMap<String,Object>();
		
		map.put("text1", "Serial number");
		map.put("text2", panel==null? "..." : panel.getSerialNumber());
			
		listDataSource.add(map);
		map = new HashMap<String,Object>();
		
		
		
		
		
		map.put("text1", "GTIN");
		map.put("text2", panel==null? "..." : panel.getGtin());
			
		listDataSource.add(map);
		map = new HashMap<String,Object>();
		
		map.put("text1", "Contact");
		map.put("text2", panel==null? "..." : panel.getContact());
		
		listDataSource.add(map);
		map = new HashMap<String,Object>();
		
		map.put("text1", "Tel");
		map.put("text2", panel==null? "..." : panel.getTel());
			
		listDataSource.add(map);
		map = new HashMap<String,Object>();
		
		map.put("text1", "Mobile");
		map.put("text2", panel==null? "..." : panel.getMobile());
			
		listDataSource.add(map);
	
		map = new HashMap<String,Object>();
		
		map.put("text1", "Firmware version");
		map.put("text2", panel==null? "..." : panel.getVersion());
			
		listDataSource.add(map);
		
		map = new HashMap<String,Object>();
		map.put("text1", "Memory Used");
		map.put("text2", panel==null? "..." : panel.getReportUsage());
			
		listDataSource.add(map);
		
		map = new HashMap<String,Object>();
		map.put("text1", "Passcode");
		map.put("text2", panel==null? "..." : panel.getPasscode());
			
		listDataSource.add(map);
	
		return listDataSource;
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
		deviceList = DataParser.getDeviceList(panelData,eepRom);
		
		
		for(int i=0; i<deviceList.size();i++)
		{
			System.out.println("Loop: " + i);
			for(int j =0; j<deviceList.get(i).size();j++)
			{
				System.out.println("device: "+ j + " " + deviceList.get(i).get(j));
			}
		}

		Message msg = listUpdateHandler.obtainMessage();
		listUpdateHandler.sendMessage(msg);
		
	}

	@Override
	public void error(String ip) {
		// TODO Auto-generated method stub
		
	}

	public int updatePanelLocation(String location)
	{
		locationTextView.setText(location + " (" + panel.getFaultDeviceNo() + ")");
		return 1;
	}
	
	
	OnItemLongClickListener longClickListener = new OnItemLongClickListener(){

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int position, long id) {
			
			InputDialogFragment dialog = new InputDialogFragment();
			
			// if locaion long clicked
			switch(position){
				case 3: dialog.setHint(panel.getContact());
						dialog.setType(InputDialogFragment.PANEL_CONTACT);
						dialog.show(getFragmentManager(), "inputDialog");
						break;
				case 4: dialog.setHint(panel.getTel());
						dialog.setType(InputDialogFragment.PANEL_TEL);
						dialog.show(getFragmentManager(), "inputDialog");	
						break;
				case 5: dialog.setHint(panel.getMobile());
						dialog.show(getFragmentManager(), "inputDialog");
						dialog.setType(InputDialogFragment.PANEL_MOBILE);
						break;
				case 8: dialog.setHint(panel.getPasscode());
						dialog.setType(InputDialogFragment.PANEL_PASSCODE);
						dialog.show(getFragmentManager(), "inputDialog");
						break;
				default: break;
						 
					
				//display dialog
				
				
				
				
				
			}
	
			
			return true;
		}
		
		
		
	};

}
