package mackwell.nlight;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weiyuan.models.Device;

import com.example.nclient.R;
import com.example.nclient.R.layout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link DeviceInfoFragment.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link DeviceInfoFragment#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */
public class DeviceInfoFragment extends ListFragment {
	
	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}
	

	private static final String ARG_DEVICE = "device";


	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;
	
	private Device device;
	private SimpleAdapter mSimpleAdapter;
	private List<Map<String,Object>> listDataSource;
	

	private OnFragmentInteractionListener mListener;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment DeviceInfoFragment.
	 */
	// TODO: Rename and change types and number of parameters

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
		
	}

	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		System.out.println(device.toString());
		mSimpleAdapter = new SimpleAdapter(getActivity(), getData(device), R.layout.device_info_row, 
				new String[] {"text1","text2"}, new int[] {R.id.deviceDescription,R.id.deviceValue});
		
		setListAdapter(mSimpleAdapter);
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
			
		map.put("text1", "Address");
		map.put("text2", device==null? "n/a" : device.getAddress());
		
		listDataSource.add(map);
		
		map = new HashMap<String,Object>();
		
		map.put("text1", "SerialNumber:");
		map.put("text2", device==null? "n/a" : device.getSerialNumber());
			
		listDataSource.add(map);
		map = new HashMap<String,Object>();
		
		map.put("text1", "GTIN:");
		map.put("text2", device==null? "n/a" : device.getGTIN());
			
		listDataSource.add(map);
		map = new HashMap<String,Object>();
		
		map.put("text1", "Location");
		map.put("text2", device==null? "n/a" : device.getLocation());
		
		listDataSource.add(map);
		map = new HashMap<String,Object>();
		
		map.put("text1", "Emergency Mode");
		map.put("text2", device==null? "n/a" : device.getEmergencyModeText());
			
		listDataSource.add(map);
		map = new HashMap<String,Object>();
		
		map.put("text1", "Emergency Status");
		map.put("text2", device==null? "n/a" : device.getEmergencyStatusText());
			
		listDataSource.add(map);
	
		map = new HashMap<String,Object>();
		
		map.put("text1", "Failure Status");
		map.put("text2", device==null? "n/a" : device.getFailureStatusText());
			
		listDataSource.add(map);
		
		map = new HashMap<String,Object>();
		map.put("text1", "Battery Level");
		map.put("text2", device==null? "n/a" : device.getBatteryLevel());
			
		listDataSource.add(map);
		
		map = new HashMap<String,Object>();
		map.put("text1", "Communication Status");
		map.put("text2", device==null? "n/a" : "OK");
			
		listDataSource.add(map);
	
		return listDataSource;
	}

	
	// location clicked to set location for device
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		System.out.println("position---> " + position);
		
		super.onListItemClick(l, v, position, id);
	}
	
	

}
