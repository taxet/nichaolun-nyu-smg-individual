package org.ninini.jungle.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
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
		graphics.initHandlers();
		
		//initialize loginService
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL()+"jungle.html", new AsyncCallback<LoginInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				graphics.setStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(LoginInfo result) {
				loginInfo = result;
				if(loginInfo.isLoggedIn()){
					presenter.setUserId(loginInfo.getEmailAddress());
					Window.alert("welcome "+loginInfo.getEmailAddress()+loginInfo.getToken());
					Socket socket = new ChannelFactoryImpl().createChannel(loginInfo.getToken()).open(new SocketListener(){

						@Override
						public void onOpen() {
							graphics.setStatus("Socket open success.");
						}

						@Override
						public void onMessage(String message) {
							presenter.setState(Presenter.unserializeState(message));
						}

						@Override
						public void onError(ChannelError error) {
							graphics.setStatus("Socket open error.");
							
						}

						@Override
						public void onClose() {
							graphics.setStatus("Socket close.");
						}
						
					});
				}else{
					LoginPanel loginPanel = new LoginPanel(loginInfo.getLoginUrl());
					loginPanel.center();
				}
			}
			
		});
		RootPanel.get().add((Graphics)presenter.getView());
	}

}
