package nlight_android.nlight;

import nlight_android.util.Constants;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.PasswordTransformationMethod;
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
	
	
	
	public static final int DEVICE_NAME = 0;
	public static final int PANEL_NAME = 1;
	public static final int PANEL_CONTACT = 2;
	public static final int PANEL_TEL = 3;
	public static final int PANEL_MOBILE = 4;
	public static final int PANEL_PASSCODE = 5;
	
	
	//Dialog title
	private String title;
	
	//Dialog type see finals for detail
	private int type = -1;
	
	//hint for user input
	private String hint;
	
	//dialog listener for callback
	private NoticeDialogListener mListener= null;
	
	//EditText for user input
	private EditText inputEditText = null;
	
	public interface NoticeDialogListener{
		public void setInformation(String userInput, int type);
		
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
		inputEditText.setText(hint == null? "User input:" : hint);
		inputEditText.setSelection(hint.length());
		
		//set dialog view
		builder.setView(dialogView);
		
		//set title and buttons
		builder.setMessage(getTitle())
				.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
			
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						//pass location back to activity via callback
						
						mListener.setInformation(inputEditText.getText().toString(),type);
						
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
			case DEVICE_NAME: title = "Name Device";
					break;
			case PANEL_NAME: title = "Name Panel";
					break;
			case PANEL_CONTACT: title = "Enter Contact";
					break;
			case PANEL_TEL: title = "Enter Telphone Number";
					inputEditText.setInputType(InputType.TYPE_CLASS_PHONE);
					break;
			case PANEL_MOBILE: title = "Enter Mobile Number";
					inputEditText.setInputType(InputType.TYPE_CLASS_PHONE);
					break;
			case PANEL_PASSCODE: title = "Enter Passcode";
					
					//set filter for EditText
					
					//Max length
					InputFilter[] filters = {new InputFilter.LengthFilter(Constants.PASSCODE_MAX)};  
					inputEditText.setFilters(filters);
					
					//input type is number only
					inputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
					
					//setup password mask
					inputEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
					
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
