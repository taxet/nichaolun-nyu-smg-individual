package org.ninini.jungle.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.googlecode.objectify.annotation.EmbedMap;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Player implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id String uID;//facebook UID
	String nickname;
	@EmbedMap
	Map<String, Long> friendMatches = new HashMap<String, Long>();
	Set<Long> matches = new HashSet<Long>();
	boolean online;
	long latestUpdate;
	//Ranking
	int rank;
	int rd;
	
	@SuppressWarnings("unused")
	private Player(){}
	
	public Player(String uID, String nickname){
		this.uID = uID;
		this.nickname = nickname;
		online = true;
		latestUpdate = System.currentTimeMillis();
		rank = Ranking.DEFAULT_RANK;
		rd = Ranking.DEFAULT_RD;
	}
	
	public String getUID(){
		return uID;
	}
	public String getNickname(){
		return nickname;
	}
	public Map<String,Long> getFriendMatches(){
		return friendMatches;
	}
	public Set<Long> getMatches(){
		return matches;
	}
	public boolean isOnline(){
		return online;
	}
	public int getRank(){
		return rank;
	}
	public int getRD(){
		return rd;
	}
	
	public void addMatches(Match match){
		matches.add(match.getMatchId());
		String oppoId = "";
		if(match.getRedPlayer().equals(this.uID))
			oppoId = match.getBlackPlayer();
		else oppoId = match.getRedPlayer();
		friendMatches.put(oppoId, match.getMatchId());
	}
	public void matchesOver(Match match){
		String oppoId = "";
		if(match.getRedPlayer().equals(this.uID))
			oppoId = match.getBlackPlayer();
		else oppoId = match.getRedPlayer();
		friendMatches.remove(oppoId);
	}

	public void clearMatches(){
		matches.clear();
	}
	
	public boolean removeFromMatches(Long matchId){
		return matches.remove(matchId);
	}
	public boolean removeFromMatches(Match match){
		return matches.remove(match.getMatchId());
	}
	
	//connected and disconnected
	public void connect(){
		online = true;
	}
	public void disconnect(){
		online = false;
	}
	
	public void setNickName(String newName){
		this.nickname = newName;
	}
	
	//When a player don't do anything in 3 minutes, then consider him/her log out
	public boolean isActivate(){
		return System.currentTimeMillis() - latestUpdate <= 1000*60*3;
	}
	public void update(){
		latestUpdate = System.currentTimeMillis();
	}
	
	public void updateRank(int oppoRank, boolean win, Long thisTime){
		Ranking ranking = new Ranking(rank, rd);
		ranking.updateRank(oppoRank, win, latestUpdate, thisTime);
		rank = ranking.getRank();
		rd = ranking.getRD();
	}

}
