package org.ninini.jungle.client;

import java.io.Serializable;

public class LoginInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean loggedIn;
	private String loginUrl;
	private String logoutUrl;
	private String emailAddress;
	private String nickname;
	private String token;
	
	public LoginInfo(){
		
	}
	
	public boolean isLoggedIn(){
		return loggedIn;
	}
	public String getLoginUrl(){
		return loginUrl;
	}
	public String getLogoutUrl(){
		return logoutUrl;
	}
	public String getEmailAddress(){
		return emailAddress;
	}
	public String getNickname(){
		return nickname;
	}
	public String getToken(){
		return token;
	}
	
	public void setLoggedIn(boolean loggedIn){
		this.loggedIn = loggedIn;
	}
	public void setLoginUrl(String loginUrl){
		this.loginUrl = loginUrl;
	}
	public void setLogoutUrl(String logoutUrl){
		this.logoutUrl = logoutUrl;
	}
	public void setEmailAddress(String emailAddress){
		this.emailAddress = emailAddress;
	}
	public void setNickname(String nickname){
		this.nickname = nickname;
	}
	public void setToken(String token){
		this.token = token;
	}

}
