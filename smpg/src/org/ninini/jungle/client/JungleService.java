package org.ninini.jungle.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("JungleService")
public interface JungleService extends RemoteService {
	public String SubMove(String state,String id);
}
