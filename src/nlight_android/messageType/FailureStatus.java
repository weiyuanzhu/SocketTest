package nlight_android.messageType;

public enum FailureStatus implements BitField{
	CIRCUIT_FAILURE("Circuit Failed"),
	BATTERY_DURATION_FAILURE("Battery Duration"),
	BATTERY_FAILURE("Battery Failed"),
	LAMP_FAILURE("Lamp Failed"),
	FUNCTION_TEST_MAXIMUM_DELAY_EXCEEDED("FT Max"), // device is unable to perform the function test within the maximum timeout period
	DURATION_TEST_MAXIMUM_DELAY_EXCEEDED("DT Max"),
	FUNCTION_TEST_FAILURE("FT Failed"),
	DURATION_TEST_FAILURE("DT Failed");
	
	private final int flag;
	private final String description;
	
	FailureStatus(String des) {
		this.flag = 1 << this.ordinal();
		this.description = des;
	}
	
	@Override
	public int getFlag() {
		return flag;
	}
	
	public String getDescription()
	{
		return this.description;
		
	}
	
	@Override
	public String toString() {
		// capitalise first character
		return super.toString().charAt(0) + 
				super.toString().toLowerCase().replaceAll("_", " ").substring(1, super.toString().length()); 
	}
}
