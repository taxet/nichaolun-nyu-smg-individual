package org.ninini.jungle.client;

import java.util.HashSet;
import java.util.Set;

import org.ninini.jungle.shared.FbInfo;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.HasRpcToken;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.XsrfToken;
import com.google.gwt.user.client.rpc.XsrfTokenService;
import com.google.gwt.user.client.rpc.XsrfTokenServiceAsync;
import com.gwtfb.client.JSOModel;
import com.gwtfb.sdk.FBCore;

public class JungleEntryPoint implements EntryPoint {

	private LoginInfo loginInfo = new LoginInfo();
	
	//initialize graphics and presenter
	final Graphics graphics = new Graphics();
	final Presenter presenter = new Presenter(graphics);
	
	//Facebook Variables
	private static final String fbApi = "513150252125419";
	private FBCore fbCore ;//= GWT.create(FBCore.class);
	private static final boolean fbStatus = true;
	private static final boolean fbXfbml = true;
	private static final boolean fbCookie = true;
	
	@Override
	public void onModuleLoad() {
		RootPanel.get().add((Graphics)presenter.getView());
		fbCore = GWT.create(FBCore.class);
		

		//initialize loginService
		/*LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				graphics.setStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(LoginInfo result) {
				loginInfo = result;
				if(loginInfo.isLoggedIn()){
					presenter.logIn(loginInfo.getEmailAddress());
					presenter.initMuiltiPlayer(loginInfo);
					graphics.setLogButton(loginInfo.getLogoutUrl());
					graphics.setLoginMessage(Graphics.gameMessage.welcomMsg(loginInfo.getNickname()));
					presenter.getMatchesOfUser();
				}else{
					presenter.logOut();
					graphics.setLogButton(loginInfo.getLoginUrl());
					LoginPanel loginPanel = new LoginPanel(loginInfo.getLoginUrl());
					loginPanel.center();
				}
			}
			
		});
		//online list
		loginService.onlineList(presenter.getUserId(), new AsyncCallback<Set<Player>>() {

			@Override
			public void onFailure(Throwable caught) {
				graphics.setStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(Set<Player> result) {
				graphics.refreshPlayersOnline(result);
			}
			
		});*/
		Cookies.setCookie("JSESSIONID", "JSESSIONID", null, null, "/", false);
		XsrfTokenServiceAsync xsrf = (XsrfTokenServiceAsync)GWT.create(XsrfTokenService.class);
		((ServiceDefTarget)xsrf).setServiceEntryPoint(GWT.getModuleBaseURL() + "xsrf");
		xsrf.getNewXsrfToken(new AsyncCallback<XsrfToken>() {

			@Override
			public void onFailure(Throwable caught) {
				graphics.setStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(final XsrfToken xsrfToken) {
				/*LoginServiceAsync loginServiceAsync = GWT.create(LoginService.class);
				((HasRpcToken) loginServiceAsync).setRpcToken(xsrfToken);
				//login
				loginServiceAsync.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>(){

					@Override
					public void onFailure(Throwable caught) {
						graphics.setStatus(caught.getMessage());						
					}

					@Override
					public void onSuccess(LoginInfo result) {
						loginInfo = result;
						if(loginInfo.isLoggedIn()){
							presenter.logIn(loginInfo.getEmailAddress());
							presenter.initMuiltiPlayer(loginInfo, xsrfToken);
							graphics.setLogButton(loginInfo.getLogoutUrl());
							graphics.setLoginMessage(Graphics.gameMessage.welcomMsg(loginInfo.getNickname()));
							presenter.getMatchesOfUser();
						}else{
							presenter.logOut();
							graphics.setLogButton(loginInfo.getLoginUrl());
							LoginPanel loginPanel = new LoginPanel(loginInfo.getLoginUrl());
							loginPanel.center();
						}
					}
					
				});
				//online list
				loginServiceAsync.onlineList(presenter.getUserId(), new AsyncCallback<Set<Player>>(){

					@Override
					public void onFailure(Throwable caught) {
						graphics.setStatus(caught.getMessage());
					}

					@Override
					public void onSuccess(Set<Player> result) {
						graphics.refreshPlayersOnline(result);
					}
					
				});*/
				

				//facebook login
				fbLogIn(xsrfToken);
			}
			
		});
		
		
		graphics.initDndHandlers();
	}
	
	//facebook login
	private void fbLogIn(final XsrfToken xsrfToken){
		//initialize facebook events
		fbCore.init(fbApi, fbStatus, fbCookie, fbXfbml);
		fbCore.getLoginStatus(new AsyncCallback<JavaScriptObject>(){

			@Override
			public void onFailure(Throwable caught) {
				graphics.setStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(JavaScriptObject result) {
				JSOModel jsm = result.cast();
				String status = jsm.get("status");
				if(status.equals("connected")){
					JSOModel authRes = jsm.getObject("authResponse");
					loginInfo.login();
					loginInfo.setFbId(authRes.get("userID"));
					//get user info
					fbCore.api("/"+loginInfo.getFbId(), new AsyncCallback<JavaScriptObject>(){

						@Override
						public void onFailure(Throwable caught) {
							graphics.setStatus(caught.getMessage());
						}

						@Override
						public void onSuccess(JavaScriptObject result) {
			                JSOModel jsm = result.cast();
			                loginInfo.setName(jsm.get("name"));

			        		//send loginInfo to server
			        		LoginServiceAsync loginServiceAsync = GWT.create(LoginService.class);
			        		((HasRpcToken) loginServiceAsync).setRpcToken(xsrfToken);
			        		//login
			        		loginServiceAsync.fbLogin(loginInfo, new AsyncCallback<String>(){

			        			@Override
			        			public void onFailure(Throwable caught) {
			        				graphics.setStatus(caught.getMessage());
			        			}

			        			@Override
			        			public void onSuccess(String result) {
			        				loginInfo.setToken(result);
			        				presenter.logIn(loginInfo.getFbId());
			        				presenter.initMuiltiPlayer(loginInfo, xsrfToken);
									graphics.setLoginMessage(Graphics.gameMessage.welcomMsg(loginInfo.getName(), loginInfo.getFbId()));
									graphics.setAvatar(loginInfo.getAvatarUrl());
									//presenter.getMatchesOfUser();
									//set log out button
									Button logoutBtn = new Button(Graphics.gameMessage.signOut());
									logoutBtn.addClickHandler(new ClickHandler(){

										@Override
										public void onClick(ClickEvent event) {
											fbCore.logout(new AsyncCallback<JavaScriptObject>(){

												@Override
												public void onFailure(Throwable caught) {
													graphics.setStatus(caught.getMessage());
												}

												@Override
												public void onSuccess(JavaScriptObject result) {
													Window.Location.reload();
												}
												
											});
										}
										
									});
									graphics.setLogoutButton(logoutBtn);
									getFriendList();
			        			}
			        		});
						}
						
					});
					//setLoginInfo(xsrfToken);
					//TODO
					//if is logged in
				}else{//not login
					loginInfo.logout();
					//TODO
					presenter.logOut();
					graphics.setAvatar("");
					LoginPanel loginPanel = new LoginPanel();
					loginPanel.center();
					graphics.setLoginButton();
					presenter.setFriendList(new HashSet<FbInfo>());
				}
			}
		});
	}
	
	//get user info from facebook
	/*private void getUserInfo(){
	}*/
	
	//get friend info form facebook
	private void getFriendList(){
		fbCore.api("/me/friends", new AsyncCallback<JavaScriptObject>(){

			@Override
			public void onFailure(Throwable caught) {
				graphics.setStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(JavaScriptObject result) {
				JSOModel jsm = result.cast();
				final JsArray<JSOModel> jsArray = jsm.getArray("data");
				Set<FbInfo> friends = new HashSet<FbInfo>();
				if(jsArray != null){
					for(int i = 0; i < jsArray.length(); i++){
						JSOModel j = jsArray.get(i).cast();
						final String id = j.get("id");
						final String name = j.get("name");
						FbInfo fbInfo = new FbInfo(id, name);
						friends.add(fbInfo);
					}
				}
				presenter.setFriendList(friends);
			}
			
		});
	}
	//update loginInfo to server in appspot
	/*private void updateLoginInfo(final XsrfToken xsrfToken){

	}*/
	
	//set the loginInfo to graphics and presenter
	/*private void setLoginInfo(final XsrfToken xsrfToken){
		if(loginInfo.isLoggedIn()){
		}
	}*/
}
