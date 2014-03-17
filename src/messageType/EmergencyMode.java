package messageType;

public enum EmergencyMode implements BitField{
	REST_MODE,
	NORMAL_MODE,
	EMERGENCY_MODE,
	EXTENDED_EMERGENCY_MODE,
	FUNCTION_TEST_IN_PROGRESS,
	DURATION_TEST_IN_PROGRESS,
	HARDWIRED_INHIBIT,
	HARDWIRED_SWITCH_ON;
	
	private final int flag;
	
	EmergencyMode() {
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