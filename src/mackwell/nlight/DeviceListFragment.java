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
import android.view.MenuInflater;
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
 * {@link DeviceListFragment.OnDevicdListFragmentListener} interface to handle
 * interaction events.
 * 
 */
public class DeviceListFragment extends Fragment {
	
	
	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnDevicdListFragmentListener {
		// TODO: Update argument type and name
		public void onDeviceItemClicked(int groupPosition, int childPosition);

	}
	

	private OnDevicdListFragmentListener mListener;
	
	private ExpandableListView deviceListView;
	private MyExpandableListAdapter mAdapter;
	private ActionMode mActionMode;
	
	private ActionMode.Callback deviceActionModeCallback = new ActionMode.Callback() {
		
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
			MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.device_actionmode, menu);
	        return true;
		
		}
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			int position = deviceListView.getCheckedItemPosition();

			System.out.println(position);

			
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


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnDevicdListFragmentListener) activity;
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
		
		deviceListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		
		
		deviceListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				deviceListView.setItemChecked(position, true);
				System.out.println(position + "clicked");
				
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
                /*Toast.makeText(
                        getActivity(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                        listDataHeader.get(groupPosition)).get(
                                        childPosition), Toast.LENGTH_SHORT)
                        .show();*/
            	if(groupPosition==0){
            		deviceListView.setItemChecked(childPosition+1, true);
            	}
            	else {
            		int pos = listDataChild.get(listDataHeader.get(1)).size()+2+childPosition;   
            		deviceListView.setItemChecked(pos, true);
            	}
                mListener.onDeviceItemClicked(groupPosition, childPosition);
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
				if (mActionMode != null) {
		            return false;
		        }

		        // Start the CAB using the ActionMode.Callback defined above
		        mActionMode = getActivity().startActionMode(deviceActionModeCallback);
		        view.setSelected(true);
		        return true;
				

			}
			
			
			
			
		});
		
		super.onActivityCreated(savedInstanceState);
	}

	@Override 
	public void onDetach() {
		super.onDetach();
		mListener = null;
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


