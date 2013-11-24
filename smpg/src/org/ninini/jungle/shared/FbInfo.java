package org.ninini.jungle.shared;

import java.io.Serializable;

public class FbInfo implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String fbId;
	private String name;
	private int rank = Ranking.DEFAULT_RANK;
	private int rd = Ranking.DEFAULT_RD;
	
	public FbInfo(){}
	public FbInfo(String fbId, String name){
		this.fbId = fbId;
		this.name = name;
	}
	
	public String getFbId(){
		return fbId;
	}
	public String getName(){
		return name;
	}
	public int getRank(){
		return rank;
	}
	public int getRd(){
		return rd;
	}
	public String getAvatarUrl(){
		return "http://graph.facebook.com/"+fbId+"/picture";
	}
	
	public void setRankRd(int rank, int rd){
		this.rank = rank;
		this.rd = rd;
	}
}
