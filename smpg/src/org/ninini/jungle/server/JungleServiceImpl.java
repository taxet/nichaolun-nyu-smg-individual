package org.ninini.jungle.server;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.ninini.jungle.client.JungleService;
import org.ninini.jungle.shared.FbInfo;
import org.ninini.jungle.shared.Match;
import org.ninini.jungle.shared.Player;
import org.ninini.jungle.shared.Ranking;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Work;

public class JungleServiceImpl  extends XsrfProtectedServiceServlet implements JungleService {

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
	
	//update rank of two players in a finished match
	private void updateRank(Match match, boolean redWin){
		Long currTime = System.currentTimeMillis();
		Player red = ofy().load().type(Player.class).id(match.getRedPlayer()).now();
		Player black = ofy().load().type(Player.class).id(match.getBlackPlayer()).now();
		if(redWin){
			red.updateRank(black.getRank(), true, currTime);
			black.updateRank(black.getRank(), false, currTime);
		}else{
			red.updateRank(black.getRank(), false, currTime);
			black.updateRank(black.getRank(), true, currTime);		
		}
		red.matchesOver(match);black.matchesOver(match);
		red.update();black.update();
		ofy().save().entity(red);
		ofy().save().entity(black);
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
		//if state is over
		if(state.toCharArray()[1] == 'r') updateRank(match, true);
		if(state.toCharArray()[1] == 'b') updateRank(match, false);
	}

	@Override
	public Match findingGame(String id){
		String oppoId = "";
		if(waitIds.isEmpty()){//list is empty
			waitIds.add(id);			
		}else if(!waitIds.contains(id)){//id not in waiting list
			Random random = new Random(System.currentTimeMillis());
			int no = random.nextInt()%waitIds.size();
			oppoId = waitIds.get(no);//select an opponent randomly
			waitIds.remove(no);
			//load players
			final Player player1 = ofy().load().type(Player.class).id(id).now();
			final Player player2 = ofy().load().type(Player.class).id(oppoId).now();
			//adding match
			Long matchId = System.currentTimeMillis();
			//decide red and black
			String redPlayer = id, blackPlayer = oppoId;
			if(random.nextBoolean()){
				redPlayer = oppoId;
				blackPlayer = id;				
			}
			final Match match = new Match(matchId, redPlayer, blackPlayer, "r0020661571117524622264068000862660");
			//add to ofy
			player1.addMatches(match);
			player2.addMatches(match);
			ofy().transact(new Work<Void>(){

				@Override
				public Void run() {
					ofy().save().entities(player1);
					ofy().save().entities(player2);
					ofy().save().entities(match);
					return null;
				}
				
			});
			//send msg to both player
			sendMessage(match);
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
		String redPlayer = id, blackPlayer = oppo;
		if(new Random(matchId).nextBoolean()){
			redPlayer = oppo;
			blackPlayer = id;				
		}
		final Match match;
		match = new Match(matchId, redPlayer, blackPlayer, "r0020661571117524622264068000862660");
		//add to ofy
		player1.addMatches(match);
		player2.addMatches(match);
		ofy().transact(new Work<Void>(){

			@Override
			public Void run() {
				ofy().save().entities(player1);
				ofy().save().entities(player2);
				ofy().save().entities(match);
				return null;
			}
			
		});
		//send msg to both player
		sendMessage(match);
		
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
	public Match loadMatch(String id, Long matchesId) {
		Match foundMatch = ofy().load().type(Match.class).id(matchesId).now();
		channelService.sendMessage(new ChannelMessage(id, Match.serializeMatch(foundMatch)));
		return foundMatch;
	}

	@Override
	public Integer[] getRank(String userId) {
		Integer[] result = new Integer[2];
		result[0] = Ranking.DEFAULT_RANK;
		result[1] = Ranking.DEFAULT_RD;
		if(ofy().load().type(Player.class).id(userId).now() != null){
			result[0] = ofy().load().type(Player.class).id(userId).now().getRank();
			result[1] = ofy().load().type(Player.class).id(userId).now().getRD();
		}
		return result;
	}

	@Override
	public Set<FbInfo> getRanks(Set<FbInfo> fbInfos) {
		Set<FbInfo> result = new HashSet<FbInfo>();
		for(FbInfo friendInfo : fbInfos){
			FbInfo friend = new FbInfo(friendInfo.getFbId(), friendInfo.getName());
			Integer[] ranks = getRank(friendInfo.getFbId());
			friend.setRankRd(ranks[0], ranks[1]);
			result.add(friend);
		}
		return result;
	}

	@Override
	public Match fbPlay(String id, String oppoId) {
		Player player = ofy().load().type(Player.class).id(id).now();
		Player oppo = ofy().load().type(Player.class).id(oppoId).now();
		if(oppo ==null){// opponent does not have id
			return null;
		}
		if(player.getFriendMatches().containsKey(oppoId)){//load match
			Match match = loadMatch(id, player.getFriendMatches().get("oppoId"));
			return match;
		}else{//new game
			Match match = findingGameWith(id, oppoId);
			return match;
		}
	}

}
