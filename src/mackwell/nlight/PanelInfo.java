package mackwell.nlight;

import java.util.ArrayList;
import java.util.List;

import weiyuan.socket.GetCmd;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;

import com.example.nclient.R;

public class PanelInfo extends Activity {
	
	
	
	private GetCmd getCmd = null;
	private Handler myHandler;
	
	private List<Integer> rxBuffer = null;
	private byte[] rx = null;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_panel_info);
		
		//System.out.println("I am running on activity------------" + Thread.currentThread().getName());
		
		rxBuffer = new ArrayList<Integer>();
		
		
		
		
		myHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				System.out.println("Back to Main thread -------------------" + Thread.currentThread().getName());
				//System.out.println("rx.length " + rx.length);
				
				//parse(rxBuffer);
				//rxBuffer.clear();
			}
			
		};		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.panel_info, menu);
		return true;
	}
	
	Runnable test = new Runnable(){
		
		public void run(){
			
			getCmd = new GetCmd();
			
			char[] getConfig = new char[] {0x02,0xA0,0x21,0x68,0x18,0x5A,0xA5,0x0D,0x0A};
			
			char[] getPackageTest = new char[] {2, 165, 64, 15, 96, 0,0x5A,0xA5,0x0D,0x0A};

			
				rxBuffer.clear();
				System.out.println("\n------------Retriving panal info----------");
				rxBuffer = getCmd.getCMD("192.168.1.23",getPackageTest);
				System.out.println("\nrxBuffer.size = " + rxBuffer.size());
			
				
					
			Message msg = myHandler.obtainMessage();
			myHandler.sendMessage(msg);
		}
	};

	
	private void parse(List<Integer> rx){
		
		//System.out.println("parsing--------------------------");
		
		
	}
	
	public void getPanelInfo(View v)
	{
		new Thread(test).start();
		
	}
	
	public static void recall()
	{
		System.out.println("recall Test");
		
		
	}
}
