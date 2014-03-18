package mackwell.nlight;

import com.example.nclient.R;
import com.example.nclient.R.layout;
import com.example.nclient.R.menu;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;

/**
 * @author weiyuan zhu
 * a base class for all other activities 
 * to check device connecctivities
 */

public class BaseActivity extends Activity {
	
	
	//protected flags for connections 
	
	// Whether device is connected
	protected static boolean isConnected = false;
	protected static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    protected static boolean mobileConnected = false;
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.base, menu);
		return true;
	}
	

	
	/**
	 * method 
	 */
	protected void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
        	isConnected = true;
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            System.out.println("wifi----------->" + wifiConnected );
            System.out.println("3G----------->" + mobileConnected);
        } else {
            wifiConnected = false;
            mobileConnected = false;
            isConnected = false;
            System.out.println("not connected");
        }

    }

}
