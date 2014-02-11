package mackwell.nlight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListFragment;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class PanelListFragment extends ListFragment {

	private OnListItemClickedCallBack mListener;
	
	private List<Map<String,Object>> dataList = null;
	private String[] dataList2;
	private int mCurCheckPosition = 0;

	public PanelListFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_panel_list, container, false);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnListItemClickedCallBack) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
		
		

		dataList = getDataList();
		
		dataList2 = new String[] {"First","Second","Third"};
		
		//ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_activated_1,dataList2);
		
		//setListAdapter (myAdapter);
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		SimpleAdapter adapter = new SimpleAdapter(getActivity(),dataList,R.layout.panel_list_row,
				new String[]{"location","ip","img"},
				new int[]{R.id.location,R.id.ip,R.id.img});
		setListAdapter(adapter);
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		mCurCheckPosition = position;
		System.out.println(position);
		
		getListView().setItemChecked(position, true);
		
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
	public interface OnListItemClickedCallBack {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}
	
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
		
		map.put("location","Testing Panel");
		map.put("ip","192.168.1.22");
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

}
