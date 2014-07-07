package nlight_android.nlight;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nlight_android.adapter.DeviceInfoListAdapter;
import nlight_android.models.Device;

import com.example.nclient.R;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link DeviceInfoFragment.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link DeviceInfoFragment#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */
/**
 * @author weiyuan zhu
 *
 */
public class DeviceInfoFragment extends ListFragment {


	public TextView updateStampTextView;
	
	private Calendar cal;
	
	private static final String ARG_DEVICE = "device";
	private static final String ARG_REFRESH = "autoRefresh";


	
	private Device device;
	private boolean isAutoRefresh;

	private SimpleAdapter mAdapter;
	private List<Map<String,Object>> dataList;
	private TextView deviceName_textView;



	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param device An instance of a device object
	 * @return A new instance of fragment DeviceInfoFragment.
	 */
	public static DeviceInfoFragment newInstance(Device device,boolean autoRefresh) {
		DeviceInfoFragment fragment = new DeviceInfoFragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_DEVICE, device);
		args.putBoolean(ARG_REFRESH, autoRefresh);
		fragment.setArguments(args);
		return fragment;
	}

	public DeviceInfoFragment()
	{

	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		
		
		if (getArguments() != null) {
			device = getArguments().getParcelable(ARG_DEVICE);
			isAutoRefresh = getArguments().getBoolean(ARG_REFRESH);
		}
		
		if(device.getGtinArray()!=null)
		{	
			
			BigInteger gtin = BigInteger.valueOf(device.getGtinArray()[0] + device.getGtinArray()[1] * 256 + 
				device.getGtinArray()[2] * 65536 + device.getGtinArray()[3] * 16777216L + 
				device.getGtinArray()[4] * 4294967296L + device.getGtinArray()[5] * 1099511627776L);
			this.device.setGTIN(gtin);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_device_info, container, false);
	}



	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		//mListener = (DeviceSetLocationListener) activity;
		
	}

	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		updateStampTextView = (TextView) getActivity().findViewById(R.id.deviceList_stamp_textView);
		
		deviceName_textView = (TextView) getActivity().findViewById(R.id.fragment_device_info_name);
		deviceName_textView.setLongClickable(true);
		deviceName_textView.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View arg0) {
				//display dialog
				InputDialogFragment dialog = new InputDialogFragment();
				
				dialog.setType(InputDialogFragment.DEVICE_NAME);
				dialog.setHint(device.getLocation());
				dialog.show(getFragmentManager(), "setDeviceLocationDialog");
				return true;
			}
			
			
			
			
		});
		
		
		deviceName_textView.setText(device.getLocation().startsWith("?")? "Device Name: ? [Click and hold to rename device]" : device.getLocation()); // set device name textView text
		
		System.out.println(device.toString());
		
		
		//getListView().setOnItemLongClickListener(longClickListener);

		dataList = getData(device);
		
		mAdapter = new DeviceInfoListAdapter(getActivity(), dataList, R.layout.device_info_row, 
				new String[] {"description","value"}, new int[] {R.id.deviceDescription,R.id.deviceValue});
		
		setListAdapter(mAdapter);
		
		updateDevice(device,isAutoRefresh);

	}

	@Override
	public void onDetach() {
		super.onDetach();
		//mListener = null;
	}

	
	
	public List<Map<String,Object>> getData(Device device)
	{
		
		if(dataList==null) dataList = new ArrayList<Map<String,Object>>();
		
			
		Map<String,Object> map = new HashMap<String,Object>();
			
		
		//put correct address for device, -128 if it is on loop2
		int address = device.getAddress() < 127 ? device.getAddress(): device.getAddress()-128; 
		
		map.put("description", "Address");
		map.put("value", device==null? "n/a" : address);
		
		dataList.add(map);
		
		map = new HashMap<String,Object>();
		
		map.put("description", "Serial number");
		map.put("value", device==null? "n/a" : device.getSerialNumber());
			
		dataList.add(map);
		map = new HashMap<String,Object>();
		
		map.put("description", "GTIN");
		map.put("value", device==null? "n/a" : device.getGTIN());
			
		dataList.add(map);
		/*
		 * map = new HashMap<String,Object>();
		 * map.put("description", "Location");
			map.put("value", device==null? "n/a" : device.getLocation());
		
			listDataSource.add(map);
		 * 
		 */
		
		
		map = new HashMap<String,Object>();
		
		map.put("description", "Emergency mode");
		map.put("value", device==null? "n/a" : device.getEmergencyModeText());
			
		dataList.add(map);
		map = new HashMap<String,Object>();
		
		map.put("description", "Emergency status");
		map.put("value", device==null? "n/a" : device.getEmergencyStatusText());
			
		dataList.add(map);
	
		map = new HashMap<String,Object>();
		
		map.put("description", "Failure status");
		map.put("value", device==null? "n/a" : device.getFailureStatusText());
			
		dataList.add(map);
		
		map = new HashMap<String,Object>();
		map.put("description", "Battery level");
		map.put("value", device==null? "n/a" : device.getBatteryLevel());
			
		dataList.add(map);
		
		map = new HashMap<String,Object>();
		map.put("description", "Last duration test result");
		map.put("value", device==null? "n/a" : device.getDtTime() + " minutes");
			
		dataList.add(map);
		
		map = new HashMap<String,Object>();
		map.put("description", "Total emgerency lamp operating time");
		map.put("value", device==null? "n/a" : device.getLampEmergencyTimeText());
	
		
		dataList.add(map);
		
		map = new HashMap<String,Object>();
		map.put("description", "Communication status");
		map.put("value", device==null? "n/a" : "OK");
			
		dataList.add(map);
	
		return dataList;
	}

	
	// location long clicked listener to set location for device

	OnItemLongClickListener longClickListener = new OnItemLongClickListener(){

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int position, long id) {
			
			// if locaion long clicked
			if(position==3)
			{
		
				//display dialog
				InputDialogFragment dialog = new InputDialogFragment();
				
				dialog.setHint(device.getLocation());
				dialog.show(getFragmentManager(), "setDeviceLocationDialog");
				
			}
	
			return false;
		}
		
		
		
	};

	
	
	/**
	 * @param location
	 */
	public void updateLocation()
	{
		//update listDataSource
		deviceName_textView.setText(device.getLocation());
		mAdapter.notifyDataSetChanged();
		
	}
	
	/**
	 * @param device
	 */
	public void updateDevice(Device device, boolean autoRefresh) {
		
		
		
		//update refresh data stamp
		
		//Calendar deviceCal = device.getCal();
		
		//demo cal
		Calendar deviceCal = Calendar.getInstance();
		
    	SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getTimeInstance();
    	
    	String on = autoRefresh? "On" : "Off";
    	
		updateStampTextView.setText("Auto refresh: " + on + " , Last refreshed: " + sdf.format(deviceCal.getTime()));
		
		//update deviceInfo ListView
		dataList.clear();
		dataList = getData(device);
			
		mAdapter.notifyDataSetChanged();
		
	}
	
}
