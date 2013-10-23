package org.ninini.jungle.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface JungleServiceAsync {
	void SubMove(String state, String id, AsyncCallback<String> callback);
}
