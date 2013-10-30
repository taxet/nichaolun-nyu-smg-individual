package org.ninini.jungle.client;

import java.util.Set;

import org.ninini.jungle.shared.Player;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {
	public LoginInfo login(String requestUrl);
	public Set<Player> onlineList(String myEmail);
}
