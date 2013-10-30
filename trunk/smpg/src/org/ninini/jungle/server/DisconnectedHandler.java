package org.ninini.jungle.server;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ninini.jungle.shared.Player;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelServiceFactory;

public class DisconnectedHandler extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		ChannelPresence channelPresence = channelService.parsePresence(req);
		
		String userId = channelPresence.clientId();
		
		ofy().load().type(Player.class).id(userId).now().disconnect();
	}

}
