package weiyuan.models;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Panel {
	
	private static final double FLASH_MEMORY = 7549747; // 90% of 8M bytes (8288608 bits)
	
	private Loop loop1;
	
	
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
	
	private int deviceNumber;
	

	public Panel(List<List<Integer>> eepRom, List<List<Integer>> deviceList) throws UnsupportedEncodingException
	{
		
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
	
		
		this.passcode = String.valueOf(eepRom.get(51).get(0) * 256 + eepRom.get(51).get(1));
		
		long reportUsage = eepRom.get(15).get(3) + eepRom.get(15).get(4) * 256 + eepRom.get(15).get(5) * 65536 + 
				eepRom.get(15).get(6) * 16777216L;
		
		this.reportUsage = (new DecimalFormat("#.#####").format(reportUsage / FLASH_MEMORY) +"%"); // update report usage
		
		System.out.println("================Panel Info========================");
		System.out.println(this.toString());
		
		loop1 = new Loop(deviceList);
		
		
		
		
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
		
		
		return deviceNumber;
	}

	
	public Loop getLoop() {
		return loop1;
	}


	
	
	
	
	
}
