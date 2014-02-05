package weiyuan.models;

import java.math.BigInteger;
import java.util.List;

public class Device {
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
	private String name;
	
	
	public Device(List<Integer> device){
		
		address = device.get(0);
		failureStatus = device.get(1);
		communicationStatus = device.get(2) == 0 ? true : false;
		emergencyStatus = device.get(3);
		emergencyMode = device.get(4);
		battery = device.get(5);
		
		serialNumber = device.get(9) + device.get(8) * 256 + device.get(7)* 65536 + device.get(6)*16777216L;
		
		GTIN = BigInteger.valueOf(device.get(15) + device.get(14) * 256 + device.get(13)*65535 + device.get(12)* 16777216L
									+ device.get(11)* 4294967296L + device.get(10)*1099511627776L);
		
		
		dtTime = device.get(16)*2;
		lampOnTime = device.get(17);
		lampEmergencyTime = device.get(18);
		feature = device.get(19);
		
	}


	@Override
	public String toString() {
		String deviceStr = "Address: " + address + "FS: " + failureStatus ;
		return deviceStr;
		
	}
	
	


}
