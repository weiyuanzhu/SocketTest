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
import android.app.*;
import nlight_android.nlight.InputDialogFragment.*;
import android.widget.*;
import android.os.*;

/**
 * @author weiyuan zhu
 *
 */
public class InputDialogFragment extends DialogFragment{
	
	//Dialog title
	private String title;
	
	/*dialog type: 0 = Device Name
	 * 			   1 = Panel Name
	 * 			   2 = Contact
	 * 			   3 = Tel
	 * 			   4 = Mobile
	 * 			   5 = Passcode
	 */
	private int type = 0;
	
	//hint for user input
	private String hint;
	
	//dialog listener for callback
	private NoticeDialogListener mListener= null;
	
	//EditText for user input
	private EditText inputEditText = null;
	
	public interface NoticeDialogListener{
		public void setInformation(String userInput);
		
	}
	
	
	

	
	
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
		inputEditText = (EditText) dialogView.findViewById(R.id.device_dialog_location);
		
		//set max length allowed for edittext
		InputFilter[] filters = {new InputFilter.LengthFilter(Constants.TEXT_MAX)};  
		inputEditText.setFilters(filters); 
		
		//set TextEdit devault message and set cursor to last position
		inputEditText.setText(hint == null? "Name device:" : hint);
		inputEditText.setSelection(hint.length());
		
		//set dialog view
		builder.setView(dialogView);
		
		//set title and buttons
		builder.setMessage(getTitle())
				.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
			
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						//pass location back to activity via callback
						
						mListener.setInformation(inputEditText.getText().toString());
						
						System.out.println("device location-----> " + inputEditText.getText().toString());
						
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

	//getters and setters

	public String getHint() {
		return hint;
	}



	public void setHint(String hint) {
		this.hint = hint;
	}



	public String getTitle() {
		
		switch(type){
			case 0: title = "Name Device";
					break;
			case 1: title = "Name Panel";
					break;
			default: title = "Enter Information";
					break;
		
		
		}
		
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public int getType() {
		return type;
	}



	public void setType(int type) {
		this.type = type;
	}
	
	
	
	
	
	

}
