package messageType;

public enum EmergencyStatus implements BitField{
	INHIBIT_MODE,
	FUNCTION_TEST_DONE,
	DURATION_TEST_DONE,
	BATTERY_FULLY_CHARGED,
	FUNCTION_TEST_PENDING,
	DURATION_TEST_PENDING,
	IDENTIFICATION_ACTIVE,
	PHYSICALLY_SELECTED;
	
	private final int flag;
	
	EmergencyStatus() {
		this.flag = 1 << this.ordinal();
	}
	
	@Override
	public int getFlag() {
		return flag;
	}
	
	@Override
	public String toString() {
		// capitalise first character
				return super.toString().charAt(0) + 
						super.toString().toLowerCase().replaceAll("_", " ").substring(1, super.toString().length()); 
	}
}
