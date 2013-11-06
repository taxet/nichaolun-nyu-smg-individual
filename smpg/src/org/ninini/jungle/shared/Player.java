package org.ninini.jungle.shared;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Player implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id String email;
	String nickname;
	Set<String> tokens = new HashSet<String>();
	Set<Long> matches = new HashSet<Long>();
	boolean online;
	long latestUpdate;
	//Ranking
	int rank;
	int rd;
	
	@SuppressWarnings("unused")
	private Player(){}
	
	public Player(String email, String nickname){
		this.email = email;
		this.nickname = nickname;
		online = true;
		latestUpdate = System.currentTimeMillis();
		rank = Ranking.DEFAULT_RANK;
		rd = Ranking.DEFAULT_RD;
	}
	
	public String getEmail(){
		return email;
	}
	public String getNickname(){
		return nickname;
	}
	public Set<String> getTokens(){
		return tokens;
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
	
	public void addToken(String token){
		tokens.add(token);
	}
	public void addMatches(Long matchId){
		matches.add(matchId);
	}
	public void addMatches(Match match){
		matches.add(match.getMatchId());
	}
	
	public void clearTokens(){
		tokens.clear();
	}
	public void clearMatches(){
		matches.clear();
	}
	
	public boolean removeFromTokens(String token){
		return tokens.remove(token);
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
