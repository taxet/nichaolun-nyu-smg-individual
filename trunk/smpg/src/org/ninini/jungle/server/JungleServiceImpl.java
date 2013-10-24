package org.ninini.jungle.server;

import java.util.ArrayList;
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
	
	private ArrayList<String> waitIds = new ArrayList<String>();
	private Map<String,String> gamePlaying = new HashMap<String,String>();
	private ChannelService channelService = ChannelServiceFactory.getChannelService();
	//ChannelService channelService = ChannelServiceFactory.getChannelService();

	@Override
	public String updateState(String state, String id) {
		String anotherId = gamePlaying.get(id);
		channelService.sendMessage(new ChannelMessage(id, state));
		channelService.sendMessage(new ChannelMessage(anotherId, state));
		
		//if game is over
		if(state.toCharArray()[1] != '0'){
			gamePlaying.remove(id);
			gamePlaying.remove(anotherId);
		}
		return state;
	}

	@Override
	public String findingGame(String id){
		String match = "";
		if(gamePlaying.containsKey(id)){//id is playing
			
		}else	if(waitIds.isEmpty()){//list is empty
			waitIds.add(id);			
		}else if(!waitIds.contains(id)){//id not in waiting list
			//adding Matches
			match = waitIds.get(0);
			channelService.sendMessage(new ChannelMessage(match, id+"R"));
			channelService.sendMessage(new ChannelMessage(id, match+"B"));
			gamePlaying.put(match, id);
			gamePlaying.put(id, match);
			waitIds.remove(0);
		}
		return match;
	}

}
