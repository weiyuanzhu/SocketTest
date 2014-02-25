package mackwell.nlight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weiyuan.socket.Connection;
import weiyuan.util.CommandFactory;
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
 * {@link PanelListFragment.OnListItemClickedCallBack} interface to handle
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
	public interface OnListItemClickedCallBack {
		// TODO: Update argument type and name
		public void onListItemClicked(String ip, String location,int index);
		public void getAllPanels();
		public void passTest();

		
	}
	
	private Handler statusUpdateHandler;
	
	private Button refreshBtn;
	private Button getAllPanelsBtn;
	private Button passTest;

	private OnListItemClickedCallBack mCallBack;
	
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
			mCallBack = (OnListItemClickedCallBack) activity;
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
						if(msg.arg1 == 1) {map.put("img", R.drawable.tick);}
						else if(msg.arg1 == 3) {map.put("img", R.drawable.cross);}
						
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
		refreshBtn.setOnClickListener(refreshClicked);
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
	
	OnClickListener refreshClicked = new OnClickListener(){

		@Override
		public void onClick(View v) {
			
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
		
		
		
	};
	
	
	
	public List<Map<String,Object>> getDataList()
	{
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("location","Nigel's Test Demo Unit");
		map.put("ip","192.168.1.17");
		map.put("img", R.drawable.panel);
		
		list.add(map);
		
		map = new HashMap<String,Object>();
		
		map.put("location","Mackwell Factory");
		map.put("ip","192.168.1.21");
		map.put("img", R.drawable.panel);
		
		list.add(map);
		
		/*map = new HashMap<String,Object>();
		
		map.put("location","N-Light CONNECT demo 01");
		map.put("ip","192.168.1.19");
		map.put("img", R.drawable.panel);
		
		list.add(map);*/
		
		map = new HashMap<String,Object>();
		
		map.put("location","Mackwell Link Building");
		map.put("ip","192.168.1.20");
		map.put("img", R.drawable.panel);
		
		list.add(map);
		
		map = new HashMap<String,Object>();
		
		map.put("location","Mackwell Specials");
		map.put("ip","192.168.1.23");
		map.put("img", R.drawable.panel);
		
		list.add(map);
		
		map = new HashMap<String,Object>();
		
		map.put("location","Technical Demo Board");
		map.put("ip","192.168.1.24");
		map.put("img", R.drawable.panel);
		
		list.add(map);
				
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
}
