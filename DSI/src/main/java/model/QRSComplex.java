package model;

public class QRSComplex {
	
	private int start;
	private int end;
	private int cycle;
	

	public QRSComplex(int start, int end, int cycle) {
		this.start = start;
		this.end = end;
		this.cycle = cycle;
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
