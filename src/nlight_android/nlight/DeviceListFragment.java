package nlight_android.nlight;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nlight_android.adapter.MyExpandableListAdapter;
import nlight_android.models.Device;
import nlight_android.models.Loop;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nclient.R;

/**
 * A simple   {@link android.support.v4.app.Fragment}  subclass. Activities that contain this fragment must implement the  {@link DeviceListFragment.OnDevicdListFragmentListener}  interface to handleinteraction events.
 */
public class DeviceListFragment extends Fragment {
	
	
	/**
	 * This interface must be implemented by activities that contain this fragment to allow an interaction in this fragment to be communicated to the activity and potentially other fragments contained in that activity. <p> See the Android Training lesson <a href= "http://developer.android.com/training/basics/fragments/communicating.html" >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnDevicdListFragmentListener {
		// TODO: Update argument type and name
		public void onDeviceItemClicked(int groupPosition, int childPosition);
		public void onGroupExpandOrCollapse(int groupPosition);
		public void ft(int address);
		public void dt(int address);
		public void st(int address);
		public void id(int address);
		public void stopId(int address);
		public void refreshDevice(int address);
		public void seekBar();

	}
	
	

	private OnDevicdListFragmentListener mListener;
	
	
	private ExpandableListView deviceListView;
	private MyExpandableListAdapter mAdapter;
	private MyActionModeCallback mActionMode;
	private List<Loop> listDataHeader;
    private Map<Loop, List<Device>> listDataChild;
    

	
	private Loop loop1;
	private Loop loop2;
	
	private Device currentSelectedDevice;
	private Loop currentSelectedLoop;
	private boolean isLoopSelected;
	
	private class MyActionModeCallback implements AbsListView.MultiChoiceModeListener {
		
		View actionModeView = null;
		TextView counterTextView = null;
		
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			
			mAdapter.setMultiSelectMode(false);
			mAdapter.clearCheck();
			mAdapter.notifyDataSetChanged();
			//mActionMode = null;
		}
		
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.device_actionmode, menu);
	        
	        actionModeView = LayoutInflater.from(getActivity()).inflate(R.layout.actionbar_devicelist,null);
			
			counterTextView = (TextView) actionModeView.findViewById(R.id.deviceListFragment_counter_number_textView);
			
			//counterTextView.setText(Integer.toString(mAdapter.getCheckedCount()));
			
			mode.setCustomView(actionModeView);
			
			
	        
	        
	        return true;
		
		}
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			
			int position = deviceListView.getCheckedItemPosition();
			System.out.println(position);
			switch(item.getItemId())
			{
				case R.id.device_ft:
					mListener.ft(getAddress());
					Toast.makeText(getActivity(), "Function test in progress.", Toast.LENGTH_LONG).show();
					break;
				case R.id.device_st:
					mListener.st(getAddress());
					Toast.makeText(getActivity(), "Stoping all pending tests.", Toast.LENGTH_LONG).show();
					break;
				case R.id.device_dt:
					mListener.dt(getAddress());
					Toast.makeText(getActivity(), "Duration test in progress.", Toast.LENGTH_LONG).show();
					break;
				case R.id.device_id:
					mListener.id(getAddress());
					Toast.makeText(getActivity(), "Device identifying in progress.", Toast.LENGTH_LONG).show();
					break;
				case R.id.device_stopId:
					mListener.stopId(getAddress());
					Toast.makeText(getActivity(), "Stoping device identifying.", Toast.LENGTH_LONG).show();
					break;
				case R.id.device_refresh:
					
					mListener.refreshDevice(getAddress());
					Toast.makeText(getActivity(), "Refreshing device status.", Toast.LENGTH_LONG).show();
					break;
				
				default: break;
			
			}
			

			//close action mode when actions clickeds
			//mode.finish();
			return false;
		}

		@Override
		public void onItemCheckedStateChanged(ActionMode mode, int position,
				long id, boolean checked) {
			
			if(!mAdapter.isMultiSelectMode()){
				mAdapter.clearCheck();
				mAdapter.setMultiSelectMode(true);
				mAdapter.selectItem(0, position-1);
			}
			
			updateCounter();
			mAdapter.notifyDataSetChanged();
			
			counterTextView.setText(Integer.toString(mAdapter.getCheckedCount()));
			
			System.out.println("------------onItemCheckedStateChanged-------------");
			System.out.println("Position: " + position + " checked: " + checked);
		}
		
		public void updateCounter(){
			counterTextView.setText(Integer.toString(mAdapter.getCheckedCount()));
			
		}
	};
	
	

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
		mActionMode = new MyActionModeCallback();
				
		//Sort by faults -- default sort
        sort(DeviceActivity.SORT_BY_FAULTY);
		
		deviceListView.setAdapter(mAdapter);
		
		deviceListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		deviceListView.setMultiChoiceModeListener(mActionMode);
		
		
		
		deviceListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				//deviceListView.setItemChecked(position, true);
				System.out.println(position + " clicked");
				
			}
		});
		
		deviceListView.setOnGroupExpandListener(new OnGroupExpandListener(){
	
			@Override
			public void onGroupExpand(int groupPosition) {
				
				// determine which position to highlight 
				int position =0;
				if(deviceListView.isGroupExpanded(0) ){
					position = groupPosition ==0 ? groupPosition : groupPosition + listDataChild.get(loop1).size();	
				}
				else {
					 position = groupPosition;
				}
				//deviceListView.setItemChecked(position, true);
				
				
				mAdapter.selectItem(groupPosition, -1);
				
				int loop = groupPosition+1;
				String str = "Loop " + loop + " Expanded";
				Toast.makeText(getActivity(),str,Toast.LENGTH_SHORT).show();
				mListener.onGroupExpandOrCollapse(groupPosition);
				
				mAdapter.notifyDataSetChanged();
			}
			
			
				
			
		});
		
		deviceListView.setOnGroupCollapseListener(new OnGroupCollapseListener(){

			@Override
			public void onGroupCollapse(int groupPosition) {
			
				//deviceListView.clearChoices();
				if(!mAdapter.isMultiSelectMode()){
					mAdapter.clearCheck();
				}
				
				//this is for single action mode
				/*if (mActionMode != null) {
					mActionMode.finish();
		        }*/
				
				mListener.onGroupExpandOrCollapse(groupPosition);
			}
			
			
		});
		
		deviceListView.setOnChildClickListener(new OnChildClickListener() {
			 
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id)
            {
                /*Toast.makeText(
                        getActivity(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                        listDataHeader.get(groupPosition)).get(
                                        childPosition), Toast.LENGTH_SHORT)
                        .show();*/
            	
            	//single action mode
				/*if (mActionMode != null) {
					mActionMode.finish();
		        }*/
            	
            	
				
            	/*if(groupPosition==0){
            		int pos = childPosition + 1;
            		//deviceListView.setItemChecked(childPosition+1, true);
            		
            	}
            	else {
            		int pos = listDataChild.get(listDataHeader.get(1)).size()+2+childPosition;   
            		//deviceListView.setItemChecked(pos, true);
            		
            	}*/
            	
            	mAdapter.selectItem(groupPosition, childPosition);
            	
            	if(mAdapter.isMultiSelectMode()){
            		mActionMode.updateCounter();
            	}
            	
                mListener.onDeviceItemClicked(groupPosition, childPosition);
                
                mAdapter.notifyDataSetChanged();
				
				
                return true;
            }
        });
		
		/*deviceListView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				deviceListView.setItemChecked(position, true);
				int type = ExpandableListView.getPackedPositionType(id);
				int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                int childPosition = ExpandableListView.getPackedPositionChild(id);  
				  
				
				if(type==0) {
					
					isLoopSelected = true; 
					if(groupPosition==0){
						currentSelectedLoop = loop1;
						
					}else currentSelectedLoop = loop2;
				
				}
				else {
					isLoopSelected = false;
					
					if(groupPosition==0){
						currentSelectedDevice = loop1.getDevice(childPosition);
						
					}else currentSelectedDevice = loop2.getDevice(childPosition);
					
				}
				
				System.out.println("type: " + type + " group position: " + groupPosition + " childPositon: " + childPosition);
				if (mActionMode != null) {
					mActionMode.finish();
					
					mActionMode = getActivity().startActionMode(deviceActionModeCallback);
		            return true;
		        }
				
				//check type and position to decide whether a loop or device is selected and which one
				
				
				

		        // Start the CAB using the ActionMode.Callback defined above
		        mActionMode = getActivity().startActionMode(deviceActionModeCallback);
		        view.setSelected(true);
		        return true;
				

			}
		});
		*/
		
		super.onActivityCreated(savedInstanceState);
	}

	@Override 
	public void onDetach() {
		super.onDetach();
		mListener = null;
		mActionMode = null;
		
	}

	

	public void setLoop1(Loop loop) {
		this.loop1 = loop;
	}

	public void setLoop2(Loop loop) {
		this.loop2 = loop;
	}
	

	private void initListData()
	{
		listDataHeader = new ArrayList<Loop>();
        listDataChild = new HashMap<Loop, List<Device>>();
 
       

        // Adding child data
        // Header, Child data
        
        if(this.loop1!=null){
        	 listDataHeader.add(loop1);
        	 listDataChild.put(loop1, loop1.getDeviceList());
        }
        if(this.loop2!=null){
        	 listDataHeader.add(loop2);
        	 listDataChild.put(loop2, loop2.getDeviceList());
       }

        
		
	}
	
	private int getAddress()
	{
		int address = 0; 
		
		if(isLoopSelected){
			
			if(currentSelectedLoop.equals(loop1))
			{
				address = 64;
			}else address = 192;
		}
		else address = currentSelectedDevice.getAddress();
		
		System.out.println("Device or Loop Address-----------------> " + address);
		
		return address;
		
		
	}
	
	public void updateLocation(int groupPosition, int index, String location)
	{
		if(groupPosition==0){
			listDataChild.get(loop1).get(index).setLocation(location);
			
		}else listDataChild.get(loop2).get(index).setLocation(location);
		
		
		mAdapter.notifyDataSetChanged();
	}
	
	public void refershStatus()
	{
		updateProgressIcon(0);
		mAdapter.notifyDataSetChanged();
		
	}
		
	
	public void sort(Comparator<Device> comparator){
		System.out.println("sort");
		
		mAdapter.sort(comparator);
		
	}

	public void search(String query){
		System.out.println(query);
		
		
	}
	
	public void updateProgressIcon(int status){
		
		listDataChild.get(loop1).get(0).setCurrentStatus(1);
		mAdapter.notifyDataSetChanged();
		
	}
	
	public void updateDeviceList(){
		mAdapter.notifyDataSetChanged();
	}
		
}


