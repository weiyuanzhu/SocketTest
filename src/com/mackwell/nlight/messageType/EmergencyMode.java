package com.mackwell.nlight.messageType;

public enum EmergencyMode implements BitField{
	REST_MODE("Rest Mode"),
	NORMAL_MODE("Normal Mode"),
	EMERGENCY_MODE("Emergency Mode"),
	EXTENDED_EMERGENCY_MODE("Extended Emergency Mode"),
	FUNCTION_TEST_IN_PROGRESS("FT in Progress"),
	DURATION_TEST_IN_PROGRESS("DT in Progress"),
	HARDWIRED_INHIBIT("Hardwired Inhibited"),
	HARDWIRED_SWITCH_ON("Hardwire switch is on");
	
	private final int flag;
	private String description;
	
	EmergencyMode(String des) {
		this.flag = 1 << this.ordinal();
		this.description = des;
	}
	
	@Override
	public int getFlag() {
		return flag;
	}

	
	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		// capitalise first character
				return super.toString().charAt(0) + 
						super.toString().toLowerCase().replaceAll("_", " ").substring(1, super.toString().length()); 
	}
}