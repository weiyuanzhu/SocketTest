package mackwell.nlight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weiyuan.models.Panel;
import weiyuan.socket.Connection;
import weiyuan.util.CommandFactory;
import weiyuan.util.Constants;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.nclient.R;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link PanelListFragment.OnPanelListItemClickedCallBack} interface to handle
 * interaction events.
 * 
 */
public class PanelListFragment extends ListFragment implements Connection.CallBack{
	
	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnPanelListItemClickedCallBack {
		// TODO: Update argument type and name
		public void onListItemClicked(String ip, String location,int index);
		public void getAllPanels();
		public void passTest();

		
	}
	
	private List<Panel> panelList;
	
	

	private Handler statusUpdateHandler;
	
	private Button refreshBtn;
	private Button getAllPanelsBtn;
	private Button passTest;

	private OnPanelListItemClickedCallBack mCallBack;
	
	private List<Map<String,Object>> dataList = null;
	private SimpleAdapter simpleAdapter;
	private int mCurCheckPosition = 0;
	
	private List<Connection> connectionList = null;
	private List<char[]> commandList;

	public PanelListFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_panel_list, container, false);
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallBack = (OnPanelListItemClickedCallBack) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
		
		this.statusUpdateHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				
				String ip = (String) msg.obj;
				for(Map<String,Object> map:dataList)		
				{
					if(map.get("ip").equals(ip))
					{
						if(msg.arg1 == 1) {
							map.put("img", R.drawable.greentick);
							
						}
						else if(msg.arg1 == 3) {
							map.put("img", R.drawable.redcross);
						}
						
					}
				}
				
			
				
				simpleAdapter.notifyDataSetChanged();
				
			}
			
			
		};
	}

	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
		refreshBtn = (Button) getActivity().findViewById(R.id.panelList_refreshButton);
		//refreshBtn.setOnClickListener(refreshClicked);
		passTest = (Button) getActivity().findViewById(R.id.panelList_passTest);
		
		getAllPanelsBtn = (Button) getActivity().findViewById(R.id.panelList_getAllPanelButton);
		getAllPanelsBtn.setOnClickListener(getAllPanelsListener);
		passTest.setOnClickListener(passListener);
		dataList = getDataList();
		
		
		//ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_activated_1,dataList2);
		
		//setListAdapter (myAdapter);
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		simpleAdapter = new SimpleAdapter(getActivity(),dataList,R.layout.panel_list_row,
				new String[]{"location","ip","img"},
				new int[]{R.id.location,R.id.ip,R.id.img});
		setListAdapter(simpleAdapter);
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		
		
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallBack = null;
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		mCurCheckPosition = position;
		
		String ip = (String)dataList.get(position).get("ip");
    	String location = (String)dataList.get(position).get("location");
		//System.out.println(position);
		
		getListView().setItemChecked(position, true);
		
		mCallBack.onListItemClicked(ip, location,position);
		
	}
	

	public void refreshStatus(boolean isDemo, boolean isConnected) {
			
			//check connectivity and demo mode flag
			
		if(!isDemo && isConnected){
			
			connectionList = new ArrayList<Connection>();
			
			PanelListFragment currentFragment= (PanelListFragment)getFragmentManager().findFragmentByTag("panelListFragment");
			
			for(int i=0; i<dataList.size(); i++)
			{
				commandList = CommandFactory.getOverallStatus();
				String ip = (String) dataList.get(i).get("ip");
				
				Connection connection = new Connection(currentFragment,ip);
				connectionList.add(connection);
				
				connection.fetchData(commandList);
			}
		}
	}
		

	
	
	
	private List<Map<String,Object>> getDataList()
	{
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		Map<String, Object> map  = null;
		
		for(int i =0; i<panelList.size();i++)
		{
			Panel p = panelList.get(i);
			
			map = new HashMap<String, Object>();
			map.put("location",p.getPanelLocation());
			map.put("ip",p.getIp());
			if(p.getOverAllStatus()== Constants.ALL_OK){
				map.put("img", R.drawable.greentick);
			}else map.put("img", R.drawable.redcross);
			
			list.add(map);
	
		}
	
		return list;
	}

	
	
	
	@Override
	public void receive(List<Integer> rx,String ip) {
		System.out.println(rx);
		
		Message msg = statusUpdateHandler.obtainMessage();
		msg.arg1 = rx.get(3);
		msg.obj = ip;

		statusUpdateHandler.sendMessage(msg);
		
		for(Connection c: connectionList){
			
			if(c.getIp().equals(ip))
			{
				c.setIsClosed(true);
				
			}
			
		}
		
	}

	OnClickListener getAllPanelsListener = new OnClickListener()
	{

		@Override
		public void onClick(View arg0) {
			mCallBack.getAllPanels();
			
		}
		
		
	};
	
	OnClickListener passListener = new OnClickListener()
	{
		@Override
		public void onClick(View arg0) {
			mCallBack.passTest();
			
		}
		
		
	};
	
	protected List<Panel> getPanelList() {
		return panelList;
	}

	protected void setPanelList(List<Panel> panelList) {
		this.panelList = panelList;
	}
}
