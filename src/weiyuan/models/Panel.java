package weiyuan.models;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.List;

public class Panel {
	
	
	private String panelLocation;
	private String contact;
	private String tel;
	private String mobile;
	private String version;
	private String id;
	
	private Long serialNumber;
	private BigInteger gtin;
	

	public Panel(List<List<Integer>> eepRom) throws UnsupportedEncodingException
	{
		this.panelLocation = new String(getBytes(eepRom.get(60)),"UTF-8");
		this.contact = new String(getBytes(eepRom.get(61)),"UTF-8");
		this.tel = new String(getBytes(eepRom.get(62)),"UTF-8");
		this.mobile = new String(getBytes(eepRom.get(63)),"UTF-8");
		this.version = new String(getBytes(eepRom.get(13)),"UTF-8");
		
		this.serialNumber = eepRom.get(3).get(9) + eepRom.get(3).get(9) * 256 + eepRom.get(3).get(9) * 65536 + 
				eepRom.get(3).get(9) * 16777216L;
		
		this.gtin = BigInteger.valueOf(eepRom.get(3).get(9) + eepRom.get(3).get(9) * 256 + 
				eepRom.get(3).get(9) * 65536 + eepRom.get(3).get(9) * 16777216L + 
				eepRom.get(3).get(9) * 4294967296L + eepRom.get(3).get(9) * 1099511627776L);

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
		String description = "\nLocation: " + panelLocation + "\ncontact: " + contact + "\nTel: " + tel + "\nSerialNumber: " + serialNumber + "\nGTIN: " + gtin + "\nVersion: " + version;
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
	
	
	
}
