package weiyuan.models;




import java.math.BigInteger;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Device  implements Parcelable{
	//05 Feb 2014
	
	
	private int address;
	private int failureStatus;
	private boolean communicationStatus; // false : communication lost / true : communication ok
	private int emergencyStatus;
	private int emergencyMode;
	private int battery;
	private long serialNumber;
	private BigInteger GTIN;
	private int dtTime;
	private int lampOnTime;
	private int lampEmergencyTime;
	private int feature;
	//private String name;
	
	
	public Device(Parcel source) {
		this();
		readFromParcel(source);
	}
	
	public Device()
	{	
		address = 0;
		failureStatus = 0;
		communicationStatus = true; 
		emergencyStatus = 0;
		emergencyMode = 0;
		battery = 0;
		serialNumber = 0;
		GTIN = BigInteger.valueOf(0);
		dtTime = 0;
		lampOnTime = 0;
		lampEmergencyTime = 0;
		feature = 0;
	}
	
	public Device(List<Integer> device)
	{
		address = device.get(0);
		failureStatus = device.get(1);
		communicationStatus = device.get(2) == 0 ? true : false;
		emergencyStatus = device.get(3);
		emergencyMode = device.get(4);
		battery = device.get(5);		
		serialNumber = device.get(9) + device.get(8) * 256 + device.get(7)* 65536 + device.get(6)*16777216L;		
		GTIN = BigInteger.valueOf(device.get(15) + device.get(14) * 256 + device.get(13)*65535 + device.get(12)* 16777216L + device.get(11)* 4294967296L + device.get(10)*1099511627776L);
		dtTime = device.get(16)*2;
		lampOnTime = device.get(17);
		lampEmergencyTime = device.get(18);
		feature = device.get(19);
		
	}


	@Override
	public String toString() {
		String deviceStr = "Address: " + address + " FS: " + failureStatus  + " serialNumber: " + serialNumber + " GTIN: " + GTIN ;
		return deviceStr;
		
	}


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeInt(address);
		dest.writeInt(failureStatus);
		dest.writeByte((byte)(communicationStatus ? 1 : 0));
		dest.writeInt(emergencyStatus);
		dest.writeInt(emergencyMode);
		dest.writeInt(battery);
		dest.writeLong(serialNumber);
		dest.writeInt(dtTime);
		dest.writeInt(lampOnTime);
		dest.writeInt(lampEmergencyTime);
		dest.writeInt(feature);
		
		
		
		
	}
	
	public void readFromParcel(Parcel source)
	{
		address = source.readInt();
		failureStatus = source.readInt();
		communicationStatus = source.readByte()!= 0;
		emergencyStatus = source.readInt();
		emergencyMode = source.readInt();
		battery = source.readInt();
		serialNumber = source.readLong();
		dtTime = source.readInt();
		lampOnTime = source.readInt();
		lampEmergencyTime = source.readInt();
		feature = source.readInt();
	

	}
	
	public static final Parcelable.Creator<Device> CREATOR = new Parcelable.Creator<Device>() {

		@Override
		public Device createFromParcel(Parcel source) {
			
			return new Device(source);
		}

		@Override
		public Device[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}
		
		
	};
	
	
	//getters


	public int getAddress() {
		return address;
	}

	public int getFailureStatus() {
		return failureStatus;
	}

	public boolean isCommunicationStatus() {
		return communicationStatus;
	}

	public int getEmergencyStatus() {
		return emergencyStatus;
	}

	public int getEmergencyMode() {
		return emergencyMode;
	}

	public int getBattery() {
		return battery;
	}

	public long getSerialNumber() {
		return serialNumber;
	}

	public BigInteger getGTIN() {
		return GTIN;
	}

	public int getDtTime() {
		return dtTime;
	}

	public int getLampOnTime() {
		return lampOnTime;
	}

	public int getLampEmergencyTime() {
		return lampEmergencyTime;
	}

	public int getFeature() {
		return feature;
	}
	
	


}
