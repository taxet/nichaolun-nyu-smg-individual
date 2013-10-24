package org.ninini.jungle.server;

import org.ninini.jungle.client.LoginInfo;
import org.ninini.jungle.client.LoginService;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public LoginInfo login(String requestUrl) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		LoginInfo loginInfo = new LoginInfo();
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		
		if(user != null){
			loginInfo.setLoggedIn(true);
			loginInfo.setEmailAddress(user.getEmail());
			loginInfo.setNickname(user.getNickname());
			loginInfo.setLoginUrl(userService.createLoginURL(requestUrl));
			loginInfo.setLogoutUrl(userService.createLogoutURL(requestUrl));
			loginInfo.setToken(channelService.createChannel(loginInfo.getEmailAddress()));
		}else{
			loginInfo.setLoggedIn(false);
			loginInfo.setLoginUrl(userService.createLoginURL(requestUrl));
		}
		return loginInfo;
	}

}
