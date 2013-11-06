package org.ninini.jungle.shared;

public class Ranking {
	public static int DEFAULT_RANK = 1500;
	public static int DEFAULT_RD = 350;
	private int rank;
	private int rd;
	
	public Ranking(){
		rank = 1500;
		rd = 350;
	}
	public Ranking(int rank, int rd){
		this.rank = rank;
		this.rd = rd;
	}
	
	public int getRank(){
		return rank;
	}
	public int getRD(){
		return rd;
	}
	
	public void determineRD(Long lastTime, Long thisTime){
		int t = (int)((thisTime - lastTime) / (1000.0 * 60.0 * 60.0 * 24.0));
		//c = sqrt((350^2-50^2)/365)
		double csq = (350.0 * 350.0 - 50.0 * 50.0) / 365.0;
		int newRD = (int)(Math.sqrt(rd*rd+t*csq));
		if (newRD<350) rd = newRD;
		else rd = 350;
	}
	public void determinNewRD(double dSqr){
		//rd=sqrt((1/rd^2+1/d^2)^-1)
		double rdd = Math.sqrt(1.0/(1.0/((double)rd*rd)+1.0/dSqr));
		rd = (int)rdd;
	}
	public void updateRank(int oppoRank, boolean win, Long lastTime, Long thisTime){
		determineRD(lastTime, thisTime);
		//q=ln(10)/400
		double q = Math.log(10)/400;
		//g(RD)=1/sqrt(1+3q^2*RD^2/pi^2)
		double gRD = 1/Math.sqrt(1.0+3.0*q*q*rd*rd/Math.PI*Math.PI);
		//E=1/(1+10^(g(rd)(r-ri)/-400)
		double e=1/(1+Math.pow(10.0, (gRD*(rank-oppoRank))/(-400.0)));
		//d^2=1/(q^2*g^2(RD)E(1-E))
		double dSqr = 1.0/(q*q*gRD*gRD*e*(1.0-e));
		double s=0;
		if (win) s=1.0;
		//r=r0+q/(1/rd^2+1/d^2)*g(RD)(s-E)
		double rr=(double)rank+q/(1.0/(rd*rd)+1.0/dSqr)*gRD*(s-e);
		rank = (int)rr;
		determinNewRD(dSqr);
	}
}
