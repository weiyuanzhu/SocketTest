package weiyuan.util;

import java.util.ArrayList;
import java.util.List;

public class CrcCheckSum {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		List <Integer> list = new ArrayList<Integer>();
		
		list.add(0x02);
		list.add(0xA5);
		list.add(0x40);
		list.add(0x00);
		list.add(CRC.calcCRC(list, list.size()));
		list.add(CRC.calcCRC(list, list.size()) >> 8);
		
		System.out.println(list);

	}

}
