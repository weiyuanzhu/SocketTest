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
	public static final int ENTER_PASSCODE = 6;
	
	
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
		public void cancel();
		
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
		inputEditText.setSelection(hint == null ? 0: hint.length());
		
		//setup password mask
		//inputEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
		//inputEditText.setHint("Please enter passcode");
		
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
						mListener.cancel();
						
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
			case DEVICE_NAME: title = "Name device";
				inputEditText.setHint("Enter device name");
					break;
			case PANEL_NAME: title = "Name panel";
				inputEditText.setHint("Enter panel name");
					break;
			case PANEL_CONTACT: title = "Update contact name ";
				inputEditText.setHint("Enter contact name");
					break;
			case PANEL_TEL: title = "Update Telphone Number";
					inputEditText.setInputType(InputType.TYPE_CLASS_PHONE);
					break;
			case PANEL_MOBILE: title = "Update Mobile Number";
					inputEditText.setInputType(InputType.TYPE_CLASS_PHONE);
					inputEditText.setHint("Enter mobile number");
					break;
			case PANEL_PASSCODE: title = "Update Passcode";
					
					//set filter for EditText
					
					//Max length
					InputFilter[] filters = {new InputFilter.LengthFilter(Constants.PASSCODE_MAX)};  
					inputEditText.setFilters(filters);
					
					//input type is number only
					inputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
					inputEditText.setHint("Enter 4 digit passcode");
					
					//setup password mask
					inputEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
					
					break;
			case ENTER_PASSCODE: title = "Please enter passcode for the panel";
			
				//set filter for EditText
				
				//Max length
				InputFilter[] filters2 = {new InputFilter.LengthFilter(Constants.PASSCODE_MAX)};  
				inputEditText.setFilters(filters2);
				
				//input type is number only
				inputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
				inputEditText.setText("");
				inputEditText.setHint("Enter 4 digit passcode");
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
