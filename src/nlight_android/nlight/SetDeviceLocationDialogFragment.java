package nlight_android.nlight;

import nlight_android.util.Constants;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.nclient.R;

/**
 * @author weiyuan zhu
 *
 */
public class SetDeviceLocationDialogFragment extends DialogFragment{
	
	private String location;
	
	public interface NoticeDialogListener{
		public void setLocation(String location);
		
	}
	
	
	private NoticeDialogListener mListener= null;
	private EditText locationEditText = null;

	
	
	@Override
	public void onAttach(Activity activity) {
		
		mListener = (NoticeDialogListener) activity;
		
		super.onAttach(activity);
	}



	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		//create alertdialog builder
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		//get inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		//create final view with dialog layout
		final View dialogView = inflater.inflate(R.layout.dialog_setdevice_name, null);

		//get EditText view
		locationEditText = (EditText) dialogView.findViewById(R.id.device_dialog_location);
		
		//set max length allowed for edittext
		InputFilter[] filters = {new InputFilter.LengthFilter(Constants.TEXT_MAX)};  
		locationEditText.setFilters(filters); 
		
		//set TextEdit devault message and set cursor to last position
		locationEditText.setText(location == null? "Name device:" : location);
		locationEditText.setSelection(location.length());
		
		//set dialog view
		builder.setView(dialogView);
		
		//set title and buttons
		builder.setMessage("Name Device Location")
				.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
			
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						//pass location back to activity via callback
						
						mListener.setLocation(locationEditText.getText().toString());
						
						System.out.println("device location-----> " + locationEditText.getText().toString());
						
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}
				})
		
		
		;
		
		
		
		return builder.create();
		
		
		
	}



	public String getLocation() {
		return location;
	}



	public void setLocation(String location) {
		this.location = location;
	}
	
	
	
	
	
	

}
