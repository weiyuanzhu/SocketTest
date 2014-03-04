package mackwell.nlight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import weiyuan.models.Device;
import weiyuan.models.Loop;
import Adapter.MyExpandableListAdapter;
import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nclient.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link DeviceListFragment.OnDeviceFragmentInteractionListener} interface to handle
 * interaction events.
 * 
 */
public class DeviceListFragment extends Fragment {

	private OnDeviceFragmentInteractionListener mListener;
	
	private ExpandableListView deviceListView;
	private MyExpandableListAdapter mAdapter;
	private ActionMode mActionMode;
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
		
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
		}
		
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			int pos = deviceListView.getCheckedItemPosition();
			return false;
		}
	};
	
	List<String> listDataHeader;
    HashMap<String, List<Device>> listDataChild;
    
    
	
	
	
	private Loop loop1;
	private Loop loop2;

	public DeviceListFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_device_list, container, false);
		
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnDeviceFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}
	
	



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		deviceListView = (ExpandableListView) getActivity().findViewById(R.id.expandableListView_deviceList);
		
		
		
		initListData();
		
		mAdapter = new MyExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
		deviceListView.setAdapter(mAdapter);
		
		//deviceListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		
		
		
		deviceListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				System.out.println(arg2 + "clicked");
				
			}
		});
		
		deviceListView.setOnGroupExpandListener(new OnGroupExpandListener(){

			@Override
			public void onGroupExpand(int groupPosition) {
				Toast.makeText(getActivity(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
				
			}
			
			
			
		});
		
		deviceListView.setOnChildClickListener(new OnChildClickListener() {
			 
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getActivity(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                        listDataHeader.get(groupPosition)).get(
                                        childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });
		
		deviceListView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				deviceListView.setItemChecked(position, true);
				int type = ExpandableListView.getPackedPositionType(id);
				int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                int childPosition = ExpandableListView.getPackedPositionChild(id);  
				  
				
				
				System.out.println("type: " + type + " group position: " + groupPosition + " childPositon: " + childPosition);
				if(mActionMode!=null){
	                return false;
	            }
				
				return false;
			}
			
			
			
			
		});
		
		super.onActivityCreated(savedInstanceState);
	}

	@Override 
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnDeviceFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}

	public void setLoop1(Loop loop) {
		this.loop1 = loop;
	}

	public void setLoop2(Loop loop) {
		this.loop2 = loop;
	}
	

	private void initListData()
	{
		listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Device>>();
 
        // Adding child data
       

        // Adding child data
        // Header, Child data
        
        if(this.loop1!=null){
        	 listDataHeader.add("Loop1");
        	 listDataChild.put(listDataHeader.get(0), loop1.getDeviceList());
        }
        if(this.loop2!=null){
        	 listDataHeader.add("Loop2");
        	 listDataChild.put(listDataHeader.get(1), loop2.getDeviceList());
       }


		
		
	}

}
