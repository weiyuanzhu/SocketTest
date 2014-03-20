package mackwell.nlight;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.nclient.R;

/**
 * @author weiyuan zhu
 *
 */
public class SetDeviceLocationDialogFrament extends DialogFragment{
	
	private EditText locationEditText = null;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		//create alertdialog builder
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		//get inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		//create final view with dialog layout
		final View dialogView = inflater.inflate(R.layout.dialog_setdevice_name, null);
		
		//set dialog view
		builder.setView(dialogView);
		
		//set title and buttons
		builder.setMessage("Set Device Location")
				.setPositiveButton("Set", new DialogInterface.OnClickListener() {
			
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						locationEditText = (EditText) dialogView.findViewById(R.id.device_dialog_location);
						System.out.println(locationEditText.getText().toString());
						
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
	
	
	
	
	
	

}
