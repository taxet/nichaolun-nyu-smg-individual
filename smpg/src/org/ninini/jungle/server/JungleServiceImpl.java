package org.ninini.jungle.server;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.ninini.jungle.client.JungleService;
import org.ninini.jungle.client.Presenter;
import org.ninini.jungle.shared.Match;
import org.ninini.jungle.shared.Player;
import org.ninini.jungle.shared.State;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Work;

public class JungleServiceImpl  extends RemoteServiceServlet implements JungleService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static{
		ObjectifyService.register(Player.class);
		ObjectifyService.register(Match.class);
	}
	
	private ArrayList<String> waitIds = new ArrayList<String>();
	//private Map<String,String> gamePlaying = new HashMap<String,String>();
	private ChannelService channelService = ChannelServiceFactory.getChannelService();
	//ChannelService channelService = ChannelServiceFactory.getChannelService();

	//sent match message to both player
	private void sendMessage(Match match){
		String id1 = match.getRedPlayer();
		String id2 = match.getBlackPlayer();
		channelService.sendMessage(new ChannelMessage(id1, Match.serializeMatch(match)));
		channelService.sendMessage(new ChannelMessage(id2, Match.serializeMatch(match)));
	}
	
	@Override
	public void updateState(String state, String userId, Long matchId) {
		/*String anotherId = gamePlaying.get(id);
		channelService.sendMessage(new ChannelMessage(id, state));
		channelService.sendMessage(new ChannelMessage(anotherId, state));
		
		//if game is over
		if(state.toCharArray()[1] != '0'){
			gamePlaying.remove(id);
			gamePlaying.remove(anotherId);
		}
		return state;*/
		Match match = ofy().load().type(Match.class).id(matchId).now();
		match.setState(state);
		ofy().save().entities(match);
		sendMessage(match);
	}

	@Override
	public Match findingGame(String id){
		String oppoId = "";
		if(waitIds.isEmpty()){//list is empty
			waitIds.add(id);			
		}else if(!waitIds.contains(id)){//id not in waiting list
			int no = Random.nextInt()%waitIds.size();
			oppoId = waitIds.get(no);//select an opponent randomly
			waitIds.remove(no);
			//load players
			final Player player1 = ofy().load().type(Player.class).id(id).now();
			final Player player2 = ofy().load().type(Player.class).id(oppoId).now();
			//adding match
			Long matchId = System.currentTimeMillis();
			//decide red and black
			String redPlayer, blackPlayer;
			if(Random.nextBoolean()){
				redPlayer = id;
				blackPlayer = oppoId;
			}else{
				redPlayer = oppoId;
				blackPlayer = id;				
			}
			final Match match = new Match(matchId, redPlayer, blackPlayer, Presenter.serializeState(new State()));
			//send msg to both player
			sendMessage(match);
			//add to ofy
			ofy().transact(new Work<Void>(){

				@Override
				public Void run() {
					ofy().save().entities(player1);
					ofy().save().entities(player2);
					ofy().save().entities(match);
					return null;
				}
				
			});
			return match;
		}
		return null;
	}

	@Override
	public Match findingGameWith(String id, String oppo) {
		//load players
		final Player player1 = ofy().load().type(Player.class).id(id).now();
		final Player player2 = ofy().load().type(Player.class).id(oppo).now();
		//adding match
		Long matchId = System.currentTimeMillis();
		//decide red and black
		String redPlayer, blackPlayer;
		if(Random.nextBoolean()){
			redPlayer = id;
			blackPlayer = oppo;
		}else{
			redPlayer = oppo;
			blackPlayer = id;				
		}
		final Match match = new Match(matchId, redPlayer, blackPlayer, Presenter.serializeState(new State()));
		//send msg to both player
		sendMessage(match);
		//add to ofy
		player1.addMatches(matchId);
		player2.addMatches(matchId);
		ofy().transact(new Work<Void>(){

			@Override
			public Void run() {
				ofy().save().entities(player1);
				ofy().save().entities(player2);
				ofy().save().entities(match);
				return null;
			}
			
		});
		
		return match;
	}

	@Override
	public Set<Match> getMatches(String id) {
		Set<Match> matches = new HashSet<Match>();
		Set<Long> matchIds = ofy().load().type(Player.class).id(id).now().getMatches();
		for(Long matchId : matchIds){
			matches.add(ofy().load().type(Match.class).id(matchId).now());
		}
		return matches;
	}

	@Override
	public Match loadMatch(Long matchesId) {
		return ofy().load().type(Match.class).id(matchesId).now();
	}

}
