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
	
	/**
	 * This interface must be implemented by activities that contain this fragment to allow an interaction in this fragment to be communicated to the activity and potentially other fragments contained in that activity. <p> See the Android Training lesson <a href= "http://developer.android.com/training/basics/fragments/communicating.html" >Communicating with Other Fragments</a> for more information.
	 */
	public interface DeviceSetLocationListener {
		
		public void setDeviceLocation(String location);
	}
	

	public TextView updateStampTextView;
	
	private Calendar cal;
	
	private static final String ARG_DEVICE = "device";


	
	private Device device;
	private SimpleAdapter mSimpleAdapter;
	private List<Map<String,Object>> listDataSource;
	private TextView deviceName;

	@SuppressWarnings("unused")
	private DeviceSetLocationListener mListener;


	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param device An instance of a device object
	 * @return A new instance of fragment DeviceInfoFragment.
	 */
	public static DeviceInfoFragment newInstance(Device device) {
		DeviceInfoFragment fragment = new DeviceInfoFragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_DEVICE, device);
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
		mListener = (DeviceSetLocationListener) activity;
		
	}

	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		updateStampTextView = (TextView) getActivity().findViewById(R.id.deviceList_stamp_textView);
		
		deviceName = (TextView) getActivity().findViewById(R.id.fragment_device_info_name);
		deviceName.setLongClickable(true);
		deviceName.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View arg0) {
				//display dialog
				SetDeviceLocationDialogFragment dialog = new SetDeviceLocationDialogFragment();
				
				dialog.setLocation(device.getLocation());
				dialog.show(getFragmentManager(), "setDeviceLocationDialog");
				return true;
			}
			
			
			
			
		});
		
		
		deviceName.setText(device.getLocation().startsWith("?")? "Device Name" : device.getLocation()); // set device name textView text
		
		System.out.println(device.toString());
		mSimpleAdapter = new DeviceInfoListAdapter(getActivity(), getData(device), R.layout.device_info_row, 
				new String[] {"description","value"}, new int[] {R.id.deviceDescription,R.id.deviceValue});
		
		setListAdapter(mSimpleAdapter);
		
		//getListView().setOnItemLongClickListener(longClickListener);

		updateTimeStamp();

	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	
	
	public List<Map<String,Object>> getData(Device device)
	{
		
		listDataSource = new ArrayList<Map<String,Object>>();
		
			
		Map<String,Object> map = new HashMap<String,Object>();
			
		
		//put correct address for device, -128 if it is on loop2
		int address = device.getAddress() < 127 ? device.getAddress(): device.getAddress()-128; 
		
		map.put("description", "Address");
		map.put("value", device==null? "n/a" : address);
		
		listDataSource.add(map);
		
		map = new HashMap<String,Object>();
		
		map.put("description", "Serial number:");
		map.put("value", device==null? "n/a" : device.getSerialNumber());
			
		listDataSource.add(map);
		map = new HashMap<String,Object>();
		
		map.put("description", "GTIN:");
		map.put("value", device==null? "n/a" : device.getGTIN());
			
		listDataSource.add(map);
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
			
		listDataSource.add(map);
		map = new HashMap<String,Object>();
		
		map.put("description", "Emergency status");
		map.put("value", device==null? "n/a" : device.getEmergencyStatusText());
			
		listDataSource.add(map);
	
		map = new HashMap<String,Object>();
		
		map.put("description", "Failure status");
		map.put("value", device==null? "n/a" : device.getFailureStatusText());
			
		listDataSource.add(map);
		
		map = new HashMap<String,Object>();
		map.put("description", "Battery level");
		map.put("value", device==null? "n/a" : device.getBatteryLevel());
			
		listDataSource.add(map);
		
		map = new HashMap<String,Object>();
		map.put("description", "Last duration test result");
		map.put("value", device==null? "n/a" : device.getDtTime() + " minutes");
			
		listDataSource.add(map);
		
		map = new HashMap<String,Object>();
		map.put("description", "Total emgerency lamp operating time");
		map.put("value", device==null? "n/a" : device.getLampEmergencyTimeText());
	
		
		listDataSource.add(map);
		
		map = new HashMap<String,Object>();
		map.put("description", "Communication status");
		map.put("value", device==null? "n/a" : "OK");
			
		listDataSource.add(map);
	
		return listDataSource;
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
				SetDeviceLocationDialogFragment dialog = new SetDeviceLocationDialogFragment();
				
				dialog.setLocation(device.getLocation());
				dialog.show(getFragmentManager(), "setDeviceLocationDialog");
				
			}
	
			return false;
		}
		
		
		
	};

	
	
	/**
	 * @param location
	 */
	public void updateLocation(String location)
	{
		//update listDataSource
		deviceName.setText(location);
		mSimpleAdapter.notifyDataSetChanged();
		
	}
	
	/**
	 * @param device
	 */
	public void updateDevice(Device device) {
		
		updateTimeStamp();
		
		listDataSource = getData(device);
		
		mSimpleAdapter = new SimpleAdapter(getActivity(), listDataSource, R.layout.device_info_row, 
				new String[] {"description","value"}, new int[] {R.id.deviceDescription,R.id.deviceValue});
		
		setListAdapter(mSimpleAdapter);
		
		mSimpleAdapter.notifyDataSetChanged();
		
	}
	private void updateTimeStamp(){
		
		cal = device.getCal();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		updateStampTextView.setText("Last updated: " + sdf.format(cal.getTime()));
	}

}
