package weiyuan.messageType;

public enum EmergencyStatus implements BitField{
	INHIBIT_MODE("Inhibit Mode"),
	FUNCTION_TEST_DONE("FT Done"),
	DURATION_TEST_DONE("DT Done"),
	BATTERY_FULLY_CHARGED("Battery Full"),
	FUNCTION_TEST_PENDING("FT Pending"),
	DURATION_TEST_PENDING("DT Pending"),
	IDENTIFICATION_ACTIVE("Identification Activt"),
	PHYSICALLY_SELECTED("Physically Selected");
	
	private final int flag;
	private final String description;
	
	EmergencyStatus(String des) {
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
