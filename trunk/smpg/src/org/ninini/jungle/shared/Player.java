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
	String id;
	Set<String> tokens = new HashSet<String>();
	Set<Long> matches = new HashSet<Long>();
	boolean online;
	
	@SuppressWarnings("unused")
	private Player(){}
	
	public Player(String email, String id, boolean online){
		this.email = email;
		this.id = id;
		this.online = online;
	}
	
	public String getEmail(){
		return email;
	}
	public String getId(){
		return id;
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
	
	public void logIn(){
		online = true;
	}
	public void logOut(){
		online = false;
	}

}
