package org.ninini.jungle.server;

import java.util.HashMap;
import java.util.Map;

import org.ninini.jungle.client.JungleService;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class JungleServiceImpl  extends RemoteServiceServlet implements JungleService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String wait = "";
	private Map<String, String> hash = new HashMap<String, String>();
	//ChannelService channelService = ChannelServiceFactory.getChannelService();

	@Override
	public String SubMove(String state, String id) {
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		if(hash.containsKey(id)){
			String op = hash.get(id);
			channelService.sendMessage(new ChannelMessage(op, state));
			channelService.sendMessage(new ChannelMessage(id, state));
		}else if(hash.values().contains(id)){
			String op = "";
			for(String key : hash.keySet()){
				if(hash.get(key).equals(id)) op = key;
			}
			channelService.sendMessage(new ChannelMessage(op, state));
			channelService.sendMessage(new ChannelMessage(id, state));
		}else if(wait.equals("")){
			wait = id;
			channelService.sendMessage(new ChannelMessage(id,"Wtttt$$$$RNBQKBNRPPPPPPPP$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$pppppppprnbqkbnr0"));
		}else if(!wait.equals(id)){
			hash.put(wait, id);
			wait="";
			channelService.sendMessage(new ChannelMessage(wait, state));
			channelService.sendMessage(new ChannelMessage(id, state));
		}else channelService.sendMessage(new ChannelMessage(id,"Wtttt$$$$RNBQKBNRPPPPPPPP$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$pppppppprnbqkbnr0"));
		return state;
	}

}
