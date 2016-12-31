package ID3;

public class RetType{
	private double eS;
	private double eSP;
	private double eSN;
	private int countP;
	private int countN;
	
	public RetType(double eS, double eSP,double eSN, int countP, int countN) {
		this.eS = eS;
		this.eSP = eSP;
		this.eSN = eSN;
		this.countP = countP;
		this.countN = countN;
	}

	public double geteS() {
		return eS;
	}

	public void seteS(double eS) {
		this.eS = eS;
	}

	public double geteSP() {
		return eSP;
	}

	public void seteSP(double eSP) {
		this.eSP = eSP;
	}

	public double geteSN() {
		return eSN;
	}

	public void seteSN(double eSN) {
		this.eSN = eSN;
	}

	public int getCountP() {
		return countP;
	}

	public void setCountP(int countP) {
		this.countP = countP;
	}

	public int getCountN() {
		return countN;
	}

	public void setCountN(int countN) {
		this.countN = countN;
	}
	
}