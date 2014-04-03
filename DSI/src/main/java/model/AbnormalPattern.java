package model;

public class AbnormalPattern {

	private String name;
	private String symptom;
	private double extraInfo;
	private int cycle;
	
	public AbnormalPattern(String name, String symptom, double extraInfo, int cycle) {
		this.name = name;
		this.symptom = symptom;
		this.extraInfo = extraInfo;
		this.cycle = cycle;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSymptom() {
		return symptom;
	}
	
	public void setSymptom(String symptom) {
		this.symptom = symptom;
	}
	
	public double getExtraInfo() {
		return extraInfo;
	}
	
	public void setExtraInfo(double extraInfo) {
		this.extraInfo = extraInfo;
	}
	
	public int getCycle() {
		return cycle;
	}
	
	public void setCycle(int cycle) {
		this.cycle = cycle;
	}
	
}
