package org.ninini.jungle.client;

import java.util.Set;

import org.ninini.jungle.shared.Player;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {
	/*void login(String requestUri, AsyncCallback<LoginInfo> callback);
	void onlineList(String myEmail, AsyncCallback<Set<Player>> callback);*/
	void fbLogin(LoginInfo fbInfo, AsyncCallback<String> callback);
}
