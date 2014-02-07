package weiyuan.util;

import java.util.ArrayList;
import java.util.List;

public class DataParser {
	
	
	//panel stop and new line byte
	static final int UART_STOP_BIT_H = 0x5A;
	static final int UART_STOP_BIT_L = 0xA5;
	static final int UART_NEW_LINE_H = 0x0D;
	static final int UART_NEW_LINE_L = 0x0A;
	
	static public List<List<Integer>> removeJunkBytes(List<Integer> rxBuffer)
	{
		
			List<List<Integer>> panelData = new ArrayList<List<Integer>>();
			
			System.out.println("==========Removing junk bytes=======");
			System.out.println("rxBuffer.size = " + rxBuffer.size());
			
			for (int i=0,j=3; i<rxBuffer.size()-24; i++)
			{
				if((rxBuffer.get(i) == UART_NEW_LINE_L) && 
        				rxBuffer.get(i - 1)==(UART_NEW_LINE_H) &&
        				rxBuffer.get(i - 2)==(UART_STOP_BIT_L) &&
        				rxBuffer.get(i - 3)==(UART_STOP_BIT_H))
				{
						
					panelData.add(rxBuffer.subList(j, i-5));	
					j = i+4;
				}
				
				
			}
			
			/*System.out.println(parsedData.size());
			
			for(int i=0; i <parsedData.size(); i++)
			{
				System.out.println("parsedDataList " + i + " size: " + parsedData.get(i).size() );
				System.out.println("parsedData: " + parsedData.get(i));
			}*/
			
			return panelData;
	}
	
	public static List<List<Integer>> getEepRom(List<List<Integer>> parsedData)
	{
		List<List<Integer>> eepRom = new ArrayList<List<Integer>>();
		
		for(int i=0; i<16 ;i++)
		{
			List<Integer> list = (List<Integer>) parsedData.get(i);
			for(int j=0;j<list.size();j+=32)
			{
				eepRom.add(list.subList(j, j+32));
				
			}
			
			
		}
		
		System.out.println("EEPROM size: " + eepRom.size());
		return eepRom;
		
	}
	
	
	public static List<List<List<Integer>>> getDeviceList(List<List<Integer>> parsedData)
	{
		List<List<List<Integer>>> deviceList = new ArrayList<List<List<Integer>>>();
		List<List<Integer>> loop1= new ArrayList<List<Integer>>();
		List<List<Integer>> loop2= new ArrayList<List<Integer>>();
		
		deviceList.add(loop1);
		deviceList.add(loop2);
		
		
		
		for(int i=16; i<parsedData.size() ;i++)
		{			
			int address = parsedData.get(i).get(0) / 100;
			
			if(address == 0)
			{
				loop1.add(parsedData.get(i));	
			}
			else loop2.add(parsedData.get(i));
		}
		
		
		
		System.out.println("Device Number: " + (deviceList.get(0).size()+deviceList.get(1).size()));
		return deviceList;
		
		
	}
}




