package weiyuan.models;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Panel  implements Parcelable{
	
	private static final double FLASH_MEMORY = 7549747; // 90% of 8M bytes (8288608 bits)
	private static final int ALL_OK = 0;
	private static final int FAULT = 1;
	
	private Loop loop1;
	private Loop loop2;
	
	private String ip;
	
	private String panelLocation;
	private String contact;
	private String tel;
	private String mobile;
	private String version;
	private String id;
	private String passcode;
	

	private String reportUsage;
	
	private Long serialNumber;
	private BigInteger gtin;
	private int[] gtinArray;
	
	private int deviceNumber;
	
	private int overAllStatus;
	
	public Panel()
	{
		//init 
		gtinArray = new int[6]; 
	}

	public Panel(String ip)

	{
		setIp(ip);
		panelLocation = "Mackwell L&B Demo";
		contact = "Mackwell Engineer";
		loop1 = new Loop();
		loop2 = new Loop();
		tel = "01922 458 255";
		mobile = "0742600000";
		version = "Firmware test";
		id = "test";
		passcode= "1111";
		reportUsage = "1%";
		serialNumber = (long) 1234567;
		gtinArray = new int[]{1,2,3,4,5,6};
		overAllStatus = 0;
		
		if(loop1.getStatus()!=0 || loop2.getStatus()!=0)
		{
			overAllStatus = FAULT;
			
		}
		else overAllStatus = ALL_OK;
		
		
	}

	public Panel(Parcel source)
	{
		this();
		readFromParcel(source);
	}

	public Panel(List<List<Integer>> eepRom, List<List<List<Integer>>> deviceList, String ip) throws UnsupportedEncodingException
	{
		gtinArray = new int[6];
		
		this.ip = ip;
		
		this.panelLocation = new String(getBytes(eepRom.get(60)),"UTF-8");
		
		String con = new String(getBytes(eepRom.get(61)),"UTF-8");
		this.contact = con.contains("?")? "-" : con;
		
		
		String tel = new String(getBytes(eepRom.get(62)),"UTF-8");
		this.tel = tel.contains("?")? "-" : tel;
		
		String mob = new String(getBytes(eepRom.get(63)),"UTF-8");
		this.mobile = mob.contains("?")? "-" : mob;
		
		
		this.version = new String(getBytes(eepRom.get(13)),0,16,"UTF-8");
		
		this.serialNumber = eepRom.get(3).get(9) + eepRom.get(3).get(8) * 256 + eepRom.get(3).get(7) * 65536 + 
				eepRom.get(3).get(6) * 16777216L;
		
		this.gtin = BigInteger.valueOf(eepRom.get(3).get(5) + eepRom.get(3).get(4) * 256 + 
				eepRom.get(3).get(3) * 65536 + eepRom.get(3).get(2) * 16777216L + 
				eepRom.get(3).get(1) * 4294967296L + eepRom.get(3).get(0) * 1099511627776L);
		
		this.gtinArray = new int[]{0,0,0,0,0,0};
		for(int i=0; i< gtinArray.length; i++)
		{
			int temp = 5-i;
			gtinArray[i] = eepRom.get(3).get(temp);
		}
		
		
		this.passcode = String.valueOf(eepRom.get(51).get(0) * 256 + eepRom.get(51).get(1));
		
		long reportUsage = eepRom.get(15).get(3) + eepRom.get(15).get(4) * 256 + eepRom.get(15).get(5) * 65536 + 
				eepRom.get(15).get(6) * 16777216L;
		
		this.reportUsage = (new DecimalFormat("#.#####").format(reportUsage / FLASH_MEMORY) +"%"); // update report usage
		
		System.out.println("================Panel Info========================");
		System.out.println(this.toString());
		
		loop1 = new Loop(deviceList.get(0),eepRom);
		loop2 = new Loop(deviceList.get(1),eepRom);
		
		
		if(loop1.getStatus()!=0 || loop2.getStatus()!=0)
		{
			overAllStatus = FAULT;
			
		}else overAllStatus = ALL_OK;
		
	}
	
	public static final Parcelable.Creator<Panel> CREATOR = new Parcelable.Creator<Panel>(){

		@Override
		public Panel createFromParcel(Parcel source) {

			return new Panel(source);
		}

		@Override
		public Panel[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}
		
	};
	
	
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
	
	public String toString()
	{
		String description = "\nLocation: " + panelLocation + "\ncontact: " + contact + "\nTel: " + tel + "\nSerialNumber: " + serialNumber + "\nGTIN: " + gtin + "\nVersion: " + version
				+ "\nReport Usage: " + reportUsage + "\nPasscode: " + passcode;
		return description;
			
	}


	//getters
	
	public String getPanelLocation() {
		return panelLocation;
	}


	public String getContact() {
		return contact;
	}


	public String getTel() {
		return tel;
	}


	public String getMobile() {
		return mobile;
	}


	public String getVersion() {
		return version;
	}


	public String getId() {
		return id;
	}


	public Long getSerialNumber() {
		return serialNumber;
	}


	public BigInteger getGtin() {
		return gtin;
	}

	public void setGtin(BigInteger gtin) {
		this.gtin = gtin;
	}

	public String getPasscode() {
		return passcode;
	}


	public String getReportUsage() {
		return reportUsage;
	}


	public int getDeviceNumber() {
		
		if(loop1 != null)
		{
				deviceNumber += loop1.getDeviceNumber();		
		}
		
		if(loop2 != null)
		{
			deviceNumber += loop2.getDeviceNumber();
		}
				
		return deviceNumber;
	}

	
	
	public int[] getGtinArray() {
		return gtinArray;
	}

	public Loop getLoop1() {
		return loop1;
	}

	public Loop getLoop2() {
		return loop2;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeValue(loop1);
		dest.writeValue(loop2);
		
		dest.writeString(ip);
		dest.writeString(panelLocation);
		dest.writeString(contact);
		dest.writeString(tel);
		dest.writeString(mobile);
		dest.writeString(version);
		dest.writeString(id);
		dest.writeString(passcode);
		dest.writeString(reportUsage);
		
		
		dest.writeLong(serialNumber);
		dest.writeIntArray(gtinArray);
		dest.writeInt(overAllStatus);
	}
	
	public void readFromParcel(Parcel source)
	{
		
		loop1 = (Loop) source.readValue(Loop.class.getClassLoader());
		loop2 = (Loop) source.readValue(Loop.class.getClassLoader());
		
		ip = source.readString();
		panelLocation = source.readString();
		contact = source.readString();
		tel  = source.readString();
		mobile  = source.readString();
		version  = source.readString();
		id  = source.readString();
		passcode  = source.readString();
		reportUsage = source.readString();
		
		serialNumber = source.readLong();
		
		source.readIntArray(gtinArray);
		overAllStatus = source.readInt();

	
	}

	public int getOverAllStatus() {
		return overAllStatus;
	}


	
	
	
	
	
}
