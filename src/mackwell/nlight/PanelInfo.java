package mackwell.nlight;

import java.util.ArrayList;
import java.util.List;

import weiyuan.socket.*;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;

import com.example.nclient.R;

public class PanelInfo extends Activity  implements Connection.Delegation{

	//panel stop and new line byte
	static final int UART_STOP_BIT_H = 0x5A;
	static final int UART_STOP_BIT_L = 0xA5;
	static final int UART_NEW_LINE_H = 0x0D;
	static final int UART_NEW_LINE_L = 0x0A;
	
	private Connection connection = null;
	
	private List<Integer> panalData = null; //


	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_panel_info);
		
		//create connector
		panalData = new ArrayList<Integer>();
		connection = new Connection(this);
		
		connection.fetchData();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.panel_info, menu);
		return true;
	}

	
	//function for delegate interface
	@Override
	public void receive(List<Integer> rx) {
		
		panalData = new ArrayList<Integer>(rx);

		connection.setIsClosed(true);
				
		if(this.panalData.size()< 16500)
		{		
			System.out.println("not complete " + this.panalData.size());
			connection.fetchData();			
		}
		else {
			//rxComplete = true;
			//new Thread(parse).start();
		}
		

		System.out.println("Actual bytes received: " + panalData.size());
		
	}
	
	

}
