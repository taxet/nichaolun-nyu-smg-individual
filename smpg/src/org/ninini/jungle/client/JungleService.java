package org.ninini.jungle.client;

import java.util.Set;

import com.google.gwt.user.client.rpc.XsrfProtectedService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import org.ninini.jungle.shared.Match;

@RemoteServiceRelativePath("JungleService")
public interface JungleService extends XsrfProtectedService {
	public void updateState(String state, String userId, Long matchId);
	public Match findingGame(String id);
	public Match findingGameWith(String id, String oppo);
	public Set<Match> getMatches(String id);
	public Match loadMatch(String id, Long matchesId);
	public Integer getRank(String userId);
}
