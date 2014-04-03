package model;

public class Segment {

	private SegmentType type;
	private int start;
	private int end;
	private int cycle;
	
	public Segment(SegmentType type, int start, int end, int cycle) {
		this.type = type;
		this.start = start;
		this.end = end;
		this.cycle = cycle;
	}
	
	
	public SegmentType getType() {
		return type;
	}
	
	public void setType(SegmentType type) {
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
