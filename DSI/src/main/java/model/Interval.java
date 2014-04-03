package model;

public class Interval {

	private IntervalType type;
	private int start;
	private int end;
	private int cycle;
	
	public Interval(IntervalType type, int start, int end, int cycle) {
		this.type = type;
		this.start = start;
		this.end = end;
		this.cycle = cycle;
	}
	
	
	public IntervalType getType() {
		return type;
	}
	
	public void setType(IntervalType type) {
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
	
	public int getCycle() {
		return cycle;
	}
	
	public void setCycle(int cycle) {
		this.cycle = cycle;
	}
}
