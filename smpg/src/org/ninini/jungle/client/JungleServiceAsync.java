package org.ninini.jungle.client;

import java.util.Set;

import org.ninini.jungle.shared.FbInfo;
import org.ninini.jungle.shared.Match;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface JungleServiceAsync {
	void updateState(String state, String userId, Long matchId, AsyncCallback<Void> callback);
	void findingGame(String id, AsyncCallback<Match> callback);
	void findingGameWith(String id, String oppo, AsyncCallback<Match> callback);
	void getMatches(String id, AsyncCallback<Set<Match>> callback);
	void loadMatch(String id, Long matchesId, AsyncCallback<Match> callback);
	void getRank(String userId, AsyncCallback<Integer[]> callback);
	void getRanks(Set<FbInfo> fbInfos, AsyncCallback<Set<FbInfo>> callback);
	void fbPlay(String id, String oppoId, AsyncCallback<Match> callback);
}
