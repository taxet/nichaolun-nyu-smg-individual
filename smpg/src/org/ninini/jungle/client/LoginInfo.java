package org.ninini.jungle.client;

import java.io.Serializable;

public class LoginInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String fbId;
	private String name;
	private String token;
	private boolean loggedIn = false;
	//private String loginUrl;
	//private String logoutUrl;
	//private String nickname;
	//private String avatarUrl;
	
	public LoginInfo(){}
	
	public String getFbId(){
		return fbId;
	}
	public String getName(){
		return name;
	}
	public String getToken(){
		return token;
	}
	public String getAvatarUrl(){
		return "http://graph.facebook.com/"+fbId+"/picture";
	}
	public boolean isLoggedIn(){
		return loggedIn;
	}
	
	public void setFbId(String fbId){
		this.fbId = fbId;
	}
	public void setName(String name){
		this.name = name;
	}
	public void setToken(String token){
		this.token = token;
	}
	public void setLoggedIn(boolean loggedIn){
		this.loggedIn = loggedIn;
	}
	public void login(){
		this.loggedIn = true;
	}
	public void logout(){
		this.loggedIn = false;
	}
	/*
	public String getLoginUrl(){
		return loginUrl;
	}
	public String getLogoutUrl(){
		return logoutUrl;
	}
	public String getNickname(){
		return nickname;
	}
	public String getAvatarUrl(){
		return avatarUrl;
	}
	
	
	public void setLoginUrl(String loginUrl){
		this.loginUrl = loginUrl;
	}
	public void setLogoutUrl(String logoutUrl){
		this.logoutUrl = logoutUrl;
	}
	public void setNickname(String nickname){
		this.nickname = nickname;
	}
	public void setAvatarUrl(String avatarUrl){
		this.avatarUrl = avatarUrl;
	}*/

}
