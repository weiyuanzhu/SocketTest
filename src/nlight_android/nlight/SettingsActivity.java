package nlight_android.nlight;

import com.example.nclient.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SettingsActivity extends Activity {
	
	public static final String SAVE_PANEL_LOCATION = "pref_key_panel_save";
	public static final String SAVE_CHECKED = "pref_key_save_checked";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new SettingsFragment())
        .commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

}
