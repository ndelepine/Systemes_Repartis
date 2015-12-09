package systeme;

public class Interval {
	int lowerBound;
	int upperBound;
	
	public Interval(int lowrBound, int upperBound) {
		super();
		this.lowerBound = lowrBound;
		this.upperBound = upperBound;
	}
	
	public int getLowerBound() {
		return lowerBound;
	}
	public void setLowerBound(int lowrBound) {
		this.lowerBound = lowrBound;
	}
	public int getUpperBound() {
		return upperBound;
	}
	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
	}

	@Override
	public String toString() {
		return "Interval [lowerBound=" + lowerBound + ", upperBound="
				+ upperBound + "]";
	}
	
	
}
