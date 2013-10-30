package org.ninini.jungle.client;

import java.util.Set;

import org.ninini.jungle.shared.Player;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
/*import com.google.gwt.appengine.channel.client.Channel;
import com.google.gwt.appengine.channel.client.ChannelFactoryImpl;
import com.google.gwt.appengine.channel.client.Socket;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.appengine.channel.client.ChannelError;*/

public class JungleEntryPoint implements EntryPoint {

	private LoginInfo loginInfo = null;
	
	@Override
	public void onModuleLoad() {
		//initialize graphics and presenter
		final Graphics graphics = new Graphics();
		final Presenter presenter = new Presenter(graphics);
		RootPanel.get().add((Graphics)presenter.getView());
		
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
			
		});
		
		//graphics.initDndHandlers();
	}
	
}
