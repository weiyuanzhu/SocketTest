package weiyuan.models;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;


/* created by weiyuan
 * 05 Feb 2014
 * 
 */
 
public class Loop  implements Parcelable {
	
	
	private List<Device> deviceList;
	
	private int deviceNumber; 
	
	
	
	public Loop()
	{
		deviceList = (List<Device>) new ArrayList<Device>();
		deviceNumber = 0;
	}
	

	private Loop(Parcel source) {
		this();
		readFromParcel(source);
        
    }
	
	
	
	public Loop(List<List<Integer>> dl){
		
		deviceList = (List<Device>) new ArrayList<Device>();
		
		for(int i =0; i<dl.size(); i++)
		{
			Device d = new Device(dl.get(i));
			deviceList.add(d);
			System.out.println(d.toString());
	
		}		
		
		deviceNumber = deviceList.size();;
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
	}

	public void readFromParcel(Parcel source)
	{
		source.readTypedList(deviceList,Device.CREATOR);
		deviceNumber =  source.readInt();
		
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
	
	
	



	
	
}

