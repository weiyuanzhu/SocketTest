package mackwell.nlight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

import com.example.nclient.R;

public class LoadingScreenActivity extends Activity {
	
	private static int delay = 3000;
	private Handler loadHandler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading_screen);
		
		loadHandler = new Handler();
		loadHandler.postDelayed(load, delay);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.loading_screen, menu);
		return true;
	}
	
	Runnable load = new Runnable()
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			Intent i = new Intent(LoadingScreenActivity.this, PanelListActivity.class);
			startActivity(i);
			
			finish();
			
		}
		
		
	};

}
