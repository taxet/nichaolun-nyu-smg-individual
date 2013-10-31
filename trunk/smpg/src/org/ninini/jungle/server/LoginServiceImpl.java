package org.ninini.jungle.server;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ninini.jungle.client.LoginInfo;
import org.ninini.jungle.client.LoginService;
import org.ninini.jungle.shared.Player;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.ObjectifyService;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static{
		ObjectifyService.register(Player.class);
	}
	
	//private Set<String> users = new HashSet<String>();
	private ChannelService channelService = ChannelServiceFactory.getChannelService();

	@Override
	public LoginInfo login(String requestUrl) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		LoginInfo loginInfo = new LoginInfo();
		
		if(user != null){
			loginInfo.setLoggedIn(true);
			loginInfo.setEmailAddress(user.getEmail());
			loginInfo.setNickname(user.getNickname());
			loginInfo.setLoginUrl(userService.createLoginURL(requestUrl));
			loginInfo.setLogoutUrl(userService.createLogoutURL(requestUrl));
			loginInfo.setToken(channelService.createChannel(loginInfo.getEmailAddress()));
			//add to userlist
			if (ofy().load().type(Player.class).id(loginInfo.getEmailAddress()).now() == null) {
				Player player = new Player(loginInfo.getEmailAddress(), loginInfo.getNickname());
				ofy().save().entities(player);
			}else{
				Player player = ofy().load().type(Player.class).id(loginInfo.getEmailAddress()).now();
				player.connect();
				ofy().save().entity(player);
			}
		}else{
			loginInfo.setLoggedIn(false);
			loginInfo.setLoginUrl(userService.createLoginURL(requestUrl));
		}
		return loginInfo;
	}

	@Override
	public Set<Player> onlineList(String myEmail) {
		List<Player> playerList = ofy().load().type(Player.class).list();
		Set<Player> onlineList = new HashSet<Player>();
		for(Player p : playerList){
			if(p.isOnline() && !p.getEmail().equals(myEmail))
				onlineList.add(p);
		}
		return onlineList;
	}

}
