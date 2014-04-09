package weiyuan.models;




import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.EnumSet;
import java.util.List;

import messageType.EmergencyMode;
import messageType.EmergencyModeFlag;
import messageType.EmergencyStatus;
import messageType.EmergencyStatusFlag;
import messageType.FailureStatus;
import messageType.FailureStatusFlag;

import weiyuan.util.Constants;

import android.os.Parcel;
import android.os.Parcelable;

public class Device  implements Parcelable{
	//05 Feb 2014
	
	
	private int address;
	

	private String location;
	private int failureStatus;
	private boolean communicationStatus; // false : communication lost / true : communication ok
	private int emergencyStatus;
	private int emergencyMode;
	private int battery;
	private long serialNumber;
	private BigInteger GTIN;
	private int[] gtinArray;
	private int dtTime;
	private int lampOnTime;
	private int lampEmergencyTime;
	private int feature;
	
	
	
	public Device(Parcel source) {
		this();
		readFromParcel(source);
	}
	
	public Device()
	{	
		address = 0;
		location = "test";
		failureStatus = 0;
		communicationStatus = true; 
		emergencyStatus = 0;
		emergencyMode = 0;
		battery = 254;
		serialNumber = 0;
		GTIN = BigInteger.valueOf(0);
		dtTime = 0;
		lampOnTime = 0;
		lampEmergencyTime = 0;
		feature = 0;
		gtinArray = new int[]{0,0,0,0,0,0};
	}
	
	public Device(int add,String loc,int fs,int es,int em,int bat,long sn,int[] gtin)
	{
		address = add;
		location = loc;
		failureStatus = fs;
		emergencyStatus = es;
		emergencyMode = em;
		battery = bat;
		serialNumber = sn;
		dtTime = 0;
		lampOnTime = 0;
		lampEmergencyTime = 0;
		feature = 0;
		gtinArray = gtin;
		
	}
	
	public Device(List<Integer> device,List<List<Integer>> eepRom)
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
		location = getDeviceLocation(device,eepRom);
		
		gtinArray = new int[]{0,0,0,0,0,0};
		for(int i=0; i< gtinArray.length; i++)
		{
			int temp = 15-i;
			gtinArray[i] = device.get(temp);
		}
	}

	public void updateDevice(List<Integer> device)
	{
		failureStatus = device.get(4);
		communicationStatus = device.get(5) == 0 ? true : false;
		emergencyStatus = device.get(6);
		emergencyMode = device.get(7);
		battery = device.get(8);		
		dtTime = device.get(19)*2;
		lampOnTime = device.get(20);
		lampEmergencyTime = device.get(21);
		feature = device.get(22);
		
		
		
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
		dest.writeString(location);
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
		dest.writeIntArray(gtinArray);
		
		
		
		
	}
	
	public void readFromParcel(Parcel source)
	{
		address = source.readInt();
		location = source.readString();
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
		source.readIntArray(gtinArray);
	

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
	
	public String getLocation() {
		return location.replaceAll("\\s+$", "");
	}
	
	

	public void setLocation(String location) {
		this.location = location;
	}

	public int getFailureStatus() {
		return failureStatus;
	}
	
	public void setFailureStatus(int failureStatus) {
		this.failureStatus = failureStatus;
	}

	public String getFailureStatusText() {
		StringBuilder sb = new StringBuilder();
		
		EnumSet<FailureStatus> fsSet = new FailureStatusFlag().getFlagStatus(failureStatus);
		
		if (fsSet.size()==0)
		{
			return "All OK";
		}
		else{
			for(FailureStatus fs : fsSet)
			{
				sb.append(fs.getDescription()+" , ");
			}
			System.out.println(sb);
			
			//trim last ","
			sb.deleteCharAt(sb.length()-2);
			return sb.toString();
		}
	}

	public boolean isCommunicationStatus() {
		return communicationStatus;
	}

	public int getEmergencyStatus() {
		return emergencyStatus;
	}
	
	public String getEmergencyStatusText() {
		StringBuilder sb = new StringBuilder();
		
		EnumSet<EmergencyStatus> esSet = new EmergencyStatusFlag().getFlagStatus(emergencyStatus);
		
		if (esSet.size()==0)
		{
			return "-";
		}
		else{
			for(EmergencyStatus es : esSet)
			{
				sb.append(es.getDescription()+" , ");
			}
			System.out.println(sb);
			sb.deleteCharAt(sb.length()-2);
			return sb.toString();
		}
	}

	public int getEmergencyMode() {
		return emergencyMode;
	}
	
	public String getEmergencyModeText() {
		StringBuilder sb = new StringBuilder();
		
		EnumSet<EmergencyMode> emSet = new EmergencyModeFlag().getFlagStatus(emergencyMode);
		
		if (emSet.size()==0)
		{
			return "Normal Mode";
		}
		else{
			for(EmergencyMode em : emSet)
			{
				sb.append(em.getDescription()+" , ");
			}
			System.out.println(sb);
			sb.deleteCharAt(sb.length()-2);
			return sb.toString();
		}
	}

	public int getBattery() {
		return battery;
	}
	
	public String getBatteryLevel()
	{
		DecimalFormat df = new DecimalFormat("#.0");
		double bat = (double) battery/254*100;
		
		switch(battery)
		{
			case 254:

				return "100 %";
			case 0:
				return "0 %";
			default: return df.format(bat) + " %";

		}
	
		
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
	
	public String getLampEmergencyTimeText()
	{
		
		int i = getLampEmergencyTime()/60 + 1;
		
		String str = "Less than " + i + " hours";
		
		return str;
		
	}

	public int getFeature() {
		return feature;
	}
	
	

	public int[] getGtinArray() {
		return gtinArray;
	}

	public void setGTIN(BigInteger gTIN) {
		GTIN = gTIN;
	}
	
	private String getDeviceLocation(List<Integer> device,List<List<Integer>> eepRom)
	{
		String location = null;
		
		int deviceNameLocation = (device.get(0) & Constants.LOOP_ID) == Constants.LOOP_ID ? 
				(device.get(0) & Constants.DEVICE_ID) + 64 : device.get(0);
		
		int eepRomLocation = deviceNameLocation + Constants.LOOP_ID;

		byte[] deviceLocationArray = getBytes(eepRom.get(eepRomLocation));
		try {
			location = new String(deviceLocationArray,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return location;

		
	}
	
	private byte[] getBytes(List<Integer> list)
	{
		byte[] temp = new byte[list.size()];
		
		for (int i=0; i<list.size(); i++)
		{
			temp[i] =  list.get(i).byteValue();
			//System.out.print(temp[i]);
		}
		return temp;
	}

	//generic method for get status text
	
	//todo
	
	/*public <T>  String  test(T t)
	{
		
		t.getClass().getName();
		StringBuilder sb = new StringBuilder();
		
		EnumSet<t> emSet = new EmergencyModeFlag().getFlagStatus(i);
		
		if (emSet.size()==0)
		{
			return "All OK";
		}
		else{
			for(Enum a : emSet)
			{
				sb.append(((EmergencyMode) a).getDescription()+" , ");
			}
			System.out.println(sb);
			sb.deleteCharAt(sb.length()-2);
			return sb.toString();
		
		 return null;
	}*/

}
