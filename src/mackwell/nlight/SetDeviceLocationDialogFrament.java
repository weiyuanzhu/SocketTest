package mackwell.nlight;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class SetDeviceLocationDialogFrament extends DialogFragment{

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		//create alertdialog builder
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setMessage("test")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
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
