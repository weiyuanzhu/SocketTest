package weiyuan.models;

import java.util.ArrayList;
import java.util.List;

/* created by weiyuan
 * 05 Feb 2014
 * 
 */

public class Loop {
	
	private int deviceNumber; 
	
	private List<Device> deviceList;
	
	
	
	
	
	public Loop(List<List<Integer>> dl){
		
		deviceList = (List<Device>) new ArrayList<Device>();
		
		for(List<Integer> d : dl)
		{
			deviceList.add(new Device(d));
			
		}		
	}



	public int getDeviceNumber() {
		return deviceList.size();
	}
	
	
	
}

