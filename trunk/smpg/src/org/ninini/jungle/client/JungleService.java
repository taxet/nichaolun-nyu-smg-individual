package org.ninini.jungle.client;

import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import org.ninini.jungle.shared.Match;

@RemoteServiceRelativePath("JungleService")
public interface JungleService extends RemoteService {
	public void updateState(String state, String userId, Long matchId);
	public Match findingGame(String id);
	public Match findingGameWith(String id, String oppo);
	public Set<Match> getMatches(String id);
	public Match loadMatch(Long matchesId);
}
