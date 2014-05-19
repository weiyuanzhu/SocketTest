package nlight_android.nlight;

import java.util.ArrayList;
import java.util.List;

import com.example.nclient.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class ListDialogFragment extends DialogFragment {
	
	public interface ListDialogListener{
		public void connectPanels(List<Integer> selected);

		
	} 
	
	private String[] ips; 												//An array contains panel' IP
	private ListDialogListener mListener; 								//A callback listener for dialog when button clicked
	private List<Integer> mSelectedItems = new ArrayList<Integer>(); 	//a list contains item selected

	public ListDialogFragment() {
		// Required empty public constructor
	}
	
	

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		  // Where we track the selected items
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
	    // Set the dialog title
	    builder.setTitle(ips == null? "No panel found in the building " : "Panel List")
	    
	    
	    
	    // Specify the ip array, the items to be selected by default (null for none),
	    // and the listener through which to receive callbacks when items are selected
	           .setMultiChoiceItems(ips, null,
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
	           })
	    // Set the action buttons
	           .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   // User clicked OK, so save the mSelectedItems results somewhere
	                   // or return them to the component that opened the dialog
	            	   mListener.connectPanels(mSelectedItems);
	            	   
	                   
	               }
	           })
	           .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
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



}
