package nlight_android.nlight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.nclient.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class ListDialogFragment extends DialogFragment {
	
	public interface ListDialogListener{
		public void connectPanels(List<Integer> selected);

		
	} 
	private ListView listView;
	private List<Map<String,Object>> dataList;
	private String[] ips; 												//An array contains panels' IP
	private ListDialogListener mListener; 								//A callback listener for dialog when button clicked
	private List<Integer> mSelectedItems = new ArrayList<Integer>(); 	//a list contains item selected

	public ListDialogFragment() {
		// Required empty public constructor
	}
	
	

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		listView = new ListView(getActivity());
		
		SimpleAdapter mAdapter = new SimpleAdapter(getActivity(), getDataList(), R.layout.panel_list_row2, new String[]{"ip","location"},new int[]{R.id.ip_textview,R.id.location_checkedtextview});
		
		listView.setAdapter(mAdapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(R.id.location_checkedtextview);
				
				if(checkedTextView.isChecked())
					checkedTextView.setChecked(false);
				else 
					checkedTextView.setChecked(true);
				
			}

		
			
			
			
			
			
		});
		// Where we track the selected items
		
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
	    // Set the dialog title
	    builder.setTitle(ips == null? "No panel found in the building " : "Panel List");
	    
	    
	    
	    // Specify the ip array, the items to be selected by default (null for none),
	    // and the listener through which to receive callbacks when items are selected
	   /*builder.setMultiChoiceItems(ips, null,
	                      new DialogInterface.OnMultiChoiceClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int which,
	                       boolean isChecked) {
	                   if (isChecked) {
	                       // If the user checked the item, add it to the selected items
	                       mSelectedItems.add(which);
	                   } else if (mSelectedItems.contains(which)) {
	                       // Else, if the item is already in the array, remove it 
	                       mSelectedItems.remove(Integer.valueOf(which));
	                   }
	               }
	           });*/
	    
	    // Set the action buttons
	    builder.setView(listView);
	    
	           builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   // User clicked OK, so save the mSelectedItems results somewhere
	                   // or return them to the component that opened the dialog
	            	   //mListener.connectPanels(mSelectedItems);
	            	   System.out.println(listView.getCheckedItemPositions());
	                   
	               }
	           });
	           builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   
	               }
	           });

	    return builder.create();
	}

	@Override
	public void onAttach(Activity activity) {
		//attach mListener to the activity creates this dialog
		mListener = (ListDialogListener) activity;
		super.onAttach(activity);
	}



	public String[] getIps() {
		return ips;
	}



	public void setIps(String[] ips) {
		this.ips = ips;
	}



	public List<Map<String, Object>> getDataList() {
		return dataList;
	}



	public void setDataList(List<Map<String, Object>> dataList) {
		this.dataList = dataList;
	}



}
