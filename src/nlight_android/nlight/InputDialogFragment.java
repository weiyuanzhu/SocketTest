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
	
	//hint for user input
	private String hint;
	
	//dialog listener for callback
	private NoticeDialogListener mListener= null;
	
	//EditText for user input
	private EditText locationEditText = null;
	
	public interface NoticeDialogListener{
		public void setLocation(String location);
		
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
		locationEditText = (EditText) dialogView.findViewById(R.id.device_dialog_location);
		
		//set max length allowed for edittext
		InputFilter[] filters = {new InputFilter.LengthFilter(Constants.TEXT_MAX)};  
		locationEditText.setFilters(filters); 
		
		//set TextEdit devault message and set cursor to last position
		locationEditText.setText(hint == null? "Name device:" : hint);
		locationEditText.setSelection(hint.length());
		
		//set dialog view
		builder.setView(dialogView);
		
		//set title and buttons
		builder.setMessage(title == null? "Enter Input" : title)
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

	//getters and setters

	public String getHint() {
		return hint;
	}



	public void setHint(String hint) {
		this.hint = hint;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}
	
	
	
	
	
	

}
