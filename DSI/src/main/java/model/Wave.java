package model;

public class Wave {

	private WaveType type;
	private int start;
	private int end;
	private double peak;
	private int cycle;
	
	public Wave(WaveType type, int start, int end, double peak, int cycle) {
		this.type = type;
		this.start = start;
		this.end = end;
		this.peak = peak;
		this.cycle = cycle;
	}

	public WaveType getType() {
		return type;
	}
	
	public void setType(WaveType type) {
		this.type = type;
	}
	
	public int getStart() {
		return start;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
	
	public double getPeak() {
		return peak;
	}
	
	public void setPeak(double peak) {
		this.peak = peak;
	}
	
	public int getCycle() {
		return cycle;
	}
	
	public void setCycle(int cycle) {
		this.cycle = cycle;
	}



}
