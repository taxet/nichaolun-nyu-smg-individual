package org.ninini.jungle.shared;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Match implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id Long matchId;
	String redPlayer;//Email
	String blackPlayer;//Email
	String state;//whose turn and winner can be told in this string

	@SuppressWarnings("unused")
	private Match(){}
	
	public Match(Long matchId, String redPlayer, String blackPlayer, String state){
		this.matchId = matchId;
		this.redPlayer = redPlayer;
		this.blackPlayer = blackPlayer;
		this.state = state;
	}
	public Match(Match m){
		this.matchId = m.getMatchId();
		this.redPlayer = m.getRedPlayer();
		this.blackPlayer = m.getBlackPlayer();
		this.state = m.getState();
	}
	
	public Long getMatchId(){
		return matchId;
	}
	public String getRedPlayer(){
		return redPlayer;
	}
	public String getBlackPlayer(){
		return blackPlayer;
	}
	public String getState(){
		return state;
	}
	public char getWhosTurn(){
		return state.toCharArray()[0];
	}
	public char getWinner(){
		return state.toCharArray()[1];
	}
	public boolean ifFinished(){
		return getWinner() == 'r' || getWinner() == 'b';
	}

	public void setRedPlayer(String redPlayer){
		this.redPlayer = redPlayer;
	}
	public void setBlackPlayer(String blackPlayer){
		this.blackPlayer = blackPlayer;
	}
	public void setState(String state){
		this.state = state;
	}
	
	public static String serializeMatch(Match match){
		String delimiter = "~";
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(match.getMatchId());
		stringBuffer.append(delimiter);
		stringBuffer.append(match.getRedPlayer());
		stringBuffer.append(delimiter);
		stringBuffer.append(match.getBlackPlayer());
		stringBuffer.append(delimiter);
		stringBuffer.append(match.getState());
		return stringBuffer.toString();
	}
	
	public static Match unserializeMatch(String match){
		String delimiter = "~";
		String[] msg = match.split(delimiter);
		Long matchId = Long.parseLong(msg[0], 10);
		String redPlayer = msg[1];
		String blackPlayer = msg[2];
		String state = msg[3];
		return new Match(matchId, redPlayer, blackPlayer, state);
	}
}
