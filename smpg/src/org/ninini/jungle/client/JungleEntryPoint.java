package org.ninini.jungle.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.appengine.channel.client.Channel;
import com.google.gwt.appengine.channel.client.ChannelFactoryImpl;
import com.google.gwt.appengine.channel.client.Socket;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.appengine.channel.client.ChannelError;

public class JungleEntryPoint implements EntryPoint {

	private LoginInfo loginInfo = null;
	
	@Override
	public void onModuleLoad() {
		//initialize graphics and presenter
		final Graphics graphics = new Graphics();
		final Presenter presenter = new Presenter(graphics);
		RootPanel.get().add((Graphics)presenter.getView());
		graphics.initDndHandlers();
		
		//initialize loginService
		LoginServiceAsync loginService = GWT.create(LoginService.class);
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
					graphics.setLoginMessage("Welcome, "+loginInfo.getNickname());
				}else{
					presenter.logOut();
					graphics.setLogButton(loginInfo.getLoginUrl());
				}
			}
			
		});
	}

}
