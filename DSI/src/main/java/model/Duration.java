package model;

public class Duration {
	
	private int duration;
	private DurationType type;
	
	public Duration(int duration, DurationType type) {
		this.duration = duration;
		this.type = type;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public DurationType getType() {
		return type;
	}

	public void setType(DurationType type) {
		this.type = type;
	}
	
	
	
	

}
