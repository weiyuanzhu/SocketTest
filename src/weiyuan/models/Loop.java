package weiyuan.models;

import java.util.ArrayList;
import java.util.List;

import weiyuan.util.Constants;

import android.os.Parcel;
import android.os.Parcelable;


/* created by weiyuan
 * 05 Feb 2014
 * 
 */
 
public class Loop  implements Parcelable {
	
	
	private List<Device> deviceList;
	
	private int status;
	private int deviceNumber; 
	private String loopName;
	
	
	
	public Loop()
	{
		deviceList = (List<Device>) new ArrayList<Device>();
		deviceNumber = 0;
		
		
		setStatus(0);
		loopName = "Loop";
	
	}
	
	public Loop(String name)
	{
		deviceList = (List<Device>) new ArrayList<Device>();
		deviceNumber = 0;
		setStatus(0);
		
		int[] gtin = new int[] {0,1,2,3,4,5};
		deviceList.add(new Device(0,"test1",0,0,0,100,1375167879,gtin));
		deviceList.add(new Device(1,"test2",0,0,0,100,1294967295,gtin));
		deviceList.add(new Device(2,"test3",0,0,0,100,1424967295,gtin));
		loopName = name;
		
	}

	private Loop(Parcel source) {
		this();
		readFromParcel(source);
        
    }
	
	
	
	public Loop(List<List<Integer>> dl,List<List<Integer>> eepRom, String loopName){
		
		deviceList = (List<Device>) new ArrayList<Device>();
		
		for(int i =0; i<dl.size(); i++)
		{
			Device d = new Device(dl.get(i),eepRom);
			deviceList.add(d);
			if(d.getFailureStatus()!=0) 
			{
				setStatus(1);
				
			}
			System.out.println(d.toString());
	
		}		
		
		deviceNumber = deviceList.size();;
	}
	
	



	public List<Device> getDeviceList() {
		return deviceList;
	}


	public int getDeviceNumber() {
		return deviceNumber;
	}



	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
		dest.writeTypedList(deviceList);
		dest.writeInt(deviceNumber);
		dest.writeInt(status);
		dest.writeString(loopName);
	}

	public void readFromParcel(Parcel source)
	{
		source.readTypedList(deviceList,Device.CREATOR);
		deviceNumber =  source.readInt();
		status = source.readInt();
		loopName = source.readString();
		
	}

	public static final Parcelable.Creator<Loop> CREATOR = new Parcelable.Creator<Loop>() {

		@Override
		public Loop createFromParcel(Parcel source) {
			
			return new Loop(source);
		}

		@Override
		public Loop[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}
		
		
	};

	
	public Device getDevice(int i)
	{
		return deviceList.get(i);
		
		
	}
	
	public void addDevice(Device device){
		deviceList.add(device);
		deviceNumber ++;
		
	}


	public int getStatus() {
		status = Constants.ALL_OK;
		
		for(Device d: deviceList)
		{
			if (d.getFailureStatus()!=0)
			{
				status = Constants.FAULT;
			}
			
		}
		
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
		
		for(Device d: deviceList)
		{
			d.setFailureStatus(status);
		}
	}

	public String getLoopName() {
		return loopName;
	}

	public void setLoopName(String loopName) {
		this.loopName = loopName;
	};
	
	
	
	



	
	
}

