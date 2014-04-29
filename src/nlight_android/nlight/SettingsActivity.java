package nlight_android.nlight;

import com.example.nclient.R;
import com.example.nclient.R.layout;
import com.example.nclient.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.app.*;
import android.os.*;
import android.view.*;

public class SettingsActivity extends Activity {

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
