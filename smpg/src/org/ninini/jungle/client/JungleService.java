package org.ninini.jungle.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("JungleService")
public interface JungleService extends RemoteService {
	public String updateState(String state,String id);
	public String findingGame(String id);
}
