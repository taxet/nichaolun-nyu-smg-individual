package org.ninini.jungle.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface JungleServiceAsync {
	void updateState(String state, String id, AsyncCallback<String> callback);
	void findingGame(String id, AsyncCallback<String> callback);
}
